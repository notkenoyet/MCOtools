package com.mcotools.treatment.rules;

import com.mcotools.treatment.client.McoClient;
import com.mcotools.treatment.dto.BatchDto;
import com.mcotools.treatment.dto.DepositDto;
import com.mcotools.treatment.dto.RequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Applique les regles de traitement communiquees par l'encadrant et
 * construit le rapport (liste d'ActionItem), une methode par regle.
 *
 * Le rapport est PASSIF : il liste les actions a effectuer, il ne les
 * execute pas (les MBean/retryRequest/replayDeposit/batching V1 sont des
 * systemes externes, et le changement d'etat des batch/request n'est pas
 * declenche automatiquement tant que ce n'est pas confirme).
 *
 * Chaque ActionItem porte, dans son champ "note", le texte exact de la
 * procedure communiquee par l'encadrant pour cette regle : c'est ce texte
 * que doit lire la personne qui execute le traitement, sans avoir besoin
 * de consulter un document separe.
 *
 * Chaque regle peut etre restreinte a un seul fichier source (source_file),
 * pour generer un rapport par fichier .txt d'origine (dossier data/).
 * sourceFile == null => comportement inchange, sur toutes les donnees de mco.
 */
@Service
public class TreatmentRulesService {

    private static final Logger log = LoggerFactory.getLogger(TreatmentRulesService.class);

    // Textes de procedure, repris tels quels de la consigne de l'encadrant -
    // affiches dans le champ "note" pour la personne qui execute le traitement.
    private static final String NOTE_CONVERTING_ERROR_26 =
            "Envois en converting_error avec le code error commence par 26 "
                    + "==> Appliquer mbean manualconversion avec les id des envois.";
    private static final String NOTE_CONVERTING_ERROR_AUTRE =
            "Envois en converting_error autre que le premier cas alors "
                    + "==> appliquer retryRequest avec les id des envois.";
    private static final String NOTE_DIGITAL_DIGITOOLS =
            "Envois avec l'etat DIGITAL_CHECK_ERROR ou DIGITOOLS_ERROR : "
                    + "Demande de faire retryRequest sur la liste des REQ_ID.";
    private static final String NOTE_PARSING_ERROR_FLW =
            "Envois en PARSING_ERROR : verifier si l'envoi est FLW "
                    + "-> replayDeposit des req_id.";
    private static final String NOTE_PARSING_ERROR_NON_FLW =
            "Envois en PARSING_ERROR : verifier si l'envoi est FLW, sinon "
                    + "-> retryRequest des req_id.";
    private static final String NOTE_TO_BATCH_ERROR =
            "Envois en TO_BATCH_ERROR : Demande de passer les req_id sur le batching V1.";
    private static final String NOTE_TO_BATCH_REFERENCE =
            "Requete SQL de reference pour statuer sur les envois TO_BATCH ci-dessous "
                    + "(aucun resultat -> etat BATCHED ; des resultats -> etat WAIT) :";
    private static final String SQL_QUERY_TO_BATCH =
            "use mlv_sicl\n"
                    + "select distinct r.req_id, req_current_state, e.* from request r\n"
                    + "inner join fold f on f.req_id = r.req_id\n"
                    + "inner join envelope e on e.env_id = f.env_id\n"
                    + "where r.req_id in ( LIST_REQ_ID )\n"
                    + "and bat_id is null";
    private static final String NOTE_TO_BATCH =
            "Envois avec l'etat TO_BATCH : verifier via la requete SQL ci-dessus "
                    + "si l'envoi peut etre passe a l'etat BATCHED (aucun resultat) ou doit "
                    + "passer a WAIT (des resultats trouves). Le rapport affiche l'etat "
                    + "courant a titre informatif ; la decision finale reste manuelle.";
    private static final String NOTE_BATCH_DIGIPOSTE_SENT =
            "Batch avec ETAB = DIGIPOSTE et etat SENT : "
                    + "Demande de passer le batch a l'etat RECEIVED.";
    private static final String NOTE_DEPOSIT_REJECTED =
            "Deposit avec l'etat Deposit rejected : "
                    + "Demande de faire replayDeposit du deposit.";

    private final McoClient mcoClient;

    public TreatmentRulesService(McoClient mcoClient) {
        this.mcoClient = mcoClient;
    }

    public List<ActionItem> generateReport() {
        return generateReport(null);
    }

    public List<ActionItem> generateReport(String sourceFile) {
        List<ActionItem> report = new ArrayList<>();
        report.addAll(evaluateConvertingError(sourceFile));
        report.addAll(evaluateSimpleRetry(sourceFile));
        report.addAll(evaluateParsingError(sourceFile));
        report.addAll(evaluateToBatchError(sourceFile));
        report.addAll(evaluateToBatch(sourceFile));
        report.addAll(evaluateBatchDigiposte(sourceFile));
        report.addAll(evaluateDepositRejected(sourceFile));
        log.info("Rapport de traitement genere (source={}) : {} ligne(s)", sourceFile, report.size());
        return report;
    }

    private List<RequestDto> requests(String sourceFile) {
        return sourceFile == null ? mcoClient.fetchAllRequests() : mcoClient.fetchRequestsBySourceFile(sourceFile);
    }

    private List<BatchDto> batches(String sourceFile) {
        return sourceFile == null ? mcoClient.fetchAllBatches() : mcoClient.fetchBatchesBySourceFile(sourceFile);
    }

    private List<DepositDto> deposits(String sourceFile) {
        return sourceFile == null ? mcoClient.fetchAllDeposits() : mcoClient.fetchDepositsBySourceFile(sourceFile);
    }

    /** converting_error : code_erreur commencant par "26" -> manualconversion, sinon retryRequest. */
    private List<ActionItem> evaluateConvertingError(String sourceFile) {
        List<ActionItem> items = new ArrayList<>();
        for (RequestDto r : requests(sourceFile)) {
            if (!"converting_error".equals(r.getEtat())) {
                continue;
            }
            String code = r.getCodeErreur();
            if (code != null && code.startsWith("26")) {
                items.add(new ActionItem("request", r.getReqId(), ActionType.MANUAL_CONVERSION,
                        "converting_error, code_erreur=" + code, NOTE_CONVERTING_ERROR_26));
            } else {
                items.add(new ActionItem("request", r.getReqId(), ActionType.RETRY_REQUEST,
                        "converting_error, code_erreur=" + code, NOTE_CONVERTING_ERROR_AUTRE));
            }
        }
        return items;
    }

    /** DIGITAL_CHECK_ERROR / DIGITOOLS_ERROR -> retryRequest. */
    private List<ActionItem> evaluateSimpleRetry(String sourceFile) {
        Set<String> etats = Set.of("DIGITAL_CHECK_ERROR", "DIGITOOLS_ERROR");
        List<ActionItem> items = new ArrayList<>();
        for (RequestDto r : requests(sourceFile)) {
            if (etats.contains(r.getEtat())) {
                items.add(new ActionItem("request", r.getReqId(), ActionType.RETRY_REQUEST,
                        r.getEtat(), NOTE_DIGITAL_DIGITOOLS));
            }
        }
        return items;
    }

    /**
     * PARSING_ERROR : FLW -> replayDeposit, sinon retryRequest.
     *
     * Le champ Request.flow (colonne "flow" de la table request, confirme
     * par l'encadrant le 22/08) porte cette information. Valeur exacte
     * ("FLW") a reconfirmer sur un cas reel en base.
     */
    private List<ActionItem> evaluateParsingError(String sourceFile) {
        List<ActionItem> items = new ArrayList<>();
        for (RequestDto r : requests(sourceFile)) {
            if (!"PARSING_ERROR".equals(r.getEtat())) {
                continue;
            }
            boolean isFlw = "FLW".equalsIgnoreCase(r.getFlow());
            if (isFlw) {
                items.add(new ActionItem("request", r.getReqId(), ActionType.REPLAY_DEPOSIT,
                        "PARSING_ERROR, flow=" + r.getFlow(), NOTE_PARSING_ERROR_FLW));
            } else {
                items.add(new ActionItem("request", r.getReqId(), ActionType.RETRY_REQUEST,
                        "PARSING_ERROR, flow=" + r.getFlow(), NOTE_PARSING_ERROR_NON_FLW));
            }
        }
        return items;
    }

    /** TO_BATCH_ERROR -> routage batching V1. */
    private List<ActionItem> evaluateToBatchError(String sourceFile) {
        List<ActionItem> items = new ArrayList<>();
        for (RequestDto r : requests(sourceFile)) {
            if ("TO_BATCH_ERROR".equals(r.getEtat())) {
                items.add(new ActionItem("request", r.getReqId(), ActionType.BATCHING_V1,
                        "TO_BATCH_ERROR", NOTE_TO_BATCH_ERROR));
            }
        }
        return items;
    }

    /**
     * TO_BATCH : pas de decision automatique BATCHED/WAIT (cf. reunion avec
     * l'encadrant) - on relit l'etat courant de chaque request et on
     * l'affiche simplement dans le rapport, en INFO_ONLY.
     */
    private List<ActionItem> evaluateToBatch(String sourceFile) {
        List<ActionItem> items = new ArrayList<>();
        List<RequestDto> toBatch = requests(sourceFile).stream()
                .filter(r -> "TO_BATCH".equals(r.getEtat()))
                .toList();
        if (toBatch.isEmpty()) {
            return items;
        }
        // Ligne de reference : la requete SQL n'est ecrite qu'une seule fois dans
        // le rapport, les lignes suivantes y renvoient via leur "note".
        items.add(new ActionItem("reference", null, null, NOTE_TO_BATCH_REFERENCE, SQL_QUERY_TO_BATCH));
        for (RequestDto r : toBatch) {
            String currentEtat = mcoClient.fetchRequest(r.getReqId())
                    .map(RequestDto::getEtat)
                    .orElse(r.getEtat());
            items.add(new ActionItem("request", r.getReqId(), ActionType.INFO_ONLY,
                    "TO_BATCH - etat trouve: " + currentEtat, NOTE_TO_BATCH));
        }
        return items;
    }

    /** batch ETAB=DIGIPOSTE + etat=SENT -> passer a l'etat RECEIVED. */
    private List<ActionItem> evaluateBatchDigiposte(String sourceFile) {
        List<ActionItem> items = new ArrayList<>();
        for (BatchDto b : batches(sourceFile)) {
            if ("DIGIPOSTE".equals(b.getEtab()) && "SENT".equals(b.getEtat())) {
                items.add(new ActionItem("batch", b.getBatId(), ActionType.SET_RECEIVED,
                        "DIGIPOSTE/SENT", NOTE_BATCH_DIGIPOSTE_SENT));
            }
        }
        return items;
    }

    /** deposit statut="Deposit rejected" -> replayDeposit. */
    private List<ActionItem> evaluateDepositRejected(String sourceFile) {
        List<ActionItem> items = new ArrayList<>();
        for (DepositDto d : deposits(sourceFile)) {
            if ("Deposit rejected".equals(d.getStatut())) {
                items.add(new ActionItem("deposit", d.getDepositId(), ActionType.REPLAY_DEPOSIT,
                        "Deposit rejected", NOTE_DEPOSIT_REJECTED));
            }
        }
        return items;
    }
}
