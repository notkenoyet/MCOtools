package com.mcotools.treatment.controller;

import com.mcotools.treatment.client.McoClient;
import com.mcotools.treatment.dto.BatchDto;
import com.mcotools.treatment.dto.DepositDto;
import com.mcotools.treatment.dto.RequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class TreatmentController {

    private static final Logger log = LoggerFactory.getLogger(TreatmentController.class);

    private final McoClient mcoClient;

    public TreatmentController(McoClient mcoClient) {
        this.mcoClient = mcoClient;
    }

    // ===== Endpoints "liste complete" : {id, codeErreur} pour chaque enregistrement =====

    @GetMapping("/treatementRequest")
    public List<Map<String, Object>> treatementRequest() {
        log.debug("GET /treatementRequest - traitement demande");
        List<RequestDto> all = mcoClient.fetchAllRequests();
        log.info("GET /treatementRequest - {} request(s) traite(s)", all.size());
        return all.stream()
                .map(r -> errorBody(r.getReqId(), r.getCodeErreur()))
                .collect(Collectors.toList());
    }

    @GetMapping("/treatementBatch")
    public List<Map<String, Object>> treatementBatch() {
        log.debug("GET /treatementBatch - traitement demande");
        List<BatchDto> all = mcoClient.fetchAllBatches();
        log.info("GET /treatementBatch - {} batch(es) traite(s)", all.size());
        return all.stream()
                .map(b -> errorBody(b.getBatId(), b.getBatErrorCode()))
                .collect(Collectors.toList());
    }

    @GetMapping("/treatementDeposit")
    public List<Map<String, Object>> treatementDeposit() {
        log.debug("GET /treatementDeposit - traitement demande");
        List<DepositDto> all = mcoClient.fetchAllDeposits();
        log.info("GET /treatementDeposit - {} deposit(s) traite(s)", all.size());
        return all.stream()
                .map(d -> errorBody(d.getDepositId(), d.getCodeErreur()))
                .collect(Collectors.toList());
    }

    // ===== Endpoints "par ID" : un seul {id, codeErreur}, ou 404 si absent =====

    @GetMapping("/treatementRequest/{idRequest}")
    public ResponseEntity<?> treatementRequestById(@PathVariable Long idRequest) {
        log.debug("GET /treatementRequest/{} - traitement demande", idRequest);
        Optional<RequestDto> found = mcoClient.fetchRequest(idRequest);
        if (found.isEmpty()) {
            log.warn("GET /treatementRequest/{} - request introuvable dans mco", idRequest);
            return notFound("Request " + idRequest + " introuvable dans mco");
        }
        String codeErreur = found.get().getCodeErreur();
        log.info("GET /treatementRequest/{} - codeErreur={}", idRequest, codeErreur);
        return ResponseEntity.ok(errorBody(idRequest, codeErreur));
    }

    @GetMapping("/treatementBatch/{idBatch}")
    public ResponseEntity<?> treatementBatchById(@PathVariable Long idBatch) {
        log.debug("GET /treatementBatch/{} - traitement demande", idBatch);
        Optional<BatchDto> found = mcoClient.fetchBatch(idBatch);
        if (found.isEmpty()) {
            log.warn("GET /treatementBatch/{} - batch introuvable dans mco", idBatch);
            return notFound("Batch " + idBatch + " introuvable dans mco");
        }
        String codeErreur = found.get().getBatErrorCode();
        log.info("GET /treatementBatch/{} - codeErreur={}", idBatch, codeErreur);
        return ResponseEntity.ok(errorBody(idBatch, codeErreur));
    }

    @GetMapping("/treatementDeposit/{idDeposit}")
    public ResponseEntity<?> treatementDepositById(@PathVariable String idDeposit) {
        log.debug("GET /treatementDeposit/{} - traitement demande", idDeposit);
        Optional<DepositDto> found = mcoClient.fetchDeposit(idDeposit);
        if (found.isEmpty()) {
            log.warn("GET /treatementDeposit/{} - deposit introuvable dans mco", idDeposit);
            return notFound("Deposit " + idDeposit + " introuvable dans mco");
        }
        String codeErreur = found.get().getCodeErreur();
        log.info("GET /treatementDeposit/{} - codeErreur={}", idDeposit, codeErreur);
        return ResponseEntity.ok(errorBody(idDeposit, codeErreur));
    }

    // ===== Helpers =====

    private Map<String, Object> errorBody(Object id, String codeErreur) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("id", id);
        if (codeErreur == null || codeErreur.isBlank()) {
            body.put("codeErreur", null);
            body.put("message", "Aucune erreur n'existe pour cet enregistrement");
        } else {
            body.put("codeErreur", codeErreur);
        }
        return body;
    }

    private ResponseEntity<Map<String, Object>> notFound(String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Not Found");
        body.put("message", message);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }
}
