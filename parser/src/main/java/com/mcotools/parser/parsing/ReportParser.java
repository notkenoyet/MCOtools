package com.mcotools.parser.parsing;

import com.mcotools.parser.dto.BatchDto;
import com.mcotools.parser.dto.DepositDto;
import com.mcotools.parser.dto.RequestDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Parses the raw MCOtools report dumps (plain text, one or more tables per
 * file) into typed DTOs. Each table in the dump looks like:
 *
 * <pre>
 * Canal   Req_ID      Deposit_ID   ...
 * ------- ----------- ------------ ...
 * WEB     174482787   2561770218M  ...
 *
 * (1 lignes)
 * </pre>
 *
 * The column boundaries are derived from the dashes ("underline") row, so
 * the parser is resilient to header variants across snapshot files as long
 * as {@link FieldMapper} recognizes the column names.
 */
@Component
public class ReportParser {

    private static final Pattern DASH_LINE = Pattern.compile("^\\s*-{2,}(\\s+-{2,})*\\s*$");
    private static final Pattern FOOTER_LINE = Pattern.compile("^\\s*\\(\\d+\\s+lignes?\\)\\s*$", Pattern.CASE_INSENSITIVE);

    private enum TableType { REQUEST, BATCH, DEPOSIT, UNKNOWN }

    public ParsingResult parse(String content) {
        ParsingResult result = new ParsingResult();
        if (content == null || content.isBlank()) {
            return result;
        }

        String[] rawLines = content.split("\\r?\\n", -1);

        for (int i = 1; i < rawLines.length; i++) {
            if (!DASH_LINE.matcher(rawLines[i]).matches()) {
                continue;
            }
            String headerLine = rawLines[i - 1];
            if (headerLine == null || headerLine.isBlank()) {
                continue;
            }

            List<ColumnSpec> columns = buildColumnSpecs(headerLine, rawLines[i]);
            if (columns.isEmpty()) {
                continue;
            }

            TableType tableType = detectTableType(columns);
            if (tableType == TableType.UNKNOWN) {
                continue;
            }

            int j = i + 1;
            while (j < rawLines.length
                    && !rawLines[j].isBlank()
                    && !FOOTER_LINE.matcher(rawLines[j]).matches()) {
                Map<String, String> row = extractRow(columns, rawLines[j]);
                dispatchRow(tableType, row, result);
                j++;
            }

            i = j; // continue scanning after this block
        }

        return result;
    }

    private List<ColumnSpec> buildColumnSpecs(String headerLine, String dashLine) {
        List<ColumnSpec> columns = new ArrayList<>();
        int len = dashLine.length();
        int idx = 0;
        while (idx < len) {
            if (dashLine.charAt(idx) != '-') {
                idx++;
                continue;
            }
            int start = idx;
            while (idx < len && dashLine.charAt(idx) == '-') {
                idx++;
            }
            int end = idx - 1;
            String rawHeader = start < headerLine.length()
                    ? headerLine.substring(start, Math.min(end + 1, headerLine.length())).trim()
                    : "";
            String canonical = FieldMapper.canonicalField(rawHeader);
            if (canonical != null) {
                columns.add(new ColumnSpec(rawHeader, canonical, start, end));
            }
        }
        return columns;
    }

    private TableType detectTableType(List<ColumnSpec> columns) {
        boolean hasReqId = columns.stream().anyMatch(c -> c.getCanonicalField().equals("reqId"));
        boolean hasBatId = columns.stream().anyMatch(c -> c.getCanonicalField().equals("batId"));
        boolean hasPartner = columns.stream().anyMatch(c -> c.getCanonicalField().equals("partner"));
        boolean hasFileName = columns.stream().anyMatch(c -> c.getCanonicalField().equals("fileName"));

        if (hasReqId) {
            return TableType.REQUEST;
        }
        if (hasBatId) {
            return TableType.BATCH;
        }
        if (hasPartner && hasFileName) {
            return TableType.DEPOSIT;
        }
        return TableType.UNKNOWN;
    }

