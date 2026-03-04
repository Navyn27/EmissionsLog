package com.navyn.emissionlog.modules.dashboard;

import com.navyn.emissionlog.modules.activities.models.Activity;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.*;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.DirectLandEmissions.AnimalManureAndCompostEmissions;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.DirectLandEmissions.SyntheticFertilizerEmissions;
import com.navyn.emissionlog.modules.agricultureEmissions.models.Livestock.EntericFermentationEmissions;
import com.navyn.emissionlog.modules.LandUseEmissions.models.*;
import com.navyn.emissionlog.modules.mitigationProjects.MitigationDashboardService;
import com.navyn.emissionlog.modules.mitigationProjects.dtos.MitigationDashboardSummaryDto;
import com.navyn.emissionlog.modules.wasteEmissions.models.WasteDataAbstract;
import com.navyn.emissionlog.utils.DashboardData;
import com.navyn.emissionlog.utils.FetchMethods;
import com.navyn.emissionlog.utils.DashboardHelperMethods;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xddf.usermodel.chart.*;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final MitigationDashboardService mitigationDashboardService;
    private final FetchMethods fetchMethods;
    private final DashboardHelperMethods dashboardHelperMethods;

    // GWP factors
    private static final double CH4_GWP = 25.0;
    private static final double N2O_GWP = 298.0;

    @Override
    public DashboardData getMainDashboard(Integer startingYear, Integer endingYear) {
        DashboardData data = new DashboardData();

        // Fetch all data sources
        List<Activity> activities = fetchMethods.fetchActivities(startingYear, endingYear);
        List<WasteDataAbstract> wasteData = fetchMethods.fetchWasteData(startingYear, endingYear);
        List<AquacultureEmissions> aquaculture = fetchMethods.fetchAquaculture(startingYear, endingYear);
        List<EntericFermentationEmissions> enteric = fetchMethods.fetchEntericFermentation(startingYear, endingYear);
        List<LimingEmissions> liming = fetchMethods.fetchLiming(startingYear, endingYear);
        List<AnimalManureAndCompostEmissions> manure = fetchMethods.fetchManure(startingYear, endingYear);
        List<RiceCultivationEmissions> rice = fetchMethods.fetchRice(startingYear, endingYear);
        List<SyntheticFertilizerEmissions> fertilizer = fetchMethods.fetchFertilizer(startingYear, endingYear);
        List<UreaEmissions> urea = fetchMethods.fetchUrea(startingYear, endingYear);
        List<BiomassGain> biomassGains = fetchMethods.fetchBiomassGains(startingYear, endingYear);
        List<DisturbanceBiomassLoss> disturbanceLosses = fetchMethods.fetchDisturbanceLosses(startingYear, endingYear);
        List<FirewoodRemovalBiomassLoss> firewoodLosses = fetchMethods.fetchFirewoodLosses(startingYear, endingYear);
        List<HarvestedBiomassLoss> harvestedLosses = fetchMethods.fetchHarvestedLosses(startingYear, endingYear);
        List<RewettedMineralWetlands> rewettedWetlands = fetchMethods.fetchRewettedWetlands(startingYear, endingYear);

        // aggregate all emissions
        dashboardHelperMethods.aggregateActivityEmissions(data, activities);
        dashboardHelperMethods.aggregateWasteEmissions(data, wasteData);
        dashboardHelperMethods.aggregateAgricultureEmissions(data, aquaculture, enteric, liming, manure, rice,
                fertilizer, urea);
        dashboardHelperMethods.aggregateLandUseEmissions(data, biomassGains, disturbanceLosses, firewoodLosses,
                harvestedLosses, rewettedWetlands);

        // Calculate CO2 equivalent
        dashboardHelperMethods.calculateCO2Equivalent(data);

        // Get mitigation data
        MitigationDashboardSummaryDto mitigationData = mitigationDashboardService
                .getMitigationDashboardSummary(startingYear, endingYear);
        if (mitigationData != null && mitigationData.getTotalMitigationKtCO2e() != null) {
            data.setTotalMitigationKtCO2e(mitigationData.getTotalMitigationKtCO2e());
        }

        // Calculate net emissions (Gross - Mitigation)
        double grossEmissions = data.getTotalCO2EqEmissions(); // in Kt CO2e
        double mitigation = data.getTotalMitigationKtCO2e() != null ? data.getTotalMitigationKtCO2e() : 0.0;
        data.setNetEmissionsKtCO2e(grossEmissions - mitigation);

        // Set date range if specified
        if (startingYear != null && endingYear != null) {
            data.setStartingDate(LocalDateTime.of(startingYear, 1, 1, 0, 0).toString());
            data.setEndingDate(LocalDateTime.of(endingYear, 12, 31, 23, 59).toString());
        }

        return data;
    }

    @Override
    public List<DashboardData> getMainDashboardGraph(Integer startingYear, Integer endingYear) {
        List<DashboardData> dashboardDataList = new ArrayList<>();

        if (startingYear == null || endingYear == null) {
            int currentYear = LocalDateTime.now().getYear();
            startingYear = currentYear - 4;
            endingYear = currentYear;
        }

        for (int year = startingYear; year <= endingYear; year++) {
            DashboardData yearData = getMainDashboard(year, year);
            yearData.setYear(Year.of(year));
            dashboardDataList.add(yearData);
        }

        return dashboardDataList;
    }

    @Override
    public DashboardData getStationaryDashboard(Integer startingYear, Integer endingYear) {
        DashboardData data = new DashboardData();

        // Fetch only stationary activities
        List<Activity> activities = fetchMethods.fetchActivities(startingYear, endingYear);
        List<Activity> stationaryActivities = activities.stream()
                .filter(a -> a.getSector() != null && "STATIONARY".equalsIgnoreCase(a.getSector().name()))
                .toList();

        dashboardHelperMethods.aggregateActivityEmissions(data, stationaryActivities);
        dashboardHelperMethods.calculateCO2Equivalent(data);

        if (startingYear != null && endingYear != null) {
            data.setStartingDate(LocalDateTime.of(startingYear, 1, 1, 0, 0).toString());
            data.setEndingDate(LocalDateTime.of(endingYear, 12, 31, 23, 59).toString());
        }

        return data;
    }

    @Override
    public DashboardData getWasteDashboard(Integer startingYear, Integer endingYear) {
        DashboardData data = new DashboardData();

        List<WasteDataAbstract> wasteData = fetchMethods.fetchWasteData(startingYear, endingYear);
        dashboardHelperMethods.aggregateWasteEmissions(data, wasteData);
        dashboardHelperMethods.calculateCO2Equivalent(data);

        if (startingYear != null && endingYear != null) {
            data.setStartingDate(LocalDateTime.of(startingYear, 1, 1, 0, 0).toString());
            data.setEndingDate(LocalDateTime.of(endingYear, 12, 31, 23, 59).toString());
        }

        return data;
    }

    @Override
    public DashboardData getAgricultureDashboard(Integer startingYear, Integer endingYear) {
        DashboardData data = new DashboardData();

        List<AquacultureEmissions> aquaculture = fetchMethods.fetchAquaculture(startingYear, endingYear);
        List<EntericFermentationEmissions> enteric = fetchMethods.fetchEntericFermentation(startingYear, endingYear);
        List<LimingEmissions> liming = fetchMethods.fetchLiming(startingYear, endingYear);
        List<AnimalManureAndCompostEmissions> manure = fetchMethods.fetchManure(startingYear, endingYear);
        List<RiceCultivationEmissions> rice = fetchMethods.fetchRice(startingYear, endingYear);
        List<SyntheticFertilizerEmissions> fertilizer = fetchMethods.fetchFertilizer(startingYear, endingYear);
        List<UreaEmissions> urea = fetchMethods.fetchUrea(startingYear, endingYear);

        dashboardHelperMethods.aggregateAgricultureEmissions(data, aquaculture, enteric, liming, manure, rice,
                fertilizer, urea);
        dashboardHelperMethods.calculateCO2Equivalent(data);

        if (startingYear != null && endingYear != null) {
            data.setStartingDate(LocalDateTime.of(startingYear, 1, 1, 0, 0).toString());
            data.setEndingDate(LocalDateTime.of(endingYear, 12, 31, 23, 59).toString());
        }

        return data;
    }

    @Override
    public DashboardData getLandUseDashboard(Integer startingYear, Integer endingYear) {
        DashboardData data = new DashboardData();

        List<BiomassGain> biomassGains = fetchMethods.fetchBiomassGains(startingYear, endingYear);
        List<DisturbanceBiomassLoss> disturbanceLosses = fetchMethods.fetchDisturbanceLosses(startingYear, endingYear);
        List<FirewoodRemovalBiomassLoss> firewoodLosses = fetchMethods.fetchFirewoodLosses(startingYear, endingYear);
        List<HarvestedBiomassLoss> harvestedLosses = fetchMethods.fetchHarvestedLosses(startingYear, endingYear);
        List<RewettedMineralWetlands> rewettedWetlands = fetchMethods.fetchRewettedWetlands(startingYear, endingYear);

        dashboardHelperMethods.aggregateLandUseEmissions(data, biomassGains, disturbanceLosses, firewoodLosses,
                harvestedLosses, rewettedWetlands);
        dashboardHelperMethods.calculateCO2Equivalent(data);

        if (startingYear != null && endingYear != null) {
            data.setStartingDate(LocalDateTime.of(startingYear, 1, 1, 0, 0).toString());
            data.setEndingDate(LocalDateTime.of(endingYear, 12, 31, 23, 59).toString());
        }

        return data;
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportMainDashboard(Integer startingYear, Integer endingYear) {
        List<DashboardData> graphData = getMainDashboardGraph(startingYear, endingYear);

        try (XSSFWorkbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            CreationHelper creationHelper = workbook.getCreationHelper();

            // Create styles
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle numberStyle = createNumberStyle(workbook);

            // Create data sheet
            XSSFSheet dataSheet = workbook.createSheet("Data");
            buildDataSheet(dataSheet, titleStyle, headerStyle, dataStyle, numberStyle, graphData, creationHelper);

            // Create chart sheet
            XSSFSheet chartSheet = workbook.createSheet("Chart");
            buildChartSheet(chartSheet, titleStyle, dataSheet, graphData, creationHelper);

            workbook.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate main dashboard export", e);
        }
    }

    private CellStyle createTitleStyle(Workbook workbook) {
        XSSFCellStyle style = (XSSFCellStyle) workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        XSSFCellStyle style = (XSSFCellStyle) workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setWrapText(true);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        XSSFCellStyle style = (XSSFCellStyle) workbook.createCellStyle();
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createNumberStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        DataFormat dataFormat = workbook.createDataFormat();
        style.setDataFormat(dataFormat.getFormat("#,##0.00"));
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }

    private void buildDataSheet(XSSFSheet sheet, CellStyle titleStyle, CellStyle headerStyle,
            CellStyle dataStyle, CellStyle numberStyle,
            List<DashboardData> data, CreationHelper creationHelper) {
        int rowIdx = 0;

        // Title
        Row titleRow = sheet.createRow(rowIdx++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Main Dashboard - Emissions Report");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));
        titleRow.setHeightInPoints(30);

        rowIdx++; // Blank row

        // Headers
        Row headerRow = sheet.createRow(rowIdx++);
        String[] headers = {
                "Year",
                "Total N₂O Emissions (ktCO₂eq)",
                "Total CO₂ Emissions (ktCO₂eq)",
                "Total CH₄ Emissions (ktCO₂eq)",
                "Total CO₂eq Emissions (ktCO₂eq)",
                "Total Mitigation (ktCO₂eq)",
                "Net Emissions (ktCO₂eq)",
                "Land Use Emissions (ktCO₂eq)"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Data rows
        for (DashboardData dto : data) {
            Row row = sheet.createRow(rowIdx++);

            // Year
            Cell yearCell = row.createCell(0);
            yearCell.setCellValue(dto.getYear() != null ? dto.getYear().getValue() : 0);
            yearCell.setCellStyle(dataStyle);

            // Total N2O
            Cell n2oCell = row.createCell(1);
            n2oCell.setCellValue(dto.getTotalN2OEmissions() != null ? dto.getTotalN2OEmissions() : 0.0);
            n2oCell.setCellStyle(numberStyle);

            // Total CO2 (Fossil + Bio)
            Cell co2Cell = row.createCell(2);
            double totalCO2 = (dto.getTotalFossilCO2Emissions() != null ? dto.getTotalFossilCO2Emissions() : 0.0)
                    + (dto.getTotalBioCO2Emissions() != null ? dto.getTotalBioCO2Emissions() : 0.0);
            co2Cell.setCellValue(totalCO2);
            co2Cell.setCellStyle(numberStyle);

            // Total CH4
            Cell ch4Cell = row.createCell(3);
            ch4Cell.setCellValue(dto.getTotalCH4Emissions() != null ? dto.getTotalCH4Emissions() : 0.0);
            ch4Cell.setCellStyle(numberStyle);

            // Total CO2eq
            Cell co2eqCell = row.createCell(4);
            co2eqCell.setCellValue(dto.getTotalCO2EqEmissions() != null ? dto.getTotalCO2EqEmissions() : 0.0);
            co2eqCell.setCellStyle(numberStyle);

            // Total Mitigation
            Cell mitigationCell = row.createCell(5);
            mitigationCell.setCellValue(dto.getTotalMitigationKtCO2e() != null ? dto.getTotalMitigationKtCO2e() : 0.0);
            mitigationCell.setCellStyle(numberStyle);

            // Net Emissions
            Cell netCell = row.createCell(6);
            netCell.setCellValue(dto.getNetEmissionsKtCO2e() != null ? dto.getNetEmissionsKtCO2e() : 0.0);
            netCell.setCellStyle(numberStyle);

            // Land Use Emissions
            Cell landUseCell = row.createCell(7);
            landUseCell.setCellValue(dto.getTotalLandUseEmissions() != null ? dto.getTotalLandUseEmissions() : 0.0);
            landUseCell.setCellStyle(numberStyle);
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
            if (sheet.getColumnWidth(i) > 15000) {
                sheet.setColumnWidth(i, 15000);
            }
        }
    }

    private void buildChartSheet(XSSFSheet chartSheet, CellStyle titleStyle, XSSFSheet dataSheet,
            List<DashboardData> data, CreationHelper creationHelper) {
        int rowIdx = 0;

        // Set column widths
        chartSheet.setColumnWidth(0, 6000);
        chartSheet.setColumnWidth(1, 6000);
        chartSheet.setColumnWidth(2, 6000);

        // Title
        Row titleRow = chartSheet.createRow(rowIdx++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Emissions Trends");
        titleCell.setCellStyle(titleStyle);
        chartSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));
        titleRow.setHeightInPoints(25);

        // Blank row
        chartSheet.createRow(rowIdx++).setHeightInPoints(5);

        // Create chart
        int chartStartRow = rowIdx;
        XSSFDrawing drawing = chartSheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 0, chartStartRow, 10, chartStartRow + 20);
        XSSFChart chart = drawing.createChart(anchor);
        chart.setTitleText("Total Emissions Over Time");
        chart.setTitleOverlay(false);

        XDDFChartLegend legend = chart.getOrAddLegend();
        legend.setPosition(LegendPosition.BOTTOM);

        XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
        bottomAxis.setTitle("Year");
        XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
        leftAxis.setTitle("Emissions (ktCO₂eq)");
        leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

        // Data for chart
        int dataStartRow = 3;
        int dataEndRow = dataStartRow + data.size();

        // Years (X-axis)
        XDDFDataSource<String> years = XDDFDataSourcesFactory.fromStringCellRange(dataSheet,
                new CellRangeAddress(dataStartRow, dataEndRow, 0, 0));

        // Total CO2eq (Y-axis)
        XDDFNumericalDataSource<Double> emissions = XDDFDataSourcesFactory.fromNumericCellRange(dataSheet,
                new CellRangeAddress(dataStartRow, dataEndRow, 4, 4));

        // Net Emissions (Y-axis)
        XDDFNumericalDataSource<Double> netEmissions = XDDFDataSourcesFactory.fromNumericCellRange(dataSheet,
                new CellRangeAddress(dataStartRow, dataEndRow, 6, 6));

        XDDFLineChartData lineChartData = (XDDFLineChartData) chart.createData(ChartTypes.LINE, bottomAxis, leftAxis);

        // Add series
        XDDFLineChartData.Series series1 = (XDDFLineChartData.Series) lineChartData.addSeries(years, emissions);
        series1.setTitle("Total CO₂eq Emissions", null);
        series1.setSmooth(false);
        series1.setMarkerStyle(MarkerStyle.CIRCLE);

        XDDFLineChartData.Series series2 = (XDDFLineChartData.Series) lineChartData.addSeries(years, netEmissions);
        series2.setTitle("Net Emissions", null);
        series2.setSmooth(false);
        series2.setMarkerStyle(MarkerStyle.DIAMOND);

        chart.plot(lineChartData);
    }
}
