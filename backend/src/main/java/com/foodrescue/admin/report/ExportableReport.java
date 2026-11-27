package com.foodrescue.admin.report;

import java.util.List;

/**
 * Bridge pattern - Abstraction (the lab's {@code Shape}). A report knows WHAT to show (title, columns, rows)
 * and holds a reference to a {@link ReportFormat} that knows HOW to write it.
 *
 * <p>Why here: there are two reports and two formats. Without the bridge we would need four classes
 * (RescuedCsv, RescuedText, StatsCsv, StatsText), and every new format or report would multiply them. With the
 * bridge, a new report or a new format is one class, and the two sides change independently.
 */
public abstract class ExportableReport {

    protected final ReportFormat format;

    protected ExportableReport(ReportFormat format) {
        this.format = format;
    }

    public abstract String name();

    protected abstract String title();

    protected abstract List<String> columns();

    protected abstract List<List<String>> rows();

    public String export() {
        return format.render(title(), columns(), rows());
    }

    public String fileName() {
        return "foodrescue-" + name() + "." + format.fileExtension();
    }

    public String contentType() {
        return format.contentType();
    }
}
