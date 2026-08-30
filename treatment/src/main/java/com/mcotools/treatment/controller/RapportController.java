package com.mcotools.treatment.controller;

import com.mcotools.treatment.rules.ActionItem;
import com.mcotools.treatment.rules.PdfReportService;
import com.mcotools.treatment.rules.TreatmentRulesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Expose le rapport de traitement des envois/batch/deposit, base sur les
 * regles metier communiquees par l'encadrant (voir TreatmentRulesService),
 * au format JSON ou PDF.
 *
 * Le parametre optionnel sourceFile restreint le rapport aux lignes issues
 * d'un seul fichier .txt d'origine (colonne source_file cote mco) - utilise
 * pour generer un PDF par fichier du dossier data/.
 */
@RestController
public class RapportController {

    private static final Logger log = LoggerFactory.getLogger(RapportController.class);

    private final TreatmentRulesService rulesService;
    private final PdfReportService pdfReportService;

    public RapportController(TreatmentRulesService rulesService, PdfReportService pdfReportService) {
        this.rulesService = rulesService;
        this.pdfReportService = pdfReportService;
    }

    @GetMapping("/rapportTraitement")
    public List<ActionItem> rapportTraitement(@RequestParam(value = "sourceFile", required = false) String sourceFile) {
        log.debug("GET /rapportTraitement - generation du rapport demandee (sourceFile={})", sourceFile);
        List<ActionItem> report = rulesService.generateReport(sourceFile);
        log.info("GET /rapportTraitement - {} ligne(s) retournee(s)", report.size());
        return report;
    }

    @GetMapping("/rapportTraitement/pdf")
    public ResponseEntity<byte[]> rapportTraitementPdf(@RequestParam(value = "sourceFile", required = false) String sourceFile) {
        log.debug("GET /rapportTraitement/pdf - generation du rapport PDF demandee (sourceFile={})", sourceFile);
        List<ActionItem> report = rulesService.generateReport(sourceFile);
        byte[] pdf = pdfReportService.generatePdf(report);
        log.info("GET /rapportTraitement/pdf - PDF genere ({} ligne(s), {} octets)", report.size(), pdf.length);

        String filename = sourceFile != null ? sourceFile.replaceAll("\\.txt$", "") + ".pdf" : "rapport-traitement.pdf";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", filename);

        return ResponseEntity.ok().headers(headers).body(pdf);
    }
}
