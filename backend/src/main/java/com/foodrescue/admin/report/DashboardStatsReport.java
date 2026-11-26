package com.foodrescue.admin.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Bridge pattern - Refined Abstraction (the lab's {@code Square}): counts of users, donations, requests, pickups. */
public class DashboardStatsReport extends ExportableReport {

    private final Map<String, Object> stats;

    public DashboardStatsReport(Map<String, Object> stats, ReportFormat format) {
        super(format);
        this.stats = stats;
    }

    @Override public String name() { return "statistics"; }
    @Override protected String title() { return "FoodRescue - dashboard statistics"; }
    @Override protected List<String> columns() { return List.of("Group", "Status", "Count"); }

    @Override
    protected List<List<String>> rows() {
        List<List<String>> rows = new ArrayList<>();
        stats.forEach((group, counts) -> ((Map<?, ?>) counts)
                .forEach((status, count) -> rows.add(List.of(group, String.valueOf(status), String.valueOf(count)))));
        return rows;
    }
}
