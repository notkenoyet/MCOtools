package com.mcotools.parser.parsing;


public class ColumnSpec {

    private final String rawHeader;
    private final String canonicalField;
    private final int start;
    private final int end; // inclusive

    public ColumnSpec(String rawHeader, String canonicalField, int start, int end) {
        this.rawHeader = rawHeader;
        this.canonicalField = canonicalField;
        this.start = start;
        this.end = end;
    }

    public String getRawHeader() {
        return rawHeader;
    }

    public String getCanonicalField() {
        return canonicalField;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public String extract(String line) {
        if (line == null || start >= line.length()) {
            return "";
        }
        int safeEnd = Math.min(end + 1, line.length());
        return line.substring(start, safeEnd).trim();
    }
}