    private Map<String, String> extractRow(List<ColumnSpec> columns, String line) {
        Map<String, String> row = new HashMap<>();
        for (ColumnSpec column : columns) {
            row.put(column.getCanonicalField(), column.extract(line));
        }
        return row;
    }

    private void dispatchRow(TableType tableType, Map<String, String> row, ParsingResult result) {
        switch (tableType) {
            case REQUEST -> result.getRequests().add(toRequestDto(row));
            case BATCH -> result.getBatches().add(toBatchDto(row));
            case DEPOSIT -> result.getDeposits().add(toDepositDto(row));
            default -> { /* ignore */ }
        }
    }

    private RequestDto toRequestDto(Map<String, String> row) {
        RequestDto dto = new RequestDto();
        dto.setReqId(ValueParsers.asLong(row.get("reqId")));
        dto.setDepositId(ValueParsers.asString(row.get("depositId")));
        dto.setCanal(ValueParsers.asString(row.get("canal")));
        dto.setAffranchissement(ValueParsers.asString(row.get("affranchissement")));
        dto.setLogin(ValueParsers.asString(row.get("login")));
        dto.setEtat(ValueParsers.asString(row.get("etat")));
        dto.setNbPlis(ValueParsers.asInteger(row.get("nbPlis")));
        dto.setEnv2Batch(ValueParsers.asString(row.get("env2Batch")));
        dto.setDateModif(ValueParsers.asDateTime(row.get("dateModif")));
        dto.setDateCreation(ValueParsers.asDateTime(row.get("dateCreation")));
        dto.setDateProdEsperee(ValueParsers.asDateTime(row.get("dateProdEsperee")));
        dto.setDeblocageEnvelope(ValueParsers.asDateTime(row.get("deblocageEnvelope")));
        dto.setCodeErreur(ValueParsers.asString(row.get("codeErreur")));
        return dto;
    }

    private BatchDto toBatchDto(Map<String, String> row) {
        BatchDto dto = new BatchDto();
        dto.setBatId(ValueParsers.asLong(row.get("batId")));
        dto.setReference(ValueParsers.asString(row.get("reference")));
        dto.setCanal(ValueParsers.asString(row.get("canal")));
        dto.setType(ValueParsers.asString(row.get("type")));
        dto.setAffranchissement(ValueParsers.asString(row.get("affranchissement")));
        dto.setEtat(ValueParsers.asString(row.get("etat")));
        dto.setEtab(ValueParsers.asString(row.get("etab")));
        dto.setNbEnv(ValueParsers.asInteger(row.get("nbEnv")));
        dto.setNbFeuille(ValueParsers.asInteger(row.get("nbFeuille")));
        dto.setNbPage(ValueParsers.asInteger(row.get("nbPage")));
        dto.setDateModif(ValueParsers.asDateTime(row.get("dateModif")));
        dto.setDateCreation(ValueParsers.asDateTime(row.get("dateCreation")));
        dto.setCategorie(ValueParsers.asString(row.get("categorie")));
        dto.setBatErrorCode(ValueParsers.asString(row.get("batErrorCode")));
        return dto;
    }

    private DepositDto toDepositDto(Map<String, String> row) {
        DepositDto dto = new DepositDto();
        dto.setDepositId(ValueParsers.asString(row.get("depositId")));
        dto.setPartner(ValueParsers.asString(row.get("partner")));
        dto.setFileName(ValueParsers.asString(row.get("fileName")));
        dto.setLogin(ValueParsers.asString(row.get("login")));
        dto.setType(ValueParsers.asString(row.get("type")));
        dto.setProtocol(ValueParsers.asString(row.get("protocol")));
        dto.setStatut(ValueParsers.asString(row.get("statut")));
        dto.setCodeErreur(ValueParsers.asString(row.get("codeErreur")));
        dto.setDateRecept(ValueParsers.asDateTime(row.get("dateRecept")));
        dto.setDateModif(ValueParsers.asDateTime(row.get("dateModif")));
        dto.setCompte(ValueParsers.asString(row.get("compte")));
        return dto;
    }
}
