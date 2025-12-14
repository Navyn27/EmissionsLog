package com.navyn.emissionlog.modules.mitigationProjects.IPPU;

import com.navyn.emissionlog.modules.mitigationProjects.IPPU.dto.IPPUDashboardSummaryDto;
import com.navyn.emissionlog.modules.mitigationProjects.IPPU.dto.IPPUDashboardYearDto;
import com.navyn.emissionlog.modules.mitigationProjects.IPPU.model.IPPUMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.IPPU.repository.IIPPURepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xddf.usermodel.chart.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class IPPUDashboardServiceImpl implements IPPUDashboardService {

    private final IIPPURepository iippuRepository;

    @Override
    public IPPUDashboardSummaryDto getIPPUDashboardSummary(Integer startingYear, Integer endingYear) {
        List<IPPUMitigation> allMitigations = iippuRepository.findAll();

        // Filter by year range if specified
        if (startingYear != null && endingYear != null) {
            allMitigations = filterByYearRange(allMitigations, startingYear, endingYear);
        }

        // Calculate totals
        double totalMitigation = allMitigations.stream()
                .mapToDouble(m -> m.getMitigationScenario() != 0 ? m.getMitigationScenario() : 0.0)
                .sum();

        double totalBAU = allMitigations.stream()
                .mapToDouble(m -> m.getBau() != 0 ? m.getBau() : 0.0)
                .sum();

        double totalReducedEmission = allMitigations.stream()
                .mapToDouble(m -> m.getReducedEmissionInKtCO2e() != 0 ? m.getReducedEmissionInKtCO2e() : 0.0)
                .sum();

        // Calculate totals by F-gas type
        Map<String, Double> totalsByFGas = allMitigations.stream()
                .filter(m -> m.getFGasName() != null && !m.getFGasName().trim().isEmpty())
                .collect(Collectors.groupingBy(
                        IPPUMitigation::getFGasName,
                        Collectors.summingDouble(
                                m -> m.getReducedEmissionInKtCO2e() != 0 ? m.getReducedEmissionInKtCO2e() : 0.0)));

        // Calculate percentages
        Map<String, Double> percentagesByFGas = new LinkedHashMap<>();
        if (totalReducedEmission > 0) {
            totalsByFGas.entrySet().stream()
                    .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                    .forEach(entry -> {
                        double percentage = (entry.getValue() / totalReducedEmission) * 100.0;
                        percentagesByFGas.put(entry.getKey(), percentage);
                    });
        }

        IPPUDashboardSummaryDto dto = new IPPUDashboardSummaryDto();
        dto.setStartingYear(startingYear);
        dto.setEndingYear(endingYear);
        dto.setTotalMitigationKtCO2e(totalMitigation);
        dto.setTotalBAUKtCO2e(totalBAU);
        dto.setTotalReducedEmissionKtCO2e(totalReducedEmission);
        dto.setTotalsByFGas(totalsByFGas);
        dto.setPercentagesByFGas(percentagesByFGas);

        return dto;
    }

    @Override
    public List<IPPUDashboardYearDto> getIPPUDashboardGraph(Integer startingYear, Integer endingYear) {
        int currentYear = LocalDateTime.now().getYear();
        int start = startingYear != null ? startingYear : currentYear - 4;
        int end = endingYear != null ? endingYear : currentYear;

        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }

        List<IPPUMitigation> allMitigations = iippuRepository.findAll();

        // Get unique F-gas names
        Set<String> uniqueFGasNames = allMitigations.stream()
                .map(IPPUMitigation::getFGasName)
                .filter(name -> name != null && !name.trim().isEmpty())
                .collect(Collectors.toSet());

        // Group by year and F-gas name
        Map<Integer, Map<String, Double>> perYearByFGas = allMitigations.stream()
                .filter(m -> m.getFGasName() != null && !m.getFGasName().trim().isEmpty())
                .collect(Collectors.groupingBy(
                        IPPUMitigation::getYear,
                        Collectors.groupingBy(
                                IPPUMitigation::getFGasName,
                                Collectors.summingDouble(
                                        m -> m.getReducedEmissionInKtCO2e() != 0 ? m.getReducedEmissionInKtCO2e()
                                                : 0.0))));

        List<IPPUDashboardYearDto> response = new ArrayList<>();
        for (int year = start; year <= end; year++) {
            Map<String, Double> yearFGasValues = perYearByFGas.getOrDefault(year, new HashMap<>());

            // Ensure all F-gas names are present (with 0.0 if no data)
            Map<String, Double> fGasValues = new LinkedHashMap<>();
            for (String fGasName : uniqueFGasNames) {
                fGasValues.put(fGasName, yearFGasValues.getOrDefault(fGasName, 0.0));
            }

            double totalMitigation = fGasValues.values().stream().mapToDouble(Double::doubleValue).sum();

            IPPUDashboardYearDto dto = new IPPUDashboardYearDto();
            dto.setYear(year);
            dto.setFGasValues(fGasValues);
            dto.setTotalMitigationKtCO2e(totalMitigation);

            response.add(dto);
        }

        return response;
    }

    @Override
    public byte[] exportIPPUDashboard(Integer startingYear, Integer endingYear) {
        List<IPPUMitigation> allMitigations = iippuRepository.findAll();

        // Get min and max years
        int minYear = allMitigations.stream()
                .map(IPPUMitigation::getYear)
                .filter(y -> y != null)
                .min(Comparator.naturalOrder())
                .orElse(LocalDateTime.now().getYear());

        int maxYear = allMitigations.stream()
                .map(IPPUMitigation::getYear)
                .filter(y -> y != null)
                .max(Comparator.naturalOrder())
                .orElse(LocalDateTime.now().getYear());

        int start = Optional.ofNullable(startingYear).orElse(minYear);
        int end = Optional.ofNullable(endingYear).orElse(maxYear);
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }

        allMitigations = filterByYearRange(allMitigations, start, end);

        // Get unique F-gas names (sorted by total mitigation descending)
        Map<String, Double> totalsByFGas = allMitigations.stream()
                .filter(m -> m.getFGasName() != null && !m.getFGasName().trim().isEmpty())
                .collect(Collectors.groupingBy(
                        IPPUMitigation::getFGasName,
                        Collectors.summingDouble(
                                m -> m.getReducedEmissionInKtCO2e() != 0 ? m.getReducedEmissionInKtCO2e() : 0.0)));

        List<String> sortedFGasNames = totalsByFGas.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        try (XSSFWorkbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            CreationHelper creationHelper = workbook.getCreationHelper();

            // Create professional styles
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle alternateDataStyle = createAlternateDataStyle(workbook);
            CellStyle summaryHeaderStyle = createSummaryHeaderStyle(workbook);
            CellStyle summaryDataStyle = createSummaryDataStyle(workbook);
            CellStyle numberStyle = createNumberStyle(workbook);
            DataFormat dataFormat = workbook.createDataFormat();

            XSSFSheet summarySheet = workbook.createSheet("Summary");
            buildSummarySheet(summarySheet, titleStyle, headerStyle, dataStyle, alternateDataStyle,
                    summaryHeaderStyle, summaryDataStyle, numberStyle, dataFormat, start, end,
                    allMitigations, sortedFGasNames, totalsByFGas, creationHelper);

            buildDetailedDataSheet(workbook.createSheet("Detailed Data"), headerStyle, dataStyle,
                    alternateDataStyle, numberStyle, allMitigations);

            workbook.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate IPPU dashboard export", e);
        }
    }

    private List<IPPUMitigation> filterByYearRange(List<IPPUMitigation> mitigations, int start, int end) {
        return mitigations.stream()
                .filter(m -> {
                    Integer year = m.getYear();
                    return year != null && year >= start && year <= end;
                })
                .collect(Collectors.toList());
    }

    // Style creation methods
    private CellStyle createTitleStyle(Workbook workbook) {
        XSSFCellStyle style = (XSSFCellStyle) workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 18);
        font.setColor(IndexedColors.WHITE.getIndex());
        font.setFontName("Calibri");
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.MEDIUM);
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.MEDIUM);
        style.setBorderRight(BorderStyle.MEDIUM);
        return style;
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        XSSFCellStyle style = (XSSFCellStyle) workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        font.setColor(IndexedColors.WHITE.getIndex());
        font.setFontName("Calibri");
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
        Font font = workbook.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);
        return style;
    }

    private CellStyle createAlternateDataStyle(Workbook workbook) {
        CellStyle style = createDataStyle(workbook);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle createSummaryHeaderStyle(Workbook workbook) {
        XSSFCellStyle style = (XSSFCellStyle) workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        font.setColor(IndexedColors.WHITE.getIndex());
        font.setFontName("Calibri");
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.TEAL.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.MEDIUM);
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.MEDIUM);
        style.setBorderRight(BorderStyle.MEDIUM);
        return style;
    }

    private CellStyle createSummaryDataStyle(Workbook workbook) {
        XSSFCellStyle style = (XSSFCellStyle) workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        font.setFontName("Calibri");
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderTop(BorderStyle.MEDIUM);
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.MEDIUM);
        style.setBorderRight(BorderStyle.MEDIUM);
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createNumberStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        DataFormat dataFormat = workbook.createDataFormat();
        style.setDataFormat(dataFormat.getFormat("#,##0.00"));
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createAlternateNumberStyle(Workbook workbook) {
        CellStyle style = createNumberStyle(workbook);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private void buildSummarySheet(
            XSSFSheet sheet,
            CellStyle titleStyle,
            CellStyle headerStyle,
            CellStyle dataStyle,
            CellStyle alternateDataStyle,
            CellStyle summaryHeaderStyle,
            CellStyle summaryDataStyle,
            CellStyle numberStyle,
            DataFormat dataFormat,
            int startYear,
            int endYear,
            List<IPPUMitigation> mitigations,
            List<String> sortedFGasNames,
            Map<String, Double> totalsByFGas,
            CreationHelper creationHelper) {
        int rowIdx = 0;

        // Title row
        Row title = sheet.createRow(rowIdx++);
        Cell titleCell = title.createCell(0);
        titleCell.setCellValue("IPPU Mitigation Dashboard Summary");
        titleCell.setCellStyle(titleStyle);
        int maxCols = Math.max(8, sortedFGasNames.size() + 2);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, maxCols));
        title.setHeightInPoints(30);

        rowIdx++; // Blank row

        // Year Range row
        Row rangeRow = sheet.createRow(rowIdx++);
        Cell rangeLabel = rangeRow.createCell(0);
        rangeLabel.setCellValue("Year Range:");
        CellStyle labelStyle = sheet.getWorkbook().createCellStyle();
        Font labelFont = sheet.getWorkbook().createFont();
        labelFont.setBold(true);
        labelFont.setFontHeightInPoints((short) 11);
        labelStyle.setFont(labelFont);
        labelStyle.setAlignment(HorizontalAlignment.LEFT);
        rangeLabel.setCellStyle(labelStyle);

        Cell rangeStart = rangeRow.createCell(1);
        rangeStart.setCellValue(startYear);
        CellStyle yearStyle = sheet.getWorkbook().createCellStyle();
        yearStyle.setDataFormat(dataFormat.getFormat("0"));
        yearStyle.setAlignment(HorizontalAlignment.LEFT);
        rangeStart.setCellStyle(yearStyle);

        Cell rangeTo = rangeRow.createCell(2);
        rangeTo.setCellValue("to");
        rangeTo.setCellStyle(labelStyle);

        Cell rangeEnd = rangeRow.createCell(3);
        rangeEnd.setCellValue(endYear);
        rangeEnd.setCellStyle(yearStyle);

        // Calculate overall totals
        double totalMitigation = mitigations.stream()
                .mapToDouble(m -> m.getMitigationScenario() != 0 ? m.getMitigationScenario() : 0.0)
                .sum();
        double totalBAU = mitigations.stream()
                .mapToDouble(m -> m.getBau() != 0 ? m.getBau() : 0.0)
                .sum();
        double totalReducedEmission = mitigations.stream()
                .mapToDouble(m -> m.getReducedEmissionInKtCO2e() != 0 ? m.getReducedEmissionInKtCO2e() : 0.0)
                .sum();

        rowIdx++; // Blank row

        // Overall Totals Summary Section
        Row totalsHeader = sheet.createRow(rowIdx++);
        totalsHeader.setHeightInPoints(20);
        Cell h1 = totalsHeader.createCell(0);
        h1.setCellValue("Metric");
        h1.setCellStyle(summaryHeaderStyle);
        Cell h2 = totalsHeader.createCell(1);
        h2.setCellValue("Total (ktCO2e)");
        h2.setCellStyle(summaryHeaderStyle);

        String[] metricLabels = { "Total Mitigation", "Total BAU", "Total Reduced Emissions" };
        double[] metricValues = { totalMitigation, totalBAU, totalReducedEmission };

        for (int i = 0; i < metricLabels.length; i++) {
            Row totalsRow = sheet.createRow(rowIdx++);
            Cell nameCell = totalsRow.createCell(0);
            nameCell.setCellValue(metricLabels[i]);
            CellStyle nameStyle = sheet.getWorkbook().createCellStyle();
            nameStyle.setBorderTop(BorderStyle.THIN);
            nameStyle.setBorderBottom(BorderStyle.THIN);
            nameStyle.setBorderLeft(BorderStyle.THIN);
            nameStyle.setBorderRight(BorderStyle.THIN);
            nameStyle.setAlignment(HorizontalAlignment.LEFT);
            nameStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            Font font = sheet.getWorkbook().createFont();
            font.setBold(true);
            nameStyle.setFont(font);
            nameCell.setCellStyle(nameStyle);

            Cell valueCell = totalsRow.createCell(1);
            valueCell.setCellValue(metricValues[i]);
            CellStyle valueCellStyle = sheet.getWorkbook().createCellStyle();
            valueCellStyle.cloneStyleFrom(numberStyle);
            Font valueFont = sheet.getWorkbook().createFont();
            valueFont.setBold(true);
            valueCellStyle.setFont(valueFont);
            valueCell.setCellStyle(valueCellStyle);
        }

        rowIdx++; // Blank row

        // F-Gas Type Breakdown Section
        Row fGasHeader = sheet.createRow(rowIdx++);
        fGasHeader.setHeightInPoints(20);
        Cell fGasH1 = fGasHeader.createCell(0);
        fGasH1.setCellValue("F-Gas Name");
        fGasH1.setCellStyle(summaryHeaderStyle);
        Cell fGasH2 = fGasHeader.createCell(1);
        fGasH2.setCellValue("Total Mitigation (ktCO2e)");
        fGasH2.setCellStyle(summaryHeaderStyle);
        Cell fGasH3 = fGasHeader.createCell(2);
        fGasH3.setCellValue("% of Total");
        fGasH3.setCellStyle(summaryHeaderStyle);

        int fGasDataStartRow = rowIdx;
        for (String fGasName : sortedFGasNames) {
            Row fGasRow = sheet.createRow(rowIdx++);
            Cell nameCell = fGasRow.createCell(0);
            nameCell.setCellValue(fGasName);
            CellStyle nameStyle = sheet.getWorkbook().createCellStyle();
            nameStyle.setBorderTop(BorderStyle.THIN);
            nameStyle.setBorderBottom(BorderStyle.THIN);
            nameStyle.setBorderLeft(BorderStyle.THIN);
            nameStyle.setBorderRight(BorderStyle.THIN);
            nameStyle.setAlignment(HorizontalAlignment.LEFT);
            nameStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            nameCell.setCellStyle(nameStyle);

            double fGasTotal = totalsByFGas.getOrDefault(fGasName, 0.0);
            Cell valueCell = fGasRow.createCell(1);
            valueCell.setCellValue(fGasTotal);
            CellStyle valueCellStyle = sheet.getWorkbook().createCellStyle();
            valueCellStyle.cloneStyleFrom(numberStyle);
            valueCell.setCellStyle(valueCellStyle);

            double percentage = totalReducedEmission > 0 ? (fGasTotal / totalReducedEmission) * 100.0 : 0.0;
            Cell percentCell = fGasRow.createCell(2);
            percentCell.setCellValue(percentage / 100.0); // Excel percentage format
            CellStyle percentStyle = sheet.getWorkbook().createCellStyle();
            percentStyle.cloneStyleFrom(numberStyle);
            DataFormat percentFormat = sheet.getWorkbook().createDataFormat();
            percentStyle.setDataFormat(percentFormat.getFormat("0.00%"));
            percentCell.setCellStyle(percentStyle);
        }

        rowIdx++; // Blank row

        // Per-Year Breakdown Table
        Row perYearHeader = sheet.createRow(rowIdx++);
        perYearHeader.setHeightInPoints(20);
        Cell yearHeaderCell = perYearHeader.createCell(0);
        yearHeaderCell.setCellValue("Year");
        yearHeaderCell.setCellStyle(headerStyle);

        int colIdx = 1;
        for (String fGasName : sortedFGasNames) {
            Cell fGasHeaderCell = perYearHeader.createCell(colIdx++);
            fGasHeaderCell.setCellValue(fGasName);
            fGasHeaderCell.setCellStyle(headerStyle);
        }
        Cell totalHeaderCell = perYearHeader.createCell(colIdx);
        totalHeaderCell.setCellValue("Total");
        totalHeaderCell.setCellStyle(headerStyle);

        // Group by year and F-gas
        Map<Integer, Map<String, Double>> perYearByFGas = mitigations.stream()
                .filter(m -> m.getFGasName() != null && !m.getFGasName().trim().isEmpty())
                .collect(Collectors.groupingBy(
                        IPPUMitigation::getYear,
                        Collectors.groupingBy(
                                IPPUMitigation::getFGasName,
                                Collectors.summingDouble(
                                        m -> m.getReducedEmissionInKtCO2e() != 0 ? m.getReducedEmissionInKtCO2e()
                                                : 0.0))));

        int dataStartRow = rowIdx;
        for (int year = startYear; year <= endYear; year++) {
            Row r = sheet.createRow(rowIdx++);
            r.setHeightInPoints(18);
            boolean isAlternate = (year - startYear) % 2 == 1;

            Cell yearCell = r.createCell(0);
            yearCell.setCellValue(year);
            CellStyle baseYearStyle = isAlternate ? alternateDataStyle : dataStyle;
            CellStyle yearCellStyle = sheet.getWorkbook().createCellStyle();
            yearCellStyle.cloneStyleFrom(baseYearStyle);
            yearCellStyle.setAlignment(HorizontalAlignment.CENTER);
            yearCellStyle.setDataFormat(dataFormat.getFormat("0"));
            yearCell.setCellStyle(yearCellStyle);

            Map<String, Double> yearFGasValues = perYearByFGas.getOrDefault(year, new HashMap<>());
            double yearTotal = 0.0;

            colIdx = 1;
            CellStyle numStyle = isAlternate ? createAlternateNumberStyle(sheet.getWorkbook()) : numberStyle;
            for (String fGasName : sortedFGasNames) {
                double value = yearFGasValues.getOrDefault(fGasName, 0.0);
                yearTotal += value;
                Cell valueCell = r.createCell(colIdx++);
                valueCell.setCellValue(value);
                valueCell.setCellStyle(numStyle);
            }

            Cell totalCell = r.createCell(colIdx);
            totalCell.setCellValue(yearTotal);
            CellStyle totalStyle = sheet.getWorkbook().createCellStyle();
            totalStyle.cloneStyleFrom(numStyle);
            Font totalFont = sheet.getWorkbook().createFont();
            totalFont.setBold(true);
            totalStyle.setFont(totalFont);
            totalCell.setCellStyle(totalStyle);
        }

        // Auto-size columns
        int totalCols = sortedFGasNames.size() + 2;
        for (int i = 0; i < totalCols; i++) {
            sheet.autoSizeColumn(i);
            int currentWidth = sheet.getColumnWidth(i);
            int minWidth = 2500;
            int maxWidth = 18000;
            if (i == 0) { // Year column
                minWidth = 1500;
            }
            if (currentWidth < minWidth) {
                sheet.setColumnWidth(i, minWidth);
            } else if (currentWidth > maxWidth) {
                sheet.setColumnWidth(i, maxWidth);
            }
        }

        // Charts
        int dataEndRow = rowIdx - 1;
        if (dataEndRow >= dataStartRow && !sortedFGasNames.isEmpty()) {
            // Chart 1: Stacked Bar Chart
            XSSFDrawing drawing = sheet.createDrawingPatriarch();
            XSSFClientAnchor anchor1 = new XSSFClientAnchor();
            anchor1.setCol1(0);
            anchor1.setRow1(dataEndRow + 2);
            anchor1.setCol2(Math.min(10, totalCols + 2));
            anchor1.setRow2(dataEndRow + 25);

            XSSFChart chart1 = drawing.createChart(anchor1);
            chart1.setTitleText("Mitigation by F-Gas Type Over Time");
            chart1.setTitleOverlay(false);
            XDDFChartLegend legend1 = chart1.getOrAddLegend();
            legend1.setPosition(LegendPosition.BOTTOM);

            XDDFCategoryAxis bottomAxis = chart1.createCategoryAxis(AxisPosition.BOTTOM);
            bottomAxis.setTitle("Year");
            XDDFValueAxis leftAxis = chart1.createValueAxis(AxisPosition.LEFT);
            leftAxis.setTitle("Mitigation (ktCO2e)");
            leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

            int pointCount = dataEndRow - dataStartRow + 1;
            String[] yearLabels = new String[pointCount];
            for (int i = 0; i < pointCount; i++) {
                yearLabels[i] = String.valueOf(startYear + i);
            }
            XDDFCategoryDataSource categories = XDDFDataSourcesFactory.fromArray(yearLabels, null);

            XDDFChartData barData = chart1.createData(ChartTypes.BAR, bottomAxis, leftAxis);
            XDDFBarChartData bar = (XDDFBarChartData) barData;
            bar.setBarGrouping(BarGrouping.STACKED);
            bar.setBarDirection(BarDirection.COL);
            bar.setGapWidth(75);

            for (int c = 0; c < sortedFGasNames.size() && c < 10; c++) { // Limit to 10 series for readability
                CellRangeAddress valuesRange = new CellRangeAddress(dataStartRow, dataEndRow, c + 1, c + 1);
                XDDFNumericalDataSource<Double> values = XDDFDataSourcesFactory
                        .fromNumericCellRange(sheet, valuesRange);
                XDDFBarChartData.Series series = (XDDFBarChartData.Series) bar.addSeries(categories, values);
                series.setTitle(sortedFGasNames.get(c), null);
            }

            chart1.plot(barData);

            // Chart 2: Pie Chart (F-Gas Contribution)
            XSSFClientAnchor anchor2 = new XSSFClientAnchor();
            anchor2.setCol1(Math.min(12, totalCols + 4));
            anchor2.setRow1(dataEndRow + 2);
            anchor2.setCol2(Math.min(22, totalCols + 14));
            anchor2.setRow2(dataEndRow + 25);

            XSSFChart chart2 = drawing.createChart(anchor2);
            chart2.setTitleText("F-Gas Contribution (Total)");
            chart2.setTitleOverlay(false);
            XDDFChartLegend legend2 = chart2.getOrAddLegend();
            legend2.setPosition(LegendPosition.RIGHT);

            XDDFChartData pieData = chart2.createData(ChartTypes.PIE, null, null);
            XDDFPieChartData pie = (XDDFPieChartData) pieData;

            // Use F-Gas totals table for pie chart
            CellRangeAddress categoriesRange = new CellRangeAddress(fGasDataStartRow,
                    fGasDataStartRow + sortedFGasNames.size() - 1, 0, 0);
            CellRangeAddress valuesRange = new CellRangeAddress(fGasDataStartRow,
                    fGasDataStartRow + sortedFGasNames.size() - 1, 1, 1);

            XDDFCategoryDataSource pieCategories = XDDFDataSourcesFactory.fromStringCellRange(sheet, categoriesRange);
            XDDFNumericalDataSource<Double> pieValues = XDDFDataSourcesFactory.fromNumericCellRange(sheet, valuesRange);

            XDDFPieChartData.Series series = (XDDFPieChartData.Series) pie.addSeries(pieCategories, pieValues);
            series.setTitle("F-Gas Contribution", null);

            chart2.plot(pieData);
        }
    }

    private void buildDetailedDataSheet(XSSFSheet sheet, CellStyle headerStyle, CellStyle dataStyle,
            CellStyle alternateDataStyle, CellStyle numberStyle, List<IPPUMitigation> data) {
        // Sort by year, then by F-gas name
        data.sort(Comparator.comparing(IPPUMitigation::getYear)
                .thenComparing(m -> m.getFGasName() != null ? m.getFGasName() : ""));

        String[] headers = {
                "Year",
                "F-Gas Name",
                "BAU (ktCO2e)",
                "Amount of Avoided F-Gas (kg)",
                "GWP Factor",
                "Reduced Emission (kg CO2e)",
                "Reduced Emission (kt CO2e)",
                "Mitigation Scenario (kt CO2e)"
        };
        createHeader(sheet, headerStyle, headers);
        DataFormat dataFormat = sheet.getWorkbook().createDataFormat();
        int rowIdx = 1;
        for (int i = 0; i < data.size(); i++) {
            IPPUMitigation item = data.get(i);
            Row r = sheet.createRow(rowIdx++);
            r.setHeightInPoints(18);
            boolean isAlternate = i % 2 == 1;

            Cell yearCell = r.createCell(0);
            yearCell.setCellValue(item.getYear());
            CellStyle baseYearStyle = isAlternate ? alternateDataStyle : dataStyle;
            CellStyle yearCellStyle = sheet.getWorkbook().createCellStyle();
            yearCellStyle.cloneStyleFrom(baseYearStyle);
            yearCellStyle.setAlignment(HorizontalAlignment.CENTER);
            yearCellStyle.setDataFormat(dataFormat.getFormat("0"));
            yearCell.setCellStyle(yearCellStyle);

            CellStyle numStyle = isAlternate ? createAlternateNumberStyle(sheet.getWorkbook()) : numberStyle;
            r.createCell(1).setCellValue(item.getFGasName() != null ? item.getFGasName() : "");
            r.getCell(1).setCellStyle(isAlternate ? alternateDataStyle : dataStyle);
            r.createCell(2).setCellValue(item.getBau() != 0 ? item.getBau() : 0.0);
            r.getCell(2).setCellStyle(numStyle);
            r.createCell(3).setCellValue(item.getAmountOfAvoidedFGas() != 0 ? item.getAmountOfAvoidedFGas() : 0.0);
            r.getCell(3).setCellStyle(numStyle);
            r.createCell(4).setCellValue(item.getGwpFactor() != 0 ? item.getGwpFactor() : 0.0);
            r.getCell(4).setCellStyle(numStyle);
            r.createCell(5)
                    .setCellValue(item.getReducedEmissionInKgCO2e() != 0 ? item.getReducedEmissionInKgCO2e() : 0.0);
            r.getCell(5).setCellStyle(numStyle);
            r.createCell(6)
                    .setCellValue(item.getReducedEmissionInKtCO2e() != 0 ? item.getReducedEmissionInKtCO2e() : 0.0);
            r.getCell(6).setCellStyle(numStyle);
            r.createCell(7).setCellValue(item.getMitigationScenario() != 0 ? item.getMitigationScenario() : 0.0);
            r.getCell(7).setCellStyle(numStyle);
        }
        autoSizeWithLimits(sheet, headers.length);
    }

    private void createHeader(Sheet sheet, CellStyle headerStyle, String[] headers) {
        Row headerRow = sheet.createRow(0);
        headerRow.setHeightInPoints(22);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private void autoSizeWithLimits(Sheet sheet, int columns) {
        for (int i = 0; i < columns; i++) {
            sheet.autoSizeColumn(i);
            int currentWidth = sheet.getColumnWidth(i);
            int minWidth = 2500;
            int maxWidth = 20000;
            if (currentWidth < minWidth) {
                sheet.setColumnWidth(i, minWidth);
            } else if (currentWidth > maxWidth) {
                sheet.setColumnWidth(i, maxWidth);
            }
        }
    }
}
