package com.mcotools.parser.parsing;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Lenient conversion helpers for the raw strings pulled out of the report
 * dumps. Blank / "NULL" / "-" values become null instead of throwing.
 */
public final class ValueParsers {

    private static final List<DateTimeFormatter> DATE_TIME_FORMATTERS = List.of(
            DateTimeFormatter.ofPattern("MMM d yyyy h:mma", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("MMM d yyyy h:mm a", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("MMM d yyyy HH:mm", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.ENGLISH)
    );

    private ValueParsers() {
    }

    private static boolean isBlankLike(String raw) {
        if (raw == null) {
            return true;
        }
        String trimmed = raw.trim();
        return trimmed.isEmpty() || trimmed.equalsIgnoreCase("NULL") || trimmed.equals("-");
    }

    public static String asString(String raw) {
        return isBlankLike(raw) ? null : raw.trim();
    }

    public static Long asLong(String raw) {
        if (isBlankLike(raw)) {
            return null;
        }
        try {
            return Long.parseLong(raw.trim().replaceAll("[^0-9-]", ""));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Integer asInteger(String raw) {
        if (isBlankLike(raw)) {
            return null;
        }
        try {
            return Integer.parseInt(raw.trim().replaceAll("[^0-9-]", ""));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static LocalDateTime asDateTime(String raw) {
        if (isBlankLike(raw)) {
            return null;
        }
        String normalized = raw.trim().replaceAll("\\s+", " ");
        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                return LocalDateTime.parse(normalized, formatter);
            } catch (Exception ignored) {
                // try the next formatter
            }
        }
        return null;
    }
}
