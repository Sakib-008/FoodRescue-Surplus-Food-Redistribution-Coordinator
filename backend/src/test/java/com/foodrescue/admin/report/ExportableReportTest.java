package com.foodrescue.admin.report;

import com.foodrescue.admin.service.ReportService.QuantityByUnit;
import com.foodrescue.admin.service.ReportService.RescueReport;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Bridge pattern: the same report (abstraction) written with two different formats (implementors). */
class ExportableReportTest {

    private final RescueReport data = new RescueReport(
            List.of(new QuantityByUnit("kg", new BigDecimal("12.50"))), List.of(), List.of(), 3, 1, 2);

    @Test
    void oneReportCanBeExportedInEveryFormat() {
        ExportableReport csv = new RescuedFoodReport(data, new CsvReportFormat());
        ExportableReport text = new RescuedFoodReport(data, new TextReportFormat());

        assertEquals("foodrescue-rescued-food.csv", csv.fileName());
        assertTrue(csv.export().startsWith("Category,Unit,Quantity\nRescued (delivered),kg,12.5\n"));

        assertEquals("text/plain", text.contentType());
        assertTrue(text.export().startsWith("FoodRescue - food rescued\n"));
        assertTrue(text.export().contains("Completed distributions"));
    }
}
