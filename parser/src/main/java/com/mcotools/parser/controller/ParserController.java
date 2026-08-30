package com.mcotools.parser.controller;

import com.mcotools.parser.service.ParseSummary;
import com.mcotools.parser.service.ParserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/parser")
public class ParserController {

    private static final Logger log = LoggerFactory.getLogger(ParserController.class);

    private final ParserService parserService;

    public ParserController(ParserService parserService) {
        this.parserService = parserService;
    }

    /**
     * Upload one of the raw .txt report dumps directly (multipart/form-data,
     * field name "file"). Parses it and pushes every Request/Batch/Deposit
     * row found to the mco service. Le nom du fichier est enregistre comme
     * source_file sur chaque ligne, pour permettre un rapport filtre par fichier.
     */
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<ParseSummary> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        log.debug("POST /api/parser/upload - fichier recu: {} ({} octets)", file.getOriginalFilename(), file.getSize());
        if (file.isEmpty()) {
            log.warn("POST /api/parser/upload - fichier vide: {}", file.getOriginalFilename());
        }
        try {
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            ParseSummary summary = parserService.parseAndPush(content, file.getOriginalFilename());
            log.info("POST /api/parser/upload - fichier {} traite: {} requests, {} batches, {} deposits, {} erreur(s)",
                    file.getOriginalFilename(), summary.getRequestsParsed(), summary.getBatchesParsed(),
                    summary.getDepositsParsed(), summary.getErrors().size());
            return ResponseEntity.ok(summary);
        } catch (IOException e) {
            log.error("POST /api/parser/upload - lecture du fichier {} impossible: {}", file.getOriginalFilename(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Alternative: paste the raw report text directly in the request body
     * (text/plain) instead of uploading a file. Le parametre optionnel
     * sourceFile permet de retrouver ces lignes plus tard (rapport par fichier).
     */
    @PostMapping(value = "/parse-text", consumes = "text/plain")
    public ResponseEntity<ParseSummary> parseText(@RequestBody String rawContent,
                                                   @RequestParam(value = "sourceFile", required = false) String sourceFile) {
        log.debug("POST /api/parser/parse-text - contenu recu ({} caracteres, source={})",
                rawContent == null ? 0 : rawContent.length(), sourceFile);
        ParseSummary summary = parserService.parseAndPush(rawContent, sourceFile);
        log.info("POST /api/parser/parse-text - traitement termine: {} requests, {} batches, {} deposits, {} erreur(s)",
                summary.getRequestsParsed(), summary.getBatchesParsed(), summary.getDepositsParsed(), summary.getErrors().size());
        return ResponseEntity.ok(summary);
    }
}
