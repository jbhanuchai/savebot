package com.savebot.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class BillParsingService {

    // amount patterns: $123.45 or 123.45
    private static final Pattern AMOUNT = Pattern.compile(
            "(?:Total Due|Amount Due|Total|Amount)[:\\s]*\\$?\\s*([0-9]{1,6}(?:,[0-9]{3})*(?:\\.[0-9]{2})?)",
            Pattern.CASE_INSENSITIVE);

    // dates like 2025-10-31, 10/31/2025, Oct 31, 2025
    private static final List<DateTimeFormatter> DATE_FORMATS = List.of(
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy"),
            DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH)
    );
    private static final Pattern DUE_LINE = Pattern.compile(
            "(?:Due Date|Payment Due|Pay By)[:\\s]*([A-Za-z]{3,9}\\s\\d{1,2},\\s\\d{4}|\\d{4}-\\d{2}-\\d{2}|\\d{1,2}/\\d{1,2}/\\d{4})",
            Pattern.CASE_INSENSITIVE);

    // rough provider: first non-empty line that doesn’t look like a label
    public String guessProvider(String text) {
        for (String line : text.split("\\R")) {
            String t = line.trim();
            if (t.isEmpty()) continue;
            if (t.toLowerCase().contains("invoice") || t.toLowerCase().contains("statement")) continue;
            if (t.length() > 2) return t.replaceAll("\\s{2,}", " ");
        }
        return "Unknown";
    }

    public BigDecimal parseAmount(String text) {
        Matcher m = AMOUNT.matcher(text);
        if (m.find()) {
            String amt = m.group(1).replace(",", "");
            try { return new BigDecimal(amt); } catch (Exception ignored) {}
        }
        return null;
    }

    public LocalDate parseDueDate(String text) {
        Matcher m = DUE_LINE.matcher(text);
        if (m.find()) {
            String raw = m.group(1);
            for (var fmt : DATE_FORMATS) {
                try { return LocalDate.parse(raw, fmt); } catch (Exception ignored) {}
            }
        }
        // final fallback: scan for ANY recognizable date
        String[] tokens = text.split("\\R");
        for (String line : tokens) {
            String t = line.trim();
            for (var fmt : DATE_FORMATS) {
                try { return LocalDate.parse(t, fmt); } catch (Exception ignored) {}
            }
        }
        return null;
    }
}
