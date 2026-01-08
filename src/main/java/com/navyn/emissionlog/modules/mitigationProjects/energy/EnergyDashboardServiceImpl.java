package com.navyn.emissionlog.modules.mitigationProjects.energy;

import com.navyn.emissionlog.modules.mitigationProjects.energy.dto.EnergyDashboardSummaryDto;
import com.navyn.emissionlog.modules.mitigationProjects.energy.dto.EnergyDashboardYearDto;
import com.navyn.emissionlog.modules.mitigationProjects.energy.rooftop.model.RoofTopMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.energy.rooftop.repository.IRoofTopMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.model.LightBulb;
import com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.repository.ILightBulbRepository;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.models.StoveMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.repository.StoveMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.models.AvoidedElectricityProduction;
import com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.repository.AvoidedElectricityProductionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xddf.usermodel.chart.*;
import org.hibernate.Hibernate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;
import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class EnergyDashboardServiceImpl implements EnergyDashboardService {

    private static final double TCO2E_TO_KCO2E = 1000.0; // Conversion factor: tCO2e to ktCO2e

    private final IRoofTopMitigationRepository rooftopRepository;
    private final ILightBulbRepository lightbulbRepository;
    private final StoveMitigationRepository cookstoveRepository;
    private final AvoidedElectricityProductionRepository waterheatRepository;

    /**
     * Convert tCO2e to ktCO2e
     */
    private double convertToKtCO2e(double tCO2e) {
        return tCO2e / TCO2E_TO_KCO2E;
    }

    /**
     * Sum projectEmission for cookstove records in a year
     * projectEmission is already in ktCO2e, no conversion needed
     */
    private double getCookstoveTotalForYear(List<StoveMitigation> cookstoveData, int year) {
        return cookstoveData.stream()
                .filter(s -> s.getYear() == year)
                .mapToDouble(s -> s.getProjectEmission() != null ? s.getProjectEmission() : 0.0)
                .sum();
    }

    @Override
    public EnergyDashboardSummaryDto getEnergyDashboardSummary(Integer startingYear, Integer endingYear) {
        List<StoveMitigation> cookstove = cookstoveRepository.findAll();
        List<RoofTopMitigation> rooftop = rooftopRepository.findAll();
        List<LightBulb> lightbulb = lightbulbRepository.findAll();
        List<AvoidedElectricityProduction> waterheat = waterheatRepository.findAll();

        // Filter by year range if specified
        if (startingYear != null && endingYear != null) {
            cookstove = filterByYear(cookstove, StoveMitigation::getYear, startingYear, endingYear);
            rooftop = filterByYear(rooftop, RoofTopMitigation::getYear, startingYear, endingYear);
            lightbulb = filterByYear(lightbulb, LightBulb::getYear, startingYear, endingYear);
            waterheat = filterByYear(waterheat, AvoidedElectricityProduction::getYear, startingYear, endingYear);
        }

        // Aggregate cookstove: Sum projectEmission (already in ktCO2e, no conversion
        // needed)
        double cookstoveTotal = cookstove.stream()
                .mapToDouble(s -> s.getProjectEmission() != null ? s.getProjectEmission() : 0.0)
                .sum();

        // Aggregate rooftop: Sum netGhGMitigationAchieved (convert tCO2e to ktCO2e)
        double rooftopTotal = rooftop.stream()
                .mapToDouble(r -> convertToKtCO2e(r.getNetGhGMitigationAchieved()))
                .sum();

        // Aggregate lightbulb: Sum netGhGMitigationAchieved (convert tCO2e to ktCO2e)
        double lightbulbTotal = lightbulb.stream()
                .mapToDouble(l -> convertToKtCO2e(l.getNetGhGMitigationAchieved()))
                .sum();

        // Aggregate waterheat: Sum netGhGMitigation (convert tCO2e to ktCO2e)
        double waterheatTotal = waterheat.stream()
                .mapToDouble(w -> w.getNetGhGMitigation() != null ? convertToKtCO2e(w.getNetGhGMitigation()) : 0.0)
                .sum();

        double totalMitigation = cookstoveTotal + rooftopTotal + lightbulbTotal + waterheatTotal;

        EnergyDashboardSummaryDto dto = new EnergyDashboardSummaryDto();
        dto.setStartingYear(startingYear);
        dto.setEndingYear(endingYear);
        dto.setCookstove(cookstoveTotal);
        dto.setRooftop(rooftopTotal);
        dto.setLightbulb(lightbulbTotal);
        dto.setWaterheat(waterheatTotal);
        dto.setTotalMitigationKtCO2e(totalMitigation);

        return dto;
    }

    @Override
    public List<EnergyDashboardYearDto> getEnergyDashboardGraph(Integer startingYear, Integer endingYear) {
        int currentYear = LocalDateTime.now().getYear();
        int start = startingYear != null ? startingYear : currentYear - 4;
        int end = endingYear != null ? endingYear : currentYear;

        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }

        List<StoveMitigation> cookstove = cookstoveRepository.findAll();
        List<RoofTopMitigation> rooftop = rooftopRepository.findAll();
        List<LightBulb> lightbulb = lightbulbRepository.findAll();
        List<AvoidedElectricityProduction> waterheat = waterheatRepository.findAll();

        List<EnergyDashboardYearDto> response = new ArrayList<>();
        for (int year = start; year <= end; year++) {
            double cookstoveTotal = getCookstoveTotalForYear(
                    filterByYear(cookstove, StoveMitigation::getYear, year, year), year);
            double rooftopTotal = sumDouble(
                    filterByYear(rooftop, RoofTopMitigation::getYear, year, year),
                    r -> convertToKtCO2e(r.getNetGhGMitigationAchieved()));
            double lightbulbTotal = sumDouble(
                    filterByYear(lightbulb, LightBulb::getYear, year, year),
                    l -> convertToKtCO2e(l.getNetGhGMitigationAchieved()));
            double waterheatTotal = sumDouble(
                    filterByYear(waterheat, AvoidedElectricityProduction::getYear, year, year),
                    w -> w.getNetGhGMitigation() != null ? convertToKtCO2e(w.getNetGhGMitigation()) : 0.0);

            double totalMitigation = cookstoveTotal + rooftopTotal + lightbulbTotal + waterheatTotal;

            EnergyDashboardYearDto dto = new EnergyDashboardYearDto();
            dto.setYear(year);
            dto.setCookstove(cookstoveTotal);
            dto.setRooftop(rooftopTotal);
            dto.setLightbulb(lightbulbTotal);
            dto.setWaterheat(waterheatTotal);
            dto.setTotalMitigationKtCO2e(totalMitigation);

            response.add(dto);
        }

        return response;
    }

    private <T> List<T> filterByYear(List<T> source, Function<T, Integer> yearExtractor, Integer start, Integer end) {
        return source.stream()
                .filter(item -> {
                    Integer year = yearExtractor.apply(item);
                    return year != null && year >= start && year <= end;
                })
                .toList();
    }

    private <T> double sumDouble(List<T> source, Function<T, Double> extractor) {
        return source.stream()
                .map(extractor)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    @Override
    public byte[] exportEnergyDashboard(Integer startingYear, Integer endingYear) {
        List<StoveMitigation> cookstove = cookstoveRepository.findAll();
        List<RoofTopMitigation> rooftop = rooftopRepository.findAll();
        List<LightBulb> lightbulb = lightbulbRepository.findAll();
        List<AvoidedElectricityProduction> waterheat = waterheatRepository.findAll();

        // Get min and max years from all projects
        int minYear = Stream.of(
                cookstove.stream().map(StoveMitigation::getYear),
                rooftop.stream().map(RoofTopMitigation::getYear),
                lightbulb.stream().map(LightBulb::getYear),
                waterheat.stream().map(AvoidedElectricityProduction::getYear))
                .flatMap(s -> s)
                .filter(y -> y != null)
                .min(Comparator.naturalOrder())
                .orElse(LocalDateTime.now().getYear());

        int maxYear = Stream.of(
                cookstove.stream().map(StoveMitigation::getYear),
                rooftop.stream().map(RoofTopMitigation::getYear),
                lightbulb.stream().map(LightBulb::getYear),
                waterheat.stream().map(AvoidedElectricityProduction::getYear))
                .flatMap(s -> s)
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

        cookstove = filterByYear(cookstove, StoveMitigation::getYear, start, end);
        rooftop = filterByYear(rooftop, RoofTopMitigation::getYear, start, end);
        lightbulb = filterByYear(lightbulb, LightBulb::getYear, start, end);
        waterheat = filterByYear(waterheat, AvoidedElectricityProduction::getYear, start, end);

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
                    cookstove, rooftop, lightbulb, waterheat, creationHelper);

            buildCookstoveSheet(workbook.createSheet("Cookstove"), headerStyle, dataStyle,
                    alternateDataStyle, numberStyle, cookstove);
            buildRooftopSheet(workbook.createSheet("Rooftop"), headerStyle, dataStyle,
                    alternateDataStyle, numberStyle, rooftop);
            buildLightbulbSheet(workbook.createSheet("Lightbulb"), headerStyle, dataStyle,
                    alternateDataStyle, numberStyle, lightbulb);
            buildWaterheatSheet(workbook.createSheet("Waterheat"), headerStyle, dataStyle,
                    alternateDataStyle, numberStyle, waterheat);

            workbook.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate energy dashboard export", e);
        }
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
            List<StoveMitigation> cookstove,
            List<RoofTopMitigation> rooftop,
            List<LightBulb> lightbulb,
            List<AvoidedElectricityProduction> waterheat,
            CreationHelper creationHelper) {
        int rowIdx = 0;

        // Title row
        Row title = sheet.createRow(rowIdx++);
        Cell titleCell = title.createCell(0);
        titleCell.setCellValue("Energy Mitigation Dashboard Summary");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));
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

        // Calculate totals
        double cookstoveTotal = cookstove.stream()
                .mapToDouble(s -> s.getProjectEmission() != null ? s.getProjectEmission() : 0.0)
                .sum();

        double rooftopTotal = rooftop.stream()
                .mapToDouble(r -> convertToKtCO2e(r.getNetGhGMitigationAchieved()))
                .sum();

        double lightbulbTotal = lightbulb.stream()
                .mapToDouble(l -> convertToKtCO2e(l.getNetGhGMitigationAchieved()))
                .sum();

        double waterheatTotal = waterheat.stream()
                .mapToDouble(w -> w.getNetGhGMitigation() != null ? convertToKtCO2e(w.getNetGhGMitigation()) : 0.0)
                .sum();

        double totalMitigation = cookstoveTotal + rooftopTotal + lightbulbTotal + waterheatTotal;

        rowIdx++; // Blank row

        // Totals Summary Section
        Row totalsHeader = sheet.createRow(rowIdx++);
        totalsHeader.setHeightInPoints(20);
        String[] totalLabels = new String[] {
                "Project",
                "Total Mitigation (ktCO2e)"
        };
        String[] projectNames = new String[] {
                "Cookstove",
                "Rooftop",
                "Lightbulb",
                "Waterheat",
                "TOTAL"
        };
        double[] totalValues = new double[] {
                cookstoveTotal,
                rooftopTotal,
                lightbulbTotal,
                waterheatTotal,
                totalMitigation
        };

        for (int i = 0; i < totalLabels.length; i++) {
            Cell h = totalsHeader.createCell(i);
            h.setCellValue(totalLabels[i]);
            h.setCellStyle(summaryHeaderStyle);
        }

        for (int i = 0; i < projectNames.length; i++) {
            Row totalsRow = sheet.createRow(rowIdx++);
            Cell nameCell = totalsRow.createCell(0);
            nameCell.setCellValue(projectNames[i]);
            CellStyle nameStyle = sheet.getWorkbook().createCellStyle();
            nameStyle.setBorderTop(BorderStyle.THIN);
            nameStyle.setBorderBottom(BorderStyle.THIN);
            nameStyle.setBorderLeft(BorderStyle.THIN);
            nameStyle.setBorderRight(BorderStyle.THIN);
            nameStyle.setAlignment(HorizontalAlignment.LEFT);
            nameStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            if (i == projectNames.length - 1) { // Last row (TOTAL)
                Font font = sheet.getWorkbook().createFont();
                font.setBold(true);
                nameStyle.setFont(font);
                nameStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
                nameStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            }
            nameCell.setCellStyle(nameStyle);

            Cell valueCell = totalsRow.createCell(1);
            valueCell.setCellValue(totalValues[i]);
            CellStyle valueCellStyle = sheet.getWorkbook().createCellStyle();
            valueCellStyle.cloneStyleFrom(numberStyle);
            if (i == projectNames.length - 1) { // Last row (TOTAL)
                Font font = sheet.getWorkbook().createFont();
                font.setBold(true);
                valueCellStyle.setFont(font);
                valueCellStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
                valueCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            }
            valueCell.setCellStyle(valueCellStyle);
        }

        rowIdx++; // Blank row

        // Per Year Data Table
        Row perYearHeader = sheet.createRow(rowIdx++);
        perYearHeader.setHeightInPoints(20);
        String[] header = new String[] {
                "Year",
                "Cookstove",
                "Rooftop",
                "Lightbulb",
                "Waterheat",
                "Total Mitigation"
        };
        for (int c = 0; c < header.length; c++) {
            Cell cell = perYearHeader.createCell(c);
            cell.setCellValue(header[c]);
            cell.setCellStyle(headerStyle);
        }

        int dataStartRow = rowIdx;
        for (int year = startYear; year <= endYear; year++) {
            Row r = sheet.createRow(rowIdx++);
            r.setHeightInPoints(18);
            boolean isAlternate = (year - startYear) % 2 == 1;

            double cs = getCookstoveTotalForYear(filterByYear(cookstove, StoveMitigation::getYear, year, year),
                    year);
            double rt = sumDouble(filterByYear(rooftop, RoofTopMitigation::getYear, year, year),
                    r2 -> convertToKtCO2e(r2.getNetGhGMitigationAchieved()));
            double lb = sumDouble(filterByYear(lightbulb, LightBulb::getYear, year, year),
                    l2 -> convertToKtCO2e(l2.getNetGhGMitigationAchieved()));
            double wh = sumDouble(filterByYear(waterheat, AvoidedElectricityProduction::getYear, year, year),
                    w2 -> w2.getNetGhGMitigation() != null ? convertToKtCO2e(w2.getNetGhGMitigation()) : 0.0);
            double total = cs + rt + lb + wh;

            // Year column
            Cell yearCell = r.createCell(0);
            yearCell.setCellValue(year);
            CellStyle baseYearStyle = isAlternate ? alternateDataStyle : dataStyle;
            CellStyle yearCellStyle = sheet.getWorkbook().createCellStyle();
            yearCellStyle.cloneStyleFrom(baseYearStyle);
            yearCellStyle.setAlignment(HorizontalAlignment.CENTER);
            yearCellStyle.setDataFormat(dataFormat.getFormat("0"));
            yearCell.setCellStyle(yearCellStyle);

            // Number columns
            CellStyle numStyle = isAlternate ? createAlternateNumberStyle(sheet.getWorkbook()) : numberStyle;
            Cell csCell = r.createCell(1);
            csCell.setCellValue(cs);
            csCell.setCellStyle(numStyle);
            r.createCell(2).setCellValue(rt);
            r.getCell(2).setCellStyle(numStyle);
            r.createCell(3).setCellValue(lb);
            r.getCell(3).setCellStyle(numStyle);
            r.createCell(4).setCellValue(wh);
            r.getCell(4).setCellStyle(numStyle);

            // Total column - bold
            Cell totalCell = r.createCell(5);
            totalCell.setCellValue(total);
            CellStyle totalStyle = sheet.getWorkbook().createCellStyle();
            totalStyle.cloneStyleFrom(numStyle);
            Font totalFont = sheet.getWorkbook().createFont();
            totalFont.setBold(true);
            totalStyle.setFont(totalFont);
            totalCell.setCellStyle(totalStyle);
        }

        // Auto-size columns
        for (int i = 0; i < header.length; i++) {
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

        // Chart
        int dataEndRow = rowIdx - 1;
        if (dataEndRow >= dataStartRow) {
            XSSFDrawing drawing = sheet.createDrawingPatriarch();
            XSSFClientAnchor anchor = new XSSFClientAnchor();
            anchor.setCol1(0);
            anchor.setRow1(dataEndRow + 2);
            anchor.setCol2(10);
            anchor.setRow2(dataEndRow + 25);

            XSSFChart chart = drawing.createChart(anchor);
            chart.setTitleText("Mitigation by Project and Year");
            chart.setTitleOverlay(false);
            XDDFChartLegend legend = chart.getOrAddLegend();
            legend.setPosition(LegendPosition.BOTTOM);

            XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
            bottomAxis.setTitle("Year");
            XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
            leftAxis.setTitle("Mitigation (ktCO2e)");
            leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

            int pointCount = dataEndRow - dataStartRow + 1;
            String[] yearLabels = new String[pointCount];
            for (int i = 0; i < pointCount; i++) {
                yearLabels[i] = String.valueOf(startYear + i);
            }
            XDDFCategoryDataSource categories = XDDFDataSourcesFactory.fromArray(yearLabels, null);

            XDDFChartData barData = chart.createData(ChartTypes.BAR, bottomAxis, leftAxis);
            XDDFBarChartData bar = (XDDFBarChartData) barData;
            bar.setBarGrouping(BarGrouping.STACKED);
            bar.setBarDirection(BarDirection.COL);
            bar.setGapWidth(75);

            for (int c = 1; c <= 4; c++) { // 4 project columns
                CellRangeAddress valuesRange = new CellRangeAddress(dataStartRow, dataEndRow, c, c);
                XDDFNumericalDataSource<Double> values = XDDFDataSourcesFactory
                        .fromNumericCellRange(sheet, valuesRange);
                XDDFBarChartData.Series series = (XDDFBarChartData.Series) bar.addSeries(categories, values);
                series.setTitle(header[c], null);
            }

            chart.plot(barData);
        }
    }

    private void buildCookstoveSheet(XSSFSheet sheet, CellStyle headerStyle, CellStyle dataStyle,
            CellStyle alternateDataStyle, CellStyle numberStyle,
            List<StoveMitigation> data) {
        // Sort by year
        List<StoveMitigation> sortedData = new ArrayList<>(data);
        sortedData.sort(Comparator.comparing(StoveMitigation::getYear));

        String[] headers = {
                "Year",
                "Stove Type",
                "Units Installed",
                "Efficiency (%)",
                "Project Intervention",
                "Fuel Consumption",
                "Project Emission (ktCO2e)"
        };
        createHeader(sheet, headerStyle, headers);
        DataFormat dataFormat = sheet.getWorkbook().createDataFormat();
        int rowIdx = 1;
        for (int i = 0; i < sortedData.size(); i++) {
            StoveMitigation item = sortedData.get(i);
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
            CellStyle textStyle = isAlternate ? alternateDataStyle : dataStyle;

            // Stove Type column (text)
            Cell stoveTypeCell = r.createCell(1);
            stoveTypeCell.setCellValue(item.getStoveType() != null ? item.getStoveType().name() : "");
            stoveTypeCell.setCellStyle(textStyle);

            // Units Installed column (number)
            r.createCell(2).setCellValue(item.getUnitsInstalled());
            r.getCell(2).setCellStyle(numStyle);

            // Efficiency column (number)
            r.createCell(3).setCellValue(item.getEfficiency() != null ? item.getEfficiency() : 0.0);
            r.getCell(3).setCellStyle(numStyle);

            // Project Intervention column (text)
            Cell interventionCell = r.createCell(4);
            String interventionName = "";
            if (item.getProjectIntervention() != null) {
                Hibernate.initialize(item.getProjectIntervention());
                interventionName = item.getProjectIntervention().getName();
            }
            interventionCell.setCellValue(interventionName);
            interventionCell.setCellStyle(textStyle);

            // Fuel Consumption column (number)
            r.createCell(5).setCellValue(item.getFuelConsumption() != null ? item.getFuelConsumption() : 0.0);
            r.getCell(5).setCellStyle(numStyle);

            // Project Emission column (number, already in ktCO2e)
            r.createCell(6).setCellValue(item.getProjectEmission() != null ? item.getProjectEmission() : 0.0);
            r.getCell(6).setCellStyle(numStyle);
        }
        autoSizeWithLimits(sheet, headers.length);
    }

    private void buildRooftopSheet(XSSFSheet sheet, CellStyle headerStyle, CellStyle dataStyle,
            CellStyle alternateDataStyle, CellStyle numberStyle, List<RoofTopMitigation> data) {
        String[] headers = {
                "Year",
                "Installed Units Per Year",
                "Cumulative Installed Units",
                "Net GHG Mitigation Achieved (tCO2e)",
                "Net GHG Mitigation Achieved (ktCO2e)",
                "BAU Emission Without Project",
                "Scenario GHG Emission With Project"
        };
        createHeader(sheet, headerStyle, headers);
        DataFormat dataFormat = sheet.getWorkbook().createDataFormat();
        int rowIdx = 1;
        for (int i = 0; i < data.size(); i++) {
            RoofTopMitigation item = data.get(i);
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
            r.createCell(1).setCellValue(item.getInstalledUnitPerYear());
            r.getCell(1).setCellStyle(numStyle);
            r.createCell(2).setCellValue(item.getCumulativeInstalledUnitPerYear());
            r.getCell(2).setCellStyle(numStyle);
            r.createCell(3).setCellValue(item.getNetGhGMitigationAchieved());
            r.getCell(3).setCellStyle(numStyle);
            r.createCell(4).setCellValue(convertToKtCO2e(item.getNetGhGMitigationAchieved()));
            r.getCell(4).setCellStyle(numStyle);
            r.createCell(5).setCellValue(item.getBauEmissionWithoutProject());
            r.getCell(5).setCellStyle(numStyle);
            r.createCell(6).setCellValue(item.getScenarioGhGEmissionWithProject());
            r.getCell(6).setCellStyle(numStyle);
        }
        autoSizeWithLimits(sheet, headers.length);
    }

    private void buildLightbulbSheet(XSSFSheet sheet, CellStyle headerStyle, CellStyle dataStyle,
            CellStyle alternateDataStyle, CellStyle numberStyle, List<LightBulb> data) {
        String[] headers = {
                "Year",
                "Total Installed Bulbs Per Year",
                "Reduction Capacity Per Bulb (Wh)",
                "Project Intervention",
                "Total Reduction Per Year (kWh)",
                "Net GHG Mitigation Achieved (tCO2e)",
                "Net GHG Mitigation Achieved (ktCO2e)",
                "Adjusted BAU Emission Mitigation (ktCO2e)",
                "Scenario GHG Mitigation Achieved (ktCO2e)"
        };
        createHeader(sheet, headerStyle, headers);
        DataFormat dataFormat = sheet.getWorkbook().createDataFormat();
        int rowIdx = 1;
        for (int i = 0; i < data.size(); i++) {
            LightBulb item = data.get(i);
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
            r.createCell(1).setCellValue(item.getTotalInstalledBulbsPerYear());
            r.getCell(1).setCellStyle(numStyle);
            r.createCell(2).setCellValue(item.getReductionCapacityPerBulb());
            r.getCell(2).setCellStyle(numStyle);

            // Project Intervention column (text)
            Cell interventionCell = r.createCell(3);
            String interventionName = "";
            if (item.getProjectIntervention() != null) {
                // Force Hibernate to initialize the proxy while session is still open
                Hibernate.initialize(item.getProjectIntervention());
                interventionName = item.getProjectIntervention().getName();
            }
            interventionCell.setCellValue(interventionName);
            CellStyle baseTextStyle = isAlternate ? alternateDataStyle : dataStyle;
            CellStyle textCellStyle = sheet.getWorkbook().createCellStyle();
            textCellStyle.cloneStyleFrom(baseTextStyle);
            textCellStyle.setAlignment(HorizontalAlignment.LEFT);
            interventionCell.setCellStyle(textCellStyle);

            r.createCell(4).setCellValue(item.getTotalReductionPerYear());
            r.getCell(4).setCellStyle(numStyle);
            r.createCell(5).setCellValue(item.getNetGhGMitigationAchieved());
            r.getCell(5).setCellStyle(numStyle);
            r.createCell(6).setCellValue(convertToKtCO2e(item.getNetGhGMitigationAchieved()));
            r.getCell(6).setCellStyle(numStyle);
            r.createCell(7).setCellValue(item.getAdjustedBauEmissionMitigation());
            r.getCell(7).setCellStyle(numStyle);
            // Scenario GHG Mitigation Achieved is already in ktCO2e, no conversion needed
            r.createCell(8).setCellValue(item.getScenarioGhGMitigationAchieved());
            r.getCell(8).setCellStyle(numStyle);
        }
        autoSizeWithLimits(sheet, headers.length);
    }

    private void buildWaterheatSheet(XSSFSheet sheet, CellStyle headerStyle, CellStyle dataStyle,
            CellStyle alternateDataStyle, CellStyle numberStyle, List<AvoidedElectricityProduction> data) {
        String[] headers = {
                "Year",
                "Units Installed This Year",
                "Cumulative Units Installed",
                "Annual Avoided Electricity (MWh)",
                "Cumulative Avoided Electricity (MWh)",
                "Net GHG Mitigation (tCO2e)",
                "Net GHG Mitigation (ktCO2e)"
        };
        createHeader(sheet, headerStyle, headers);
        DataFormat dataFormat = sheet.getWorkbook().createDataFormat();
        int rowIdx = 1;
        for (int i = 0; i < data.size(); i++) {
            AvoidedElectricityProduction item = data.get(i);
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
            r.createCell(1).setCellValue(item.getUnitsInstalledThisYear());
            r.getCell(1).setCellStyle(numStyle);
            r.createCell(2).setCellValue(item.getCumulativeUnitsInstalled());
            r.getCell(2).setCellStyle(numStyle);
            r.createCell(3).setCellValue(item.getAnnualAvoidedElectricity());
            r.getCell(3).setCellStyle(numStyle);
            r.createCell(4).setCellValue(item.getCumulativeAvoidedElectricity());
            r.getCell(4).setCellStyle(numStyle);
            r.createCell(5).setCellValue(item.getNetGhGMitigation() != null ? item.getNetGhGMitigation() : 0.0);
            r.getCell(5).setCellStyle(numStyle);
            r.createCell(6).setCellValue(
                    item.getNetGhGMitigation() != null ? convertToKtCO2e(item.getNetGhGMitigation()) : 0.0);
            r.getCell(6).setCellStyle(numStyle);
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
