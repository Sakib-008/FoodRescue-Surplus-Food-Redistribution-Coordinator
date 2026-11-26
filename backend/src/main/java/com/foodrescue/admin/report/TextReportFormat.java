package com.foodrescue.admin.report;

import java.util.List;

/** Bridge pattern - Concrete Implementor (the lab's {@code BlueColor}): a fixed-width table for printing. */
public class TextReportFormat implements ReportFormat {

    @Override
    public String render(String title, List<String> columns, List<List<String>> rows) {
        int[] width = new int[columns.size()];
        for (int i = 0; i < columns.size(); i++) {
            width[i] = columns.get(i).length();
            for (List<String> row : rows) {
                width[i] = Math.max(width[i], row.get(i).length());
            }
        }
        StringBuilder out = new StringBuilder(title).append('\n').append("=".repeat(title.length())).append("\n\n");
        out.append(line(columns, width));
        int total = 0;
        for (int w : width) {
            total += w + 2;
        }
        out.append("-".repeat(Math.max(0, total - 2))).append('\n');
        rows.forEach(row -> out.append(line(row, width)));
        return out.toString();
    }

    @Override public String contentType() { return "text/plain"; }
    @Override public String fileExtension() { return "txt"; }

    private String line(List<String> cells, int[] width) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cells.size(); i++) {
            sb.append(String.format("%-" + width[i] + "s", cells.get(i)));
            if (i < cells.size() - 1) {
                sb.append("  ");
            }
        }
        return sb.toString().stripTrailing() + "\n";
    }
}
