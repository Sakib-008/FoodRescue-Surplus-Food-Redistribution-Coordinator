package com.foodrescue.admin.report;

import java.util.List;

/** Bridge pattern - Implementor (the lab's {@code Color}): how a table of values is written out. */
public interface ReportFormat {
    String render(String title, List<String> columns, List<List<String>> rows);

    String contentType();

    String fileExtension();
}
