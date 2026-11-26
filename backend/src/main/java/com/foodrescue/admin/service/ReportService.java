package com.foodrescue.admin.service;

import com.foodrescue.admin.report.CsvReportFormat;
import com.foodrescue.admin.report.DashboardStatsReport;
import com.foodrescue.admin.report.ExportableReport;
import com.foodrescue.admin.report.ReportFormat;
import com.foodrescue.admin.report.RescuedFoodReport;
import com.foodrescue.admin.report.TextReportFormat;
import com.foodrescue.common.exception.ApiException;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Reporting reads the shared schema with plain SQL (portable across PostgreSQL and H2), so Member 1 does not
 * depend on Member 2/3 Java classes. Table/column names are constants, never user input.
 */
@Service
@RequiredArgsConstructor
public class ReportService {

    private static final List<String> ROLES = List.of("ADMIN", "DONOR", "RECIPIENT", "VOLUNTEER");
    private static final List<String> DONATION_STATUSES = List.of("AVAILABLE", "RESERVED", "COMPLETED", "EXPIRED", "CANCELLED");
    private static final List<String> REQUEST_STATUSES = List.of("PENDING", "ACCEPTED", "REJECTED", "CANCELLED", "COMPLETED");
    private static final List<String> PICKUP_STATUSES = List.of("OPEN", "ASSIGNED", "PICKED_UP", "DELIVERED", "CANCELLED");

    public record QuantityByUnit(String unit, BigDecimal quantity) { }

    public record RescueReport(List<QuantityByUnit> rescued, List<QuantityByUnit> inTransit,
                               List<QuantityByUnit> expiredUnclaimed, long completedDistributions,
                               long cancelledDistributions, long expiredDonations) { }

    private final JdbcTemplate jdbc;

    @Transactional(readOnly = true)
    public Map<String, Object> dashboardStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("users", groupCount("users", "role", ROLES));
        stats.put("donations", groupCount("donations", "status", DONATION_STATUSES));
        stats.put("requests", groupCount("donation_requests", "status", REQUEST_STATUSES));
        stats.put("pickups", groupCount("pickup_tasks", "status", PICKUP_STATUSES));
        return stats;
    }

    @Transactional(readOnly = true)
    public RescueReport rescueReport() {
        String byRequestStatus = "SELECT d.unit, SUM(r.quantity_requested) FROM donation_requests r "
                + "JOIN donations d ON d.id = r.donation_id WHERE r.status = ? GROUP BY d.unit ORDER BY d.unit";
        List<QuantityByUnit> rescued = jdbc.query(byRequestStatus, (rs, i) ->
                new QuantityByUnit(rs.getString(1), rs.getBigDecimal(2)), "COMPLETED");
        List<QuantityByUnit> inTransit = jdbc.query(byRequestStatus, (rs, i) ->
                new QuantityByUnit(rs.getString(1), rs.getBigDecimal(2)), "ACCEPTED");
        List<QuantityByUnit> expired = jdbc.query(
                "SELECT unit, SUM(quantity_available) FROM donations WHERE status = 'EXPIRED' GROUP BY unit ORDER BY unit",
                (rs, i) -> new QuantityByUnit(rs.getString(1), rs.getBigDecimal(2)));
        long completed = count("SELECT COUNT(*) FROM donation_requests WHERE status = 'COMPLETED'");
        long cancelled = count("SELECT COUNT(*) FROM donation_requests WHERE status = 'CANCELLED'");
        long expiredCount = count("SELECT COUNT(*) FROM donations WHERE status = 'EXPIRED'");
        return new RescueReport(rescued, inTransit, expired, completed, cancelled, expiredCount);
    }

    /**
     * Pairs a report (Bridge abstraction) with an output format (Bridge implementor) chosen at run time,
     * e.g. ("rescued-food", "csv") or ("statistics", "txt").
     */
    @Transactional(readOnly = true)
    public ExportableReport export(String report, String formatName) {
        ReportFormat format = switch (formatName == null ? "" : formatName.toLowerCase()) {
            case "csv" -> new CsvReportFormat();
            case "txt" -> new TextReportFormat();
            default -> throw ApiException.badRequest("UNKNOWN_FORMAT", "Format must be csv or txt");
        };
        return switch (report) {
            case "rescued-food" -> new RescuedFoodReport(rescueReport(), format);
            case "statistics" -> new DashboardStatsReport(dashboardStats(), format);
            default -> throw ApiException.badRequest("UNKNOWN_REPORT", "Report must be rescued-food or statistics");
        };
    }

    private Map<String, Long> groupCount(String table, String column, List<String> keys) {
        Map<String, Long> result = new LinkedHashMap<>();
        keys.forEach(k -> result.put(k, 0L));
        jdbc.query("SELECT " + column + ", COUNT(*) FROM " + table + " GROUP BY " + column,
                rs -> { result.put(rs.getString(1), rs.getLong(2)); });
        return result;
    }

    private long count(String sql) {
        Long value = jdbc.queryForObject(sql, Long.class);
        return value == null ? 0 : value;
    }
}
