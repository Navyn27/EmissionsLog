package com.navyn.emissionlog.modules.transportScenarios.dashboard;

import com.navyn.emissionlog.modules.transportScenarios.dashboard.dtos.TransportScenarioDashboardSummaryDto;
import com.navyn.emissionlog.modules.transportScenarios.dashboard.dtos.TransportScenarioDashboardYearDto;
import com.navyn.emissionlog.modules.transportScenarios.modalShift.models.ModalShiftMitigation;
import com.navyn.emissionlog.modules.transportScenarios.modalShift.repository.ModalShiftMitigationRepository;
import com.navyn.emissionlog.modules.transportScenarios.ElectricVehicle.models.ElectricVehicleMitigation;
import com.navyn.emissionlog.modules.transportScenarios.ElectricVehicle.repository.ElectricVehicleMitigationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xddf.usermodel.chart.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class TransportScenarioDashboardServiceImpl implements TransportScenarioDashboardService {

    private static final double GCO2E_TO_KCO2E = 1000.0; // Conversion factor: GgCO2e to ktCO2e

    private final ModalShiftMitigationRepository modalShiftMitigationRepository;
    private final ElectricVehicleMitigationRepository electricVehicleMitigationRepository;

    /**
     * Helper method to filter by year range
     */
    private <T> List<T> filterByYear(List<T> list, Function<T, Integer> yearExtractor, int startYear, int endYear) {
        return list.stream()
                .filter(item -> {
                    Integer year = yearExtractor.apply(item);
                    return year != null && year >= startYear && year <= endYear;
                })
                .collect(Collectors.toList());
    }

    /**
     * Helper method to sum double values from a list
     */
    private <T> double sumDouble(List<T> list, Function<T, Double> valueExtractor) {
        return list.stream()
                .map(valueExtractor)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    /**
     * Convert GgCO2e to ktCO2e
     */
    private double convertGCO2eToKtCO2e(double gCO2e) {
        return gCO2e * GCO2E_TO_KCO2E;
    }

    /**
     * Calculate Modal Shift mitigation and BAU for a year
     */
    private double calculateModalShiftMitigationForYear(int year,
                                                        Map<Integer, List<ModalShiftMitigation>> modalShiftByYear) {
        return sumDouble(modalShiftByYear.getOrDefault(year, List.of()),
                m -> m.getTotalProjectEmission() != null ? convertGCO2eToKtCO2e(m.getTotalProjectEmission()) : 0.0);
    }

    private double calculateModalShiftBAUForYear(int year,
                                                 Map<Integer, List<ModalShiftMitigation>> modalShiftByYear) {
        return sumDouble(modalShiftByYear.getOrDefault(year, List.of()),
                m -> m.getBauOfShift() != null ? convertGCO2eToKtCO2e(m.getBauOfShift()) : 0.0);
    }

    /**
     * Calculate Electric Vehicle mitigation and BAU for a year
     */
    private double calculateElectricVehicleMitigationForYear(int year,
                                                            Map<Integer, List<ElectricVehicleMitigation>> electricVehicleByYear) {
        return sumDouble(electricVehicleByYear.getOrDefault(year, List.of()),
                e -> e.getTotalProjectEmission() != null ? convertGCO2eToKtCO2e(e.getTotalProjectEmission()) : 0.0);
    }

    private double calculateElectricVehicleBAUForYear(int year,
                                                      Map<Integer, List<ElectricVehicleMitigation>> electricVehicleByYear) {
        return sumDouble(electricVehicleByYear.getOrDefault(year, List.of()),
                e -> e.getBau() != null ? convertGCO2eToKtCO2e(e.getBau()) : 0.0);
    }

    @Override
    @Transactional(readOnly = true)
    public TransportScenarioDashboardSummaryDto getTransportScenarioDashboardSummary(Integer startingYear, Integer endingYear) {
        // Fetch all data
        List<ModalShiftMitigation> modalShift = modalShiftMitigationRepository.findAll();
        List<ElectricVehicleMitigation> electricVehicle = electricVehicleMitigationRepository.findAll();

        // Filter by year range if specified
        if (startingYear != null && endingYear != null) {
            modalShift = filterByYear(modalShift, ModalShiftMitigation::getYear, startingYear, endingYear);
            electricVehicle = filterByYear(electricVehicle, ElectricVehicleMitigation::getYear, startingYear, endingYear);
        }

        // Aggregate Modal Shift
        double modalShiftMitigationTotal = sumDouble(modalShift,
                m -> m.getTotalProjectEmission() != null ? convertGCO2eToKtCO2e(m.getTotalProjectEmission()) : 0.0);
        double modalShiftBAUTotal = sumDouble(modalShift,
                m -> m.getBauOfShift() != null ? convertGCO2eToKtCO2e(m.getBauOfShift()) : 0.0);

        // Aggregate Electric Vehicle
        double electricVehicleMitigationTotal = sumDouble(electricVehicle,
                e -> e.getTotalProjectEmission() != null ? convertGCO2eToKtCO2e(e.getTotalProjectEmission()) : 0.0);
        double electricVehicleBAUTotal = sumDouble(electricVehicle,
                e -> e.getBau() != null ? convertGCO2eToKtCO2e(e.getBau()) : 0.0);

        // Calculate totals
        double totalBAU = modalShiftBAUTotal + electricVehicleBAUTotal;
        double totalMitigation = modalShiftMitigationTotal + electricVehicleMitigationTotal;
        double netEmissions = totalBAU - totalMitigation;
        double percentageReduction = totalBAU > 0 ? (totalMitigation / totalBAU) * 100 : 0.0;

        TransportScenarioDashboardSummaryDto dto = new TransportScenarioDashboardSummaryDto();
        dto.setStartingYear(startingYear);
        dto.setEndingYear(endingYear);
        dto.setTotalBAUKtCO2e(totalBAU);
        dto.setTotalModalShiftMitigationKtCO2e(modalShiftMitigationTotal);
        dto.setTotalElectricVehicleMitigationKtCO2e(electricVehicleMitigationTotal);
        dto.setTotalMitigationKtCO2e(totalMitigation);
        dto.setNetEmissionsKtCO2e(netEmissions);
        dto.setPercentageReduction(percentageReduction);

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransportScenarioDashboardYearDto> getTransportScenarioDashboardGraph(Integer startingYear, Integer endingYear) {
        int currentYear = LocalDateTime.now().getYear();
        int start = startingYear != null ? startingYear : currentYear - 4;
        int end = endingYear != null ? endingYear : currentYear;

        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }

        // Fetch all data
        List<ModalShiftMitigation> modalShift = modalShiftMitigationRepository.findAll();
        List<ElectricVehicleMitigation> electricVehicle = electricVehicleMitigationRepository.findAll();

        // Group by year
        Map<Integer, List<ModalShiftMitigation>> modalShiftByYear = modalShift.stream()
                .collect(Collectors.groupingBy(ModalShiftMitigation::getYear));
        Map<Integer, List<ElectricVehicleMitigation>> electricVehicleByYear = electricVehicle.stream()
                .collect(Collectors.groupingBy(ElectricVehicleMitigation::getYear));

        // Create dashboard data for each year
        List<TransportScenarioDashboardYearDto> dashboardDataList = new ArrayList<>();
        for (int year = start; year <= end; year++) {
            double modalShiftMitigation = calculateModalShiftMitigationForYear(year, modalShiftByYear);
            double modalShiftBAU = calculateModalShiftBAUForYear(year, modalShiftByYear);
            double electricVehicleMitigation = calculateElectricVehicleMitigationForYear(year, electricVehicleByYear);
            double electricVehicleBAU = calculateElectricVehicleBAUForYear(year, electricVehicleByYear);

            double totalBAU = modalShiftBAU + electricVehicleBAU;
            double totalMitigation = modalShiftMitigation + electricVehicleMitigation;
            double netEmissions = totalBAU - totalMitigation;

            TransportScenarioDashboardYearDto dto = new TransportScenarioDashboardYearDto();
            dto.setYear(year);
            dto.setBauScenarioKtCO2e(totalBAU);
            dto.setModalShiftMitigationKtCO2e(modalShiftMitigation);
            dto.setElectricVehicleMitigationKtCO2e(electricVehicleMitigation);
            dto.setTotalMitigationKtCO2e(totalMitigation);
            dto.setNetEmissionsKtCO2e(netEmissions);

            dashboardDataList.add(dto);
        }

        return dashboardDataList;
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportTransportScenarioDashboard(Integer startingYear, Integer endingYear) {
        List<TransportScenarioDashboardYearDto> graphData = getTransportScenarioDashboardGraph(startingYear, endingYear);

        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            CreationHelper creationHelper = workbook.getCreationHelper();

            // Create styles
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle numberStyle = createNumberStyle(workbook);
            CellStyle bauStyle = createBAUStyle(workbook);
            CellStyle mitigationStyle = createMitigationStyle(workbook);
            CellStyle totalStyle = createTotalStyle(workbook);

            // Create data sheet
            XSSFSheet dataSheet = workbook.createSheet("Data");
            buildDataSheet(dataSheet, titleStyle, headerStyle, dataStyle, numberStyle,
                    bauStyle, mitigationStyle, totalStyle, graphData, creationHelper);

            // Create chart sheet
            XSSFSheet chartSheet = workbook.createSheet("Chart");
            buildChartSheet(chartSheet, titleStyle, dataSheet, graphData, creationHelper);

            workbook.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate transport scenario dashboard export", e);
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

    private CellStyle createBAUStyle(Workbook workbook) {
        XSSFCellStyle style = (XSSFCellStyle) createNumberStyle(workbook);
        style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle createMitigationStyle(Workbook workbook) {
        XSSFCellStyle style = (XSSFCellStyle) createNumberStyle(workbook);
        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle createTotalStyle(Workbook workbook) {
        XSSFCellStyle style = (XSSFCellStyle) createNumberStyle(workbook);
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.PINK.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private void buildDataSheet(XSSFSheet sheet, CellStyle titleStyle, CellStyle headerStyle,
                                CellStyle dataStyle, CellStyle numberStyle,
                                CellStyle bauStyle, CellStyle mitigationStyle, CellStyle totalStyle,
                                List<TransportScenarioDashboardYearDto> data, CreationHelper creationHelper) {
        int rowIdx = 0;

        // Title
        Row titleRow = sheet.createRow(rowIdx++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Transport Scenario Dashboard - BAU vs Mitigation");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));
        titleRow.setHeightInPoints(30);

        rowIdx++; // Blank row

        // Headers
        Row headerRow = sheet.createRow(rowIdx++);
        String[] headers = {
                "Year",
                "BAU Scenario (ktCO2eq)",
                "Modal Shift Mitigation (ktCO2eq)",
                "Electric Vehicle Mitigation (ktCO2eq)",
                "Total Mitigation (ktCO2eq)",
                "Net Emissions (ktCO2eq)"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Data rows
        for (TransportScenarioDashboardYearDto dto : data) {
            Row row = sheet.createRow(rowIdx++);

            // Year
            Cell yearCell = row.createCell(0);
            yearCell.setCellValue(dto.getYear());
            yearCell.setCellStyle(dataStyle);

            // BAU Scenario (green)
            Cell bauCell = row.createCell(1);
            bauCell.setCellValue(dto.getBauScenarioKtCO2e() != null ? dto.getBauScenarioKtCO2e() : 0.0);
            bauCell.setCellStyle(bauStyle);

            // Modal Shift Mitigation (yellow)
            Cell modalShiftCell = row.createCell(2);
            modalShiftCell.setCellValue(dto.getModalShiftMitigationKtCO2e() != null ? dto.getModalShiftMitigationKtCO2e() : 0.0);
            modalShiftCell.setCellStyle(mitigationStyle);

            // Electric Vehicle Mitigation (yellow)
            Cell electricVehicleCell = row.createCell(3);
            electricVehicleCell.setCellValue(dto.getElectricVehicleMitigationKtCO2e() != null ? dto.getElectricVehicleMitigationKtCO2e() : 0.0);
            electricVehicleCell.setCellStyle(mitigationStyle);

            // Total Mitigation (pink)
            Cell totalMitCell = row.createCell(4);
            totalMitCell.setCellValue(dto.getTotalMitigationKtCO2e() != null ? dto.getTotalMitigationKtCO2e() : 0.0);
            totalMitCell.setCellStyle(totalStyle);

            // Net Emissions (pink)
            Cell netEmissionsCell = row.createCell(5);
            netEmissionsCell.setCellValue(dto.getNetEmissionsKtCO2e() != null ? dto.getNetEmissionsKtCO2e() : 0.0);
            netEmissionsCell.setCellStyle(totalStyle);
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
                                 List<TransportScenarioDashboardYearDto> data, CreationHelper creationHelper) {
        int rowIdx = 0;

        // Set small column widths for compact chart
        chartSheet.setColumnWidth(0, 6000);
        chartSheet.setColumnWidth(1, 6000);
        chartSheet.setColumnWidth(2, 6000);

        // Title
        Row titleRow = chartSheet.createRow(rowIdx++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Transport Scenario Emissions (ktCO2eq)");
        titleCell.setCellStyle(titleStyle);
        chartSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));
        titleRow.setHeightInPoints(25);

        // Add blank row for spacing
        chartSheet.createRow(rowIdx++).setHeightInPoints(5);

        // Create chart - start from row 2 (after title and blank row)
        int chartStartRow = rowIdx;
        XSSFDrawing drawing = chartSheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 0, chartStartRow, 2, chartStartRow + 15);
        XSSFChart chart = drawing.createChart(anchor);
        chart.setTitleText("Transport Scenario Emissions (ktCO2eq)");
        chart.setTitleOverlay(false);

        XDDFChartLegend legend = chart.getOrAddLegend();
        legend.setPosition(LegendPosition.BOTTOM);

        XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
        bottomAxis.setTitle("Year");
        XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
        leftAxis.setTitle("Emissions (ktCO2eq)");
        leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

        // Prepare data - data starts at row 2 (after title and blank row)
        int dataStartRow = 2;
        int dataEndRow = dataStartRow + data.size() - 1;

        // Year labels
        String[] yearLabels = data.stream()
                .map(dto -> String.valueOf(dto.getYear()))
                .toArray(String[]::new);
        XDDFCategoryDataSource categories = XDDFDataSourcesFactory.fromArray(yearLabels, null);

        // BAU Scenario series (column 1)
        CellRangeAddress bauRange = new CellRangeAddress(dataStartRow, dataEndRow, 1, 1);
        XDDFNumericalDataSource<Double> bauValues = XDDFDataSourcesFactory
                .fromNumericCellRange(dataSheet, bauRange);

        // Net Emissions series (column 5)
        CellRangeAddress netEmissionsRange = new CellRangeAddress(dataStartRow, dataEndRow, 5, 5);
        XDDFNumericalDataSource<Double> netEmissionsValues = XDDFDataSourcesFactory
                .fromNumericCellRange(dataSheet, netEmissionsRange);

        // Create line chart
        XDDFChartData lineData = chart.createData(ChartTypes.LINE, bottomAxis, leftAxis);
        XDDFLineChartData line = (XDDFLineChartData) lineData;

        XDDFLineChartData.Series bauSeries = (XDDFLineChartData.Series) line.addSeries(categories, bauValues);
        bauSeries.setTitle("BAU Scenario (ktCO2eq)", null);

        XDDFLineChartData.Series netEmissionsSeries = (XDDFLineChartData.Series) line.addSeries(categories, netEmissionsValues);
        netEmissionsSeries.setTitle("Net Emissions (ktCO2eq)", null);

        chart.plot(lineData);
    }
}

