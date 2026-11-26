package com.foodrescue.admin.report;

import com.foodrescue.admin.service.ReportService.QuantityByUnit;
import com.foodrescue.admin.service.ReportService.RescueReport;
import java.util.ArrayList;
import java.util.List;

/** Bridge pattern - Refined Abstraction (the lab's {@code Circle}): food rescued, in transit and expired, per unit. */
public class RescuedFoodReport extends ExportableReport {

    private final RescueReport data;

    public RescuedFoodReport(RescueReport data, ReportFormat format) {
        super(format);
        this.data = data;
    }

    @Override public String name() { return "rescued-food"; }
    @Override protected String title() { return "FoodRescue - food rescued"; }
    @Override protected List<String> columns() { return List.of("Category", "Unit", "Quantity"); }

    @Override
    protected List<List<String>> rows() {
        List<List<String>> rows = new ArrayList<>();
        add(rows, "Rescued (delivered)", data.rescued());
        add(rows, "In transit (accepted)", data.inTransit());
        add(rows, "Expired unclaimed", data.expiredUnclaimed());
        rows.add(List.of("Completed distributions", "", String.valueOf(data.completedDistributions())));
        rows.add(List.of("Cancelled distributions", "", String.valueOf(data.cancelledDistributions())));
        rows.add(List.of("Expired donations", "", String.valueOf(data.expiredDonations())));
        return rows;
    }

    private void add(List<List<String>> rows, String category, List<QuantityByUnit> quantities) {
        quantities.forEach(q -> rows.add(List.of(category, q.unit(), q.quantity().stripTrailingZeros().toPlainString())));
    }
}
