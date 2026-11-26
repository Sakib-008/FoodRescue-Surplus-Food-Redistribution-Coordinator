package com.foodrescue.admin.report;

import java.util.List;
import java.util.stream.Collectors;

/** Bridge pattern - Concrete Implementor (the lab's {@code RedColor}): comma-separated values for spreadsheets. */
public class CsvReportFormat implements ReportFormat {

    @Override
    public String render(String title, List<String> columns, List<List<String>> rows) {
        StringBuilder out = new StringBuilder(line(columns));
        rows.forEach(row -> out.append(line(row)));
        return out.toString();
    }

    @Override public String contentType() { return "text/csv"; }
    @Override public String fileExtension() { return "csv"; }

    private String line(List<String> cells) {
        return cells.stream().map(this::escape).collect(Collectors.joining(",")) + "\n";
    }

    private String escape(String value) {
        String v = value == null ? "" : value;
        return v.contains(",") || v.contains("\"") || v.contains("\n") ? "\"" + v.replace("\"", "\"\"") + "\"" : v;
    }
}
