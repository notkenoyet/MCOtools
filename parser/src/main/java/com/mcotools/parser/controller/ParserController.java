package com.mcotools.parser.controller;

import com.mcotools.parser.service.ParseSummary;
import com.mcotools.parser.service.ParserService;
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

    private final ParserService parserService;

    public ParserController(ParserService parserService) {
        this.parserService = parserService;
    }

    /**
     * Upload one of the raw .txt report dumps directly (multipart/form-data,
     * field name "file"). Parses it and pushes every Request/Batch/Deposit
     * row found to the mco service.
     */
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<ParseSummary> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String content = new String(file.getBytes(), StandardCharsets.UTF_8);
        return ResponseEntity.ok(parserService.parseAndPush(content));
    }

    /**
     * Alternative: paste the raw report text directly in the request body
     * (text/plain) instead of uploading a file.
     */
    @PostMapping(value = "/parse-text", consumes = "text/plain")
    public ResponseEntity<ParseSummary> parseText(@RequestBody String rawContent) {
        return ResponseEntity.ok(parserService.parseAndPush(rawContent));
    }
}
