package com.mcotools.parser.service;
import com.mcotools.parser.client.McoClient;
import com.mcotools.parser.dto.BatchDto;
import com.mcotools.parser.dto.DepositDto;
import com.mcotools.parser.dto.RequestDto;
import com.mcotools.parser.parsing.ParsingResult;
import com.mcotools.parser.parsing.ReportParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ParserService {

    private static final Logger log = LoggerFactory.getLogger(ParserService.class);

    private final ReportParser reportParser;
    private final McoClient mcoClient;

    public ParserService(ReportParser reportParser, McoClient mcoClient) {
        this.reportParser = reportParser;
        this.mcoClient = mcoClient;
    }

    /**
     * Parses the raw report text into Request/Batch/Deposit rows, then
     * pushes each row to the mco service via REST. A failure on one row
     * (e.g. mco service down, duplicate id) is recorded in the summary
     * but does not stop the rest of the batch.
     *
     * @param sourceFile nom du fichier .txt d'origine (pour la tracabilite /
     *                   le filtrage par fichier cote treatment) ; peut etre null.
     */
    public ParseSummary parseAndPush(String rawContent, String sourceFile) {
        log.debug("Debut du parsing du rapport ({} caracteres, source={})",
                rawContent == null ? 0 : rawContent.length(), sourceFile);
        ParsingResult parsed = reportParser.parse(rawContent);
        ParseSummary summary = new ParseSummary();

        summary.setRequestsParsed(parsed.getRequests().size());
        summary.setBatchesParsed(parsed.getBatches().size());
        summary.setDepositsParsed(parsed.getDeposits().size());
        log.info("Parsing termine: {} request(s), {} batch(es), {} deposit(s) detecte(s)",
                parsed.getRequests().size(), parsed.getBatches().size(), parsed.getDeposits().size());

        for (RequestDto request : parsed.getRequests()) {
            request.setSourceFile(sourceFile);
            try {
                mcoClient.pushRequest(request);
                summary.setRequestsPushed(summary.getRequestsPushed() + 1);
                log.debug("Request {} pousse vers mco avec succes", request.getReqId());
            } catch (Exception e) {
                log.warn("Echec de l'envoi du request {} vers mco: {}", request.getReqId(), e.getMessage());
                summary.getErrors().add("Request " + request.getReqId() + ": " + e.getMessage());
            }
        }

        for (BatchDto batch : parsed.getBatches()) {
            batch.setSourceFile(sourceFile);
            try {
                mcoClient.pushBatch(batch);
                summary.setBatchesPushed(summary.getBatchesPushed() + 1);
                log.debug("Batch {} pousse vers mco avec succes", batch.getBatId());
            } catch (Exception e) {
                log.warn("Echec de l'envoi du batch {} vers mco: {}", batch.getBatId(), e.getMessage());
                summary.getErrors().add("Batch " + batch.getBatId() + ": " + e.getMessage());
            }
        }

        for (DepositDto deposit : parsed.getDeposits()) {
            deposit.setSourceFile(sourceFile);
            try {
                mcoClient.pushDeposit(deposit);
                summary.setDepositsPushed(summary.getDepositsPushed() + 1);
                log.debug("Deposit {} pousse vers mco avec succes", deposit.getDepositId());
            } catch (Exception e) {
                log.warn("Echec de l'envoi du deposit {} vers mco: {}", deposit.getDepositId(), e.getMessage());
                summary.getErrors().add("Deposit " + deposit.getDepositId() + ": " + e.getMessage());
            }
        }

        if (!summary.getErrors().isEmpty()) {
            log.error("Traitement du rapport termine avec {} erreur(s): {}", summary.getErrors().size(), summary.getErrors());
        } else {
            log.info("Traitement du rapport termine sans erreur: {} requests, {} batches, {} deposits envoyes",
                    summary.getRequestsPushed(), summary.getBatchesPushed(), summary.getDepositsPushed());
        }

        return summary;
    }
}
