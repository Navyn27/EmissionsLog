package com.navyn.emissionlog.modules.mitigationProjects.Waste;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.repository.KigaliFSTPMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.repository.KigaliWWTPMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.repository.EPRPlasticWasteMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.repository.ISWMMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.repository.LandfillGasUtilizationMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.repository.MBTCompostingMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.repository.WasteToEnergyMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.dto.WasteDashboardSummaryDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.dto.WasteDashboardYearDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.models.KigaliFSTPMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.models.KigaliWWTPMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.models.EPRPlasticWasteMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.models.ISWMMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.models.LandfillGasUtilizationMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.models.MBTCompostingMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.models.WasteToEnergyMitigation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xddf.usermodel.chart.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class WasteDashboardServiceImpl implements WasteDashboardService {

        private final WasteToEnergyMitigationRepository wasteToEnergyRepository;
        private final MBTCompostingMitigationRepository mbtCompostingRepository;
        private final LandfillGasUtilizationMitigationRepository landfillGasUtilizationRepository;
        private final EPRPlasticWasteMitigationRepository eprPlasticWasteRepository;
        private final KigaliFSTPMitigationRepository kigaliFSTPRepository;
        private final KigaliWWTPMitigationRepository kigaliWWTPRepository;
        private final ISWMMitigationRepository iswmMitigationRepository;

        @Override
        public WasteDashboardSummaryDto getWasteDashboardSummary(Integer startingYear, Integer endingYear) {
                List<WasteToEnergyMitigation> wasteToEnergy = wasteToEnergyRepository.findAll();
                List<MBTCompostingMitigation> mbt = mbtCompostingRepository.findAll();
                List<LandfillGasUtilizationMitigation> landfill = landfillGasUtilizationRepository.findAll();
                List<EPRPlasticWasteMitigation> epr = eprPlasticWasteRepository.findAll();
                List<KigaliFSTPMitigation> fstp = kigaliFSTPRepository.findAll();
                List<KigaliWWTPMitigation> wwtp = kigaliWWTPRepository.findAll();
                List<ISWMMitigation> iswm = iswmMitigationRepository.findAll();

                if (startingYear != null && endingYear != null) {
                        wasteToEnergy = filterByYear(wasteToEnergy, WasteToEnergyMitigation::getYear, startingYear,
                                        endingYear);
                        mbt = filterByYear(mbt, MBTCompostingMitigation::getYear, startingYear, endingYear);
                        landfill = filterByYear(landfill, LandfillGasUtilizationMitigation::getYear, startingYear,
                                        endingYear);
                        epr = filterByYear(epr, EPRPlasticWasteMitigation::getYear, startingYear, endingYear);
                        fstp = filterByYear(fstp, KigaliFSTPMitigation::getYear, startingYear, endingYear);
                        wwtp = filterByYear(wwtp, KigaliWWTPMitigation::getYear, startingYear, endingYear);
                        iswm = filterByYear(iswm, ISWMMitigation::getYear, startingYear, endingYear);
                }

                double wasteToEnergyTotal = sumDouble(wasteToEnergy,
                                WasteToEnergyMitigation::getGhgReductionKilotonnes);
                double mbtTotal = sumDouble(mbt, MBTCompostingMitigation::getEstimatedGhgReductionKilotonnesPerYear);
                double landfillTotal = sumDouble(landfill,
                                LandfillGasUtilizationMitigation::getProjectReductionEmissions);
                double eprTotal = sumDouble(epr, EPRPlasticWasteMitigation::getGhgReductionKilotonnes);
                double fstpTotal = sumDouble(fstp, KigaliFSTPMitigation::getAnnualEmissionsReductionKilotonnes);
                double wwtpTotal = sumDouble(wwtp, KigaliWWTPMitigation::getAnnualEmissionsReductionKilotonnes);
                double iswmTotal = sumDouble(iswm, ISWMMitigation::getNetAnnualReduction);

                double totalMitigation = wasteToEnergyTotal + mbtTotal + landfillTotal + eprTotal + fstpTotal
                                + wwtpTotal
                                + iswmTotal;

                WasteDashboardSummaryDto dto = new WasteDashboardSummaryDto();
                dto.setStartingYear(startingYear);
                dto.setEndingYear(endingYear);
                dto.setWasteToEnergy(wasteToEnergyTotal);
                dto.setMbtComposting(mbtTotal);
                dto.setLandfillGasUtilization(landfillTotal);
                dto.setEprPlasticWaste(eprTotal);
                dto.setKigaliFSTP(fstpTotal);
                dto.setKigaliWWTP(wwtpTotal);
                dto.setIswm(iswmTotal);
                dto.setTotalMitigationKtCO2e(totalMitigation);

                return dto;
        }

        @Override
        public List<WasteDashboardYearDto> getWasteDashboardGraph(Integer startingYear, Integer endingYear) {
                int currentYear = LocalDateTime.now().getYear();
                int start = startingYear != null ? startingYear : currentYear - 4;
                int end = endingYear != null ? endingYear : currentYear;

                if (start > end) {
                        int tmp = start;
                        start = end;
                        end = tmp;
                }

                List<WasteToEnergyMitigation> wasteToEnergy = wasteToEnergyRepository.findAll();
                List<MBTCompostingMitigation> mbt = mbtCompostingRepository.findAll();
                List<LandfillGasUtilizationMitigation> landfill = landfillGasUtilizationRepository.findAll();
                List<EPRPlasticWasteMitigation> epr = eprPlasticWasteRepository.findAll();
                List<KigaliFSTPMitigation> fstp = kigaliFSTPRepository.findAll();
                List<KigaliWWTPMitigation> wwtp = kigaliWWTPRepository.findAll();
                List<ISWMMitigation> iswm = iswmMitigationRepository.findAll();

                List<WasteDashboardYearDto> response = new ArrayList<>();
                for (int year = start; year <= end; year++) {
                        double wasteToEnergyTotal = sumDouble(
                                        filterByYear(wasteToEnergy, WasteToEnergyMitigation::getYear, year, year),
                                        WasteToEnergyMitigation::getGhgReductionKilotonnes);
                        double mbtTotal = sumDouble(filterByYear(mbt, MBTCompostingMitigation::getYear, year, year),
                                        MBTCompostingMitigation::getEstimatedGhgReductionKilotonnesPerYear);
                        double landfillTotal = sumDouble(
                                        filterByYear(landfill, LandfillGasUtilizationMitigation::getYear, year, year),
                                        LandfillGasUtilizationMitigation::getProjectReductionEmissions);
                        double eprTotal = sumDouble(filterByYear(epr, EPRPlasticWasteMitigation::getYear, year, year),
                                        EPRPlasticWasteMitigation::getGhgReductionKilotonnes);
                        double fstpTotal = sumDouble(filterByYear(fstp, KigaliFSTPMitigation::getYear, year, year),
                                        KigaliFSTPMitigation::getAnnualEmissionsReductionKilotonnes);
                        double wwtpTotal = sumDouble(filterByYear(wwtp, KigaliWWTPMitigation::getYear, year, year),
                                        KigaliWWTPMitigation::getAnnualEmissionsReductionKilotonnes);
                        double iswmTotal = sumDouble(filterByYear(iswm, ISWMMitigation::getYear, year, year),
                                        ISWMMitigation::getNetAnnualReduction);

                        double totalMitigation = wasteToEnergyTotal + mbtTotal + landfillTotal + eprTotal + fstpTotal
                                        + wwtpTotal
                                        + iswmTotal;

                        WasteDashboardYearDto dto = new WasteDashboardYearDto();
                        dto.setYear(year);
                        dto.setWasteToEnergy(wasteToEnergyTotal);
                        dto.setMbtComposting(mbtTotal);
                        dto.setLandfillGasUtilization(landfillTotal);
                        dto.setEprPlasticWaste(eprTotal);
                        dto.setKigaliFSTP(fstpTotal);
                        dto.setKigaliWWTP(wwtpTotal);
                        dto.setIswm(iswmTotal);
                        dto.setTotalMitigationKtCO2e(totalMitigation);

                        response.add(dto);
                }

                return response;
        }

        private <T> List<T> filterByYear(List<T> source, Function<T, Integer> yearExtractor, Integer start,
                        Integer end) {
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
                                .filter(value -> value != null)
                                .mapToDouble(Double::doubleValue)
                                .sum();
        }

        @Override
        public byte[] exportWasteDashboard(Integer startingYear, Integer endingYear) {
                List<WasteToEnergyMitigation> wasteToEnergy = wasteToEnergyRepository.findAll();
                List<MBTCompostingMitigation> mbt = mbtCompostingRepository.findAll();
                List<LandfillGasUtilizationMitigation> landfill = landfillGasUtilizationRepository.findAll();
                List<EPRPlasticWasteMitigation> epr = eprPlasticWasteRepository.findAll();
                List<KigaliFSTPMitigation> fstp = kigaliFSTPRepository.findAll();
                List<KigaliWWTPMitigation> wwtp = kigaliWWTPRepository.findAll();
                List<ISWMMitigation> iswm = iswmMitigationRepository.findAll();

                int minYear = Stream.of(
                                wasteToEnergy.stream().map(WasteToEnergyMitigation::getYear),
                                mbt.stream().map(MBTCompostingMitigation::getYear),
                                landfill.stream().map(LandfillGasUtilizationMitigation::getYear),
                                epr.stream().map(EPRPlasticWasteMitigation::getYear),
                                fstp.stream().map(KigaliFSTPMitigation::getYear),
                                wwtp.stream().map(KigaliWWTPMitigation::getYear),
                                iswm.stream().map(ISWMMitigation::getYear))
                                .flatMap(s -> s)
                                .filter(y -> y != null)
                                .min(Comparator.naturalOrder())
                                .orElse(LocalDateTime.now().getYear());

                int maxYear = Stream.of(
                                wasteToEnergy.stream().map(WasteToEnergyMitigation::getYear),
                                mbt.stream().map(MBTCompostingMitigation::getYear),
                                landfill.stream().map(LandfillGasUtilizationMitigation::getYear),
                                epr.stream().map(EPRPlasticWasteMitigation::getYear),
                                fstp.stream().map(KigaliFSTPMitigation::getYear),
                                wwtp.stream().map(KigaliWWTPMitigation::getYear),
                                iswm.stream().map(ISWMMitigation::getYear))
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

                wasteToEnergy = filterByYear(wasteToEnergy, WasteToEnergyMitigation::getYear, start, end);
                mbt = filterByYear(mbt, MBTCompostingMitigation::getYear, start, end);
                landfill = filterByYear(landfill, LandfillGasUtilizationMitigation::getYear, start, end);
                epr = filterByYear(epr, EPRPlasticWasteMitigation::getYear, start, end);
                fstp = filterByYear(fstp, KigaliFSTPMitigation::getYear, start, end);
                wwtp = filterByYear(wwtp, KigaliWWTPMitigation::getYear, start, end);
                iswm = filterByYear(iswm, ISWMMitigation::getYear, start, end);

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
                                        wasteToEnergy, mbt, landfill, epr, fstp, wwtp, iswm, creationHelper);

                        buildWasteToEnergySheet(workbook.createSheet("Waste-to-Energy"), headerStyle, dataStyle,
                                        alternateDataStyle, numberStyle, wasteToEnergy);
                        buildMBTSheet(workbook.createSheet("MBT Composting"), headerStyle, dataStyle,
                                        alternateDataStyle, numberStyle, mbt);
                        buildLandfillSheet(workbook.createSheet("Landfill Gas Utilization"), headerStyle, dataStyle,
                                        alternateDataStyle, numberStyle, landfill);
                        buildEprSheet(workbook.createSheet("EPR Plastic Waste"), headerStyle, dataStyle,
                                        alternateDataStyle, numberStyle, epr);
                        buildKigaliWwtpSheet(workbook.createSheet("Kigali WWTP"), headerStyle, dataStyle,
                                        alternateDataStyle, numberStyle, wwtp);
                        buildKigaliFstpSheet(workbook.createSheet("Kigali FSTP"), headerStyle, dataStyle,
                                        alternateDataStyle, numberStyle, fstp);
                        buildIswmSheet(workbook.createSheet("ISWM"), headerStyle, dataStyle,
                                        alternateDataStyle, numberStyle, iswm);

                        workbook.write(baos);
                        return baos.toByteArray();
                } catch (Exception e) {
                        throw new RuntimeException("Failed to generate waste dashboard export", e);
                }
        }

        // Style creation methods - Professional styling
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
                // Border colors will use defaults for cleaner appearance
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
                // Border colors will use defaults for cleaner appearance
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
                // Border colors will use defaults for cleaner appearance
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
                // Border colors will use defaults for cleaner appearance
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
                // Border colors will use defaults for cleaner appearance
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
                // Border colors will use defaults for cleaner appearance
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
                        List<WasteToEnergyMitigation> wasteToEnergy,
                        List<MBTCompostingMitigation> mbt,
                        List<LandfillGasUtilizationMitigation> landfill,
                        List<EPRPlasticWasteMitigation> epr,
                        List<KigaliFSTPMitigation> fstp,
                        List<KigaliWWTPMitigation> wwtp,
                        List<ISWMMitigation> iswm,
                        CreationHelper creationHelper) {
                int rowIdx = 0;

                // Title row
                Row title = sheet.createRow(rowIdx++);
                Cell titleCell = title.createCell(0);
                titleCell.setCellValue("Waste Mitigation Dashboard Summary");
                titleCell.setCellStyle(titleStyle);
                sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));
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

                double wasteToEnergyTotal = sumDouble(wasteToEnergy,
                                WasteToEnergyMitigation::getGhgReductionKilotonnes);
                double mbtTotal = sumDouble(mbt, MBTCompostingMitigation::getEstimatedGhgReductionKilotonnesPerYear);
                double landfillTotal = sumDouble(landfill,
                                LandfillGasUtilizationMitigation::getProjectReductionEmissions);
                double eprTotal = sumDouble(epr, EPRPlasticWasteMitigation::getGhgReductionKilotonnes);
                double fstpTotal = sumDouble(fstp, KigaliFSTPMitigation::getAnnualEmissionsReductionKilotonnes);
                double wwtpTotal = sumDouble(wwtp, KigaliWWTPMitigation::getAnnualEmissionsReductionKilotonnes);
                double iswmTotal = sumDouble(iswm, ISWMMitigation::getNetAnnualReduction);
                double totalMitigation = wasteToEnergyTotal + mbtTotal + landfillTotal + eprTotal + fstpTotal
                                + wwtpTotal
                                + iswmTotal;

                rowIdx++; // Blank row

                // Totals Summary Section
                Row totalsHeader = sheet.createRow(rowIdx++);
                totalsHeader.setHeightInPoints(20);
                String[] totalLabels = new String[] {
                                "Project",
                                "Total Mitigation (ktCO2e)"
                };
                String[] projectNames = new String[] {
                                "Waste-to-Energy",
                                "MBT Composting",
                                "Landfill Gas Utilization",
                                "EPR Plastic Waste",
                                "Kigali FSTP",
                                "Kigali WWTP",
                                "ISWM",
                                "TOTAL"
                };
                double[] totalValues = new double[] {
                                wasteToEnergyTotal,
                                mbtTotal,
                                landfillTotal,
                                eprTotal,
                                fstpTotal,
                                wwtpTotal,
                                iswmTotal,
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
                        // Border colors will use defaults for cleaner appearance
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
                                "Waste-to-Energy",
                                "MBT Composting",
                                "Landfill Gas Utilization",
                                "EPR Plastic Waste",
                                "Kigali FSTP",
                                "Kigali WWTP",
                                "ISWM",
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

                        double wte = sumDouble(
                                        filterByYear(wasteToEnergy, WasteToEnergyMitigation::getYear, year, year),
                                        WasteToEnergyMitigation::getGhgReductionKilotonnes);
                        double mbtVal = sumDouble(filterByYear(mbt, MBTCompostingMitigation::getYear, year, year),
                                        MBTCompostingMitigation::getEstimatedGhgReductionKilotonnesPerYear);
                        double landfillVal = sumDouble(
                                        filterByYear(landfill, LandfillGasUtilizationMitigation::getYear, year, year),
                                        LandfillGasUtilizationMitigation::getProjectReductionEmissions);
                        double eprVal = sumDouble(filterByYear(epr, EPRPlasticWasteMitigation::getYear, year, year),
                                        EPRPlasticWasteMitigation::getGhgReductionKilotonnes);
                        double fstpVal = sumDouble(filterByYear(fstp, KigaliFSTPMitigation::getYear, year, year),
                                        KigaliFSTPMitigation::getAnnualEmissionsReductionKilotonnes);
                        double wwtpVal = sumDouble(filterByYear(wwtp, KigaliWWTPMitigation::getYear, year, year),
                                        KigaliWWTPMitigation::getAnnualEmissionsReductionKilotonnes);
                        double iswmVal = sumDouble(filterByYear(iswm, ISWMMitigation::getYear, year, year),
                                        ISWMMitigation::getNetAnnualReduction);
                        double total = wte + mbtVal + landfillVal + eprVal + fstpVal + wwtpVal + iswmVal;

                        // Year column - left aligned
                        Cell yearCell = r.createCell(0);
                        yearCell.setCellValue(year);
                        CellStyle baseYearStyle = isAlternate ? alternateDataStyle : dataStyle;
                        CellStyle yearCellStyle = sheet.getWorkbook().createCellStyle();
                        yearCellStyle.cloneStyleFrom(baseYearStyle);
                        yearCellStyle.setAlignment(HorizontalAlignment.CENTER);
                        yearCellStyle.setDataFormat(dataFormat.getFormat("0"));
                        yearCell.setCellStyle(yearCellStyle);

                        // Number columns
                        CellStyle numStyle = isAlternate ? createAlternateNumberStyle(sheet.getWorkbook())
                                        : numberStyle;
                        r.createCell(1).setCellValue(wte);
                        r.getCell(1).setCellStyle(numStyle);
                        r.createCell(2).setCellValue(mbtVal);
                        r.getCell(2).setCellStyle(numStyle);
                        r.createCell(3).setCellValue(landfillVal);
                        r.getCell(3).setCellStyle(numStyle);
                        r.createCell(4).setCellValue(eprVal);
                        r.getCell(4).setCellStyle(numStyle);
                        r.createCell(5).setCellValue(fstpVal);
                        r.getCell(5).setCellStyle(numStyle);
                        r.createCell(6).setCellValue(wwtpVal);
                        r.getCell(6).setCellStyle(numStyle);
                        r.createCell(7).setCellValue(iswmVal);
                        r.getCell(7).setCellStyle(numStyle);

                        // Total column - bold
                        Cell totalCell = r.createCell(8);
                        totalCell.setCellValue(total);
                        CellStyle totalStyle = sheet.getWorkbook().createCellStyle();
                        totalStyle.cloneStyleFrom(numStyle);
                        Font totalFont = sheet.getWorkbook().createFont();
                        totalFont.setBold(true);
                        totalStyle.setFont(totalFont);
                        totalCell.setCellStyle(totalStyle);
                }

                // Auto-size columns with minimum and maximum widths
                for (int i = 0; i < header.length; i++) {
                        sheet.autoSizeColumn(i);
                        int currentWidth = sheet.getColumnWidth(i);
                        int minWidth = 2500; // Minimum width
                        int maxWidth = 18000; // Maximum width
                        if (i == 0) { // Year column
                                minWidth = 1500;
                        }
                        if (currentWidth < minWidth) {
                                sheet.setColumnWidth(i, minWidth);
                        } else if (currentWidth > maxWidth) {
                                sheet.setColumnWidth(i, maxWidth);
                        }
                }

                // Chart (stacked column) using per-year table
                int dataEndRow = rowIdx - 1;
                if (dataEndRow >= dataStartRow) {
                        XSSFDrawing drawing = sheet.createDrawingPatriarch();
                        XSSFClientAnchor anchor = new XSSFClientAnchor();
                        anchor.setCol1(0);
                        anchor.setRow1(dataEndRow + 2);
                        anchor.setCol2(10); // Increased from 8 to 10 for wider chart
                        anchor.setRow2(dataEndRow + 25); // Increased from 18 to 25 for taller chart

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
                        // Make bars wider and more readable
                        bar.setGapWidth(75); // Reduced gap between bar groups (default is usually 150-200)
                        // Note: setOverlap removed due to POI schema compatibility issues

                        for (int c = 1; c <= 7; c++) { // per-project columns only
                                CellRangeAddress valuesRange = new CellRangeAddress(dataStartRow, dataEndRow, c, c);
                                XDDFNumericalDataSource<Double> values = XDDFDataSourcesFactory
                                                .fromNumericCellRange(sheet, valuesRange);
                                XDDFBarChartData.Series series = (XDDFBarChartData.Series) bar.addSeries(categories,
                                                values);
                                series.setTitle(header[c], null);
                        }

                        chart.plot(barData);
                }
        }

        private void buildWasteToEnergySheet(XSSFSheet sheet, CellStyle headerStyle, CellStyle dataStyle,
                        CellStyle alternateDataStyle, CellStyle numberStyle, List<WasteToEnergyMitigation> data) {
                String[] headers = {
                                "Year",
                                "Waste to WtE (t/year)",
                                "Project Intervention",
                                "GHG Reduction (ktCO2e)",
                                "Adjusted Emissions With WtE (ktCO2e)"
                };
                createHeader(sheet, headerStyle, headers);
                DataFormat dataFormat = sheet.getWorkbook().createDataFormat();
                int rowIdx = 1;
                for (int i = 0; i < data.size(); i++) {
                        WasteToEnergyMitigation item = data.get(i);
                        Row r = sheet.createRow(rowIdx++);
                        r.setHeightInPoints(18);
                        boolean isAlternate = i % 2 == 1;

                        // Year column
                        Cell yearCell = r.createCell(0);
                        yearCell.setCellValue(item.getYear());
                        CellStyle baseYearStyle = isAlternate ? alternateDataStyle : dataStyle;
                        CellStyle yearCellStyle = sheet.getWorkbook().createCellStyle();
                        yearCellStyle.cloneStyleFrom(baseYearStyle);
                        yearCellStyle.setAlignment(HorizontalAlignment.CENTER);
                        yearCellStyle.setDataFormat(dataFormat.getFormat("0"));
                        yearCell.setCellStyle(yearCellStyle);

                        // Number columns
                        CellStyle numStyle = isAlternate ? createAlternateNumberStyle(sheet.getWorkbook())
                                        : numberStyle;
                        r.createCell(1).setCellValue(item.getWasteToWtE());
                        r.getCell(1).setCellStyle(numStyle);
                        
                        // Project Intervention column (text)
                        Cell interventionCell = r.createCell(2);
                        interventionCell.setCellValue(
                                        item.getProjectIntervention() != null ? item.getProjectIntervention().getName() : "");
                        CellStyle baseTextStyle = isAlternate ? alternateDataStyle : dataStyle;
                        CellStyle textCellStyle = sheet.getWorkbook().createCellStyle();
                        textCellStyle.cloneStyleFrom(baseTextStyle);
                        textCellStyle.setAlignment(HorizontalAlignment.LEFT);
                        interventionCell.setCellStyle(textCellStyle);
                        
                        r.createCell(3).setCellValue(item.getGhgReductionKilotonnes());
                        r.getCell(3).setCellStyle(numStyle);
                        r.createCell(4).setCellValue(item.getAdjustedEmissionsWithWtE());
                        r.getCell(4).setCellStyle(numStyle);
                }
                autoSizeWithLimits(sheet, headers.length);
        }

        private void buildMBTSheet(XSSFSheet sheet, CellStyle headerStyle, CellStyle dataStyle,
                        CellStyle alternateDataStyle, CellStyle numberStyle, List<MBTCompostingMitigation> data) {
                String[] headers = {
                                "Year",
                                "Operation Status",
                                "Organic Waste Treated (t/day)",
                                "Organic Waste Treated (t/year)",
                                "Estimated GHG Reduction (kt/year)",
                                "Adjusted BAU Emission (ktCO2e)"
                };
                createHeader(sheet, headerStyle, headers);
                DataFormat dataFormat = sheet.getWorkbook().createDataFormat();
                int rowIdx = 1;
                for (int i = 0; i < data.size(); i++) {
                        MBTCompostingMitigation item = data.get(i);
                        Row r = sheet.createRow(rowIdx++);
                        r.setHeightInPoints(18);
                        boolean isAlternate = i % 2 == 1;

                        // Year column
                        Cell yearCell = r.createCell(0);
                        yearCell.setCellValue(item.getYear());
                        CellStyle baseYearStyle = isAlternate ? alternateDataStyle : dataStyle;
                        CellStyle yearCellStyle = sheet.getWorkbook().createCellStyle();
                        yearCellStyle.cloneStyleFrom(baseYearStyle);
                        yearCellStyle.setAlignment(HorizontalAlignment.CENTER);
                        yearCellStyle.setDataFormat(dataFormat.getFormat("0"));
                        yearCell.setCellStyle(yearCellStyle);

                        // Text column (Operation Status)
                        Cell textCell = r.createCell(1);
                        textCell.setCellValue(
                                        item.getOperationStatus() != null ? item.getOperationStatus().name() : "");
                        CellStyle baseTextStyle = isAlternate ? alternateDataStyle : dataStyle;
                        CellStyle textCellStyle = sheet.getWorkbook().createCellStyle();
                        textCellStyle.cloneStyleFrom(baseTextStyle);
                        textCellStyle.setAlignment(HorizontalAlignment.LEFT);
                        textCell.setCellStyle(textCellStyle);

                        // Number columns
                        CellStyle numStyle = isAlternate ? createAlternateNumberStyle(sheet.getWorkbook())
                                        : numberStyle;
                        r.createCell(2).setCellValue(item.getOrganicWasteTreatedTonsPerDay());
                        r.getCell(2).setCellStyle(numStyle);
                        r.createCell(3).setCellValue(item.getOrganicWasteTreatedTonsPerYear());
                        r.getCell(3).setCellStyle(numStyle);
                        r.createCell(4).setCellValue(item.getEstimatedGhgReductionKilotonnesPerYear());
                        r.getCell(4).setCellStyle(numStyle);
                        r.createCell(5).setCellValue(item.getAdjustedBauEmissionBiologicalTreatment());
                        r.getCell(5).setCellStyle(numStyle);
                }
                autoSizeWithLimits(sheet, headers.length);
        }

        private void buildLandfillSheet(XSSFSheet sheet, CellStyle headerStyle, CellStyle dataStyle,
                        CellStyle alternateDataStyle, CellStyle numberStyle,
                        List<LandfillGasUtilizationMitigation> data) {
                String[] headers = {
                                "Year",
                                "CH₄ Captured",
                                "CH₄ Destroyed",
                                "Equivalent CO₂e Reduction (ktCO2e)",
                                "Mitigation Scenario Grand (ktCO2e)"
                };
                createHeader(sheet, headerStyle, headers);
                DataFormat dataFormat = sheet.getWorkbook().createDataFormat();
                int rowIdx = 1;
                for (int i = 0; i < data.size(); i++) {
                        LandfillGasUtilizationMitigation item = data.get(i);
                        Row r = sheet.createRow(rowIdx++);
                        r.setHeightInPoints(18);
                        boolean isAlternate = i % 2 == 1;

                        // Year column
                        Cell yearCell = r.createCell(0);
                        yearCell.setCellValue(item.getYear());
                        CellStyle baseYearStyle = isAlternate ? alternateDataStyle : dataStyle;
                        CellStyle yearCellStyle = sheet.getWorkbook().createCellStyle();
                        yearCellStyle.cloneStyleFrom(baseYearStyle);
                        yearCellStyle.setAlignment(HorizontalAlignment.CENTER);
                        yearCellStyle.setDataFormat(dataFormat.getFormat("0"));
                        yearCell.setCellStyle(yearCellStyle);

                        // Number columns
                        CellStyle numStyle = isAlternate ? createAlternateNumberStyle(sheet.getWorkbook())
                                        : numberStyle;
                        r.createCell(1).setCellValue(
                                        item.getCh4Captured() != null ? item.getCh4Captured() : 0.0);
                        r.getCell(1).setCellStyle(numStyle);
                        r.createCell(2).setCellValue(item.getCh4Destroyed() != null ? item.getCh4Destroyed() : 0.0);
                        r.getCell(2).setCellStyle(numStyle);
                        r.createCell(3).setCellValue(item.getProjectReductionEmissions() != null
                                        ? item.getProjectReductionEmissions()
                                        : 0.0);
                        r.getCell(3).setCellStyle(numStyle);
                        r.createCell(4).setCellValue(
                                        item.getMitigationScenarioGrand() != null ? item.getMitigationScenarioGrand()
                                                        : 0.0);
                        r.getCell(4).setCellStyle(numStyle);
                }
                autoSizeWithLimits(sheet, headers.length);
        }

        private void buildEprSheet(XSSFSheet sheet, CellStyle headerStyle, CellStyle dataStyle,
                        CellStyle alternateDataStyle, CellStyle numberStyle, List<EPRPlasticWasteMitigation> data) {
                String[] headers = {
                                "Year",
                                "BAU Solid Waste Emissions (ktCO2e)",
                                "Plastic Waste Growth Factor",
                                "Recycling Rate With EPR",
                                "Plastic Waste (t/year)",
                                "Recycled With EPR (t/year)",
                                "Additional Recycling vs BAU (t/year)",
                                "GHG Reduction (ktCO2e)"
                };
                createHeader(sheet, headerStyle, headers);
                DataFormat dataFormat = sheet.getWorkbook().createDataFormat();
                int rowIdx = 1;
                for (int i = 0; i < data.size(); i++) {
                        EPRPlasticWasteMitigation item = data.get(i);
                        Row r = sheet.createRow(rowIdx++);
                        r.setHeightInPoints(18);
                        boolean isAlternate = i % 2 == 1;

                        // Year column
                        Cell yearCell = r.createCell(0);
                        yearCell.setCellValue(item.getYear());
                        CellStyle baseYearStyle = isAlternate ? alternateDataStyle : dataStyle;
                        CellStyle yearCellStyle = sheet.getWorkbook().createCellStyle();
                        yearCellStyle.cloneStyleFrom(baseYearStyle);
                        yearCellStyle.setAlignment(HorizontalAlignment.CENTER);
                        yearCellStyle.setDataFormat(dataFormat.getFormat("0"));
                        yearCell.setCellStyle(yearCellStyle);

                        // Number columns
                        CellStyle numStyle = isAlternate ? createAlternateNumberStyle(sheet.getWorkbook())
                                        : numberStyle;
                        r.createCell(1).setCellValue(
                                        item.getBauSolidWasteEmissions() != null ? item.getBauSolidWasteEmissions()
                                                        : 0.0);
                        r.getCell(1).setCellStyle(numStyle);
                        r.createCell(2).setCellValue(
                                        item.getPlasticWasteGrowthFactor() != null ? item.getPlasticWasteGrowthFactor()
                                                        : 0.0);
                        r.getCell(2).setCellStyle(numStyle);
                        r.createCell(3).setCellValue(
                                        item.getRecyclingRateWithEPR() != null ? item.getRecyclingRateWithEPR() : 0.0);
                        r.getCell(3).setCellStyle(numStyle);
                        r.createCell(4).setCellValue(item.getPlasticWasteTonnesPerYear() != null
                                        ? item.getPlasticWasteTonnesPerYear()
                                        : 0.0);
                        r.getCell(4).setCellStyle(numStyle);
                        r.createCell(5).setCellValue(item.getRecycledPlasticWithEPRTonnesPerYear() != null
                                        ? item.getRecycledPlasticWithEPRTonnesPerYear()
                                        : 0.0);
                        r.getCell(5).setCellStyle(numStyle);
                        r.createCell(6).setCellValue(item.getAdditionalRecyclingVsBAUTonnesPerYear() != null
                                        ? item.getAdditionalRecyclingVsBAUTonnesPerYear()
                                        : 0.0);
                        r.getCell(6).setCellStyle(numStyle);
                        r.createCell(7).setCellValue(
                                        item.getGhgReductionKilotonnes() != null ? item.getGhgReductionKilotonnes()
                                                        : 0.0);
                        r.getCell(7).setCellStyle(numStyle);
                }
                autoSizeWithLimits(sheet, headers.length);
        }

        private void buildKigaliWwtpSheet(XSSFSheet sheet, CellStyle headerStyle, CellStyle dataStyle,
                        CellStyle alternateDataStyle, CellStyle numberStyle, List<KigaliWWTPMitigation> data) {
                String[] headers = {
                                "Year",
                                "Annual Wastewater Treated (m3/year)",
                                "Project Intervention",
                                "Methane Potential",
                                "CO₂e per m³ Wastewater",
                                "Annual Emissions Reduction (tCO2e)",
                                "Annual Emissions Reduction (ktCO2e)",
                                "Adjusted BAU Emission Mitigation (ktCO2e)"
                };
                createHeader(sheet, headerStyle, headers);
                DataFormat dataFormat = sheet.getWorkbook().createDataFormat();
                int rowIdx = 1;
                for (int i = 0; i < data.size(); i++) {
                        KigaliWWTPMitigation item = data.get(i);
                        Row r = sheet.createRow(rowIdx++);
                        r.setHeightInPoints(18);
                        boolean isAlternate = i % 2 == 1;

                        // Year column
                        Cell yearCell = r.createCell(0);
                        yearCell.setCellValue(item.getYear());
                        CellStyle baseYearStyle = isAlternate ? alternateDataStyle : dataStyle;
                        CellStyle yearCellStyle = sheet.getWorkbook().createCellStyle();
                        yearCellStyle.cloneStyleFrom(baseYearStyle);
                        yearCellStyle.setAlignment(HorizontalAlignment.CENTER);
                        yearCellStyle.setDataFormat(dataFormat.getFormat("0"));
                        yearCell.setCellStyle(yearCellStyle);

                        // Number columns
                        CellStyle numStyle = isAlternate ? createAlternateNumberStyle(sheet.getWorkbook())
                                        : numberStyle;
                        r.createCell(1).setCellValue(
                                        item.getAnnualWastewaterTreated() != null ? item.getAnnualWastewaterTreated() : 0.0);
                        r.getCell(1).setCellStyle(numStyle);
                        
                        // Project Intervention column (text)
                        Cell interventionCell = r.createCell(2);
                        interventionCell.setCellValue(
                                        item.getProjectIntervention() != null ? item.getProjectIntervention().getName() : "");
                        CellStyle baseTextStyle = isAlternate ? alternateDataStyle : dataStyle;
                        CellStyle textCellStyle = sheet.getWorkbook().createCellStyle();
                        textCellStyle.cloneStyleFrom(baseTextStyle);
                        textCellStyle.setAlignment(HorizontalAlignment.LEFT);
                        interventionCell.setCellStyle(textCellStyle);
                        
                        r.createCell(3).setCellValue(
                                        item.getMethanePotential() != null ? item.getMethanePotential() : 0.0);
                        r.getCell(3).setCellStyle(numStyle);
                        r.createCell(4).setCellValue(
                                        item.getCo2ePerM3Wastewater() != null ? item.getCo2ePerM3Wastewater() : 0.0);
                        r.getCell(4).setCellStyle(numStyle);
                        r.createCell(5).setCellValue(item.getAnnualEmissionsReductionTonnes() != null
                                        ? item.getAnnualEmissionsReductionTonnes()
                                        : 0.0);
                        r.getCell(5).setCellStyle(numStyle);
                        r.createCell(6).setCellValue(item.getAnnualEmissionsReductionKilotonnes() != null
                                        ? item.getAnnualEmissionsReductionKilotonnes()
                                        : 0.0);
                        r.getCell(6).setCellStyle(numStyle);
                        r.createCell(7).setCellValue(item.getAdjustedBauEmissionMitigation() != null
                                        ? item.getAdjustedBauEmissionMitigation()
                                        : 0.0);
                        r.getCell(7).setCellStyle(numStyle);
                }
                autoSizeWithLimits(sheet, headers.length);
        }

        private void buildKigaliFstpSheet(XSSFSheet sheet, CellStyle headerStyle, CellStyle dataStyle,
                        CellStyle alternateDataStyle, CellStyle numberStyle, List<KigaliFSTPMitigation> data) {
                String[] headers = {
                                "Year",
                                "Project Phase",
                                "Phase Capacity (m3/day)",
                                "Operational Efficiency",
                                "Effective Daily Treatment (m3/day)",
                                "Annual Sludge Treated (m3/year)",
                                "Annual Emissions Reduction (ktCO2e)"
                };
                createHeader(sheet, headerStyle, headers);
                DataFormat dataFormat = sheet.getWorkbook().createDataFormat();
                int rowIdx = 1;
                for (int i = 0; i < data.size(); i++) {
                        KigaliFSTPMitigation item = data.get(i);
                        Row r = sheet.createRow(rowIdx++);
                        r.setHeightInPoints(18);
                        boolean isAlternate = i % 2 == 1;

                        // Year column
                        Cell yearCell = r.createCell(0);
                        yearCell.setCellValue(item.getYear());
                        CellStyle baseYearStyle = isAlternate ? alternateDataStyle : dataStyle;
                        CellStyle yearCellStyle = sheet.getWorkbook().createCellStyle();
                        yearCellStyle.cloneStyleFrom(baseYearStyle);
                        yearCellStyle.setAlignment(HorizontalAlignment.CENTER);
                        yearCellStyle.setDataFormat(dataFormat.getFormat("0"));
                        yearCell.setCellStyle(yearCellStyle);

                        // Text column (Project Phase)
                        Cell textCell = r.createCell(1);
                        textCell.setCellValue(
                                        item.getProjectPhase() != null ? item.getProjectPhase().name() : "");
                        CellStyle baseTextStyle = isAlternate ? alternateDataStyle : dataStyle;
                        CellStyle textCellStyle = sheet.getWorkbook().createCellStyle();
                        textCellStyle.cloneStyleFrom(baseTextStyle);
                        textCellStyle.setAlignment(HorizontalAlignment.LEFT);
                        textCell.setCellStyle(textCellStyle);

                        // Number columns
                        CellStyle numStyle = isAlternate ? createAlternateNumberStyle(sheet.getWorkbook())
                                        : numberStyle;
                        r.createCell(2).setCellValue(
                                        item.getPhaseCapacityPerDay() != null ? item.getPhaseCapacityPerDay() : 0.0);
                        r.getCell(2).setCellStyle(numStyle);
                        r.createCell(3).setCellValue(item.getPlantOperationalEfficiency() != null
                                        ? item.getPlantOperationalEfficiency()
                                        : 0.0);
                        r.getCell(3).setCellStyle(numStyle);
                        r.createCell(4).setCellValue(
                                        item.getEffectiveDailyTreatment() != null ? item.getEffectiveDailyTreatment()
                                                        : 0.0);
                        r.getCell(4).setCellStyle(numStyle);
                        r.createCell(5).setCellValue(
                                        item.getAnnualSludgeTreated() != null ? item.getAnnualSludgeTreated() : 0.0);
                        r.getCell(5).setCellStyle(numStyle);
                        r.createCell(6).setCellValue(item.getAnnualEmissionsReductionKilotonnes() != null
                                        ? item.getAnnualEmissionsReductionKilotonnes()
                                        : 0.0);
                        r.getCell(6).setCellStyle(numStyle);
                }
                autoSizeWithLimits(sheet, headers.length);
        }

        private void buildIswmSheet(XSSFSheet sheet, CellStyle headerStyle, CellStyle dataStyle,
                        CellStyle alternateDataStyle, CellStyle numberStyle, List<ISWMMitigation> data) {
                String[] headers = {
                                "Year",
                                "Waste Processed (t)",
                                "Degradable Organic Fraction (%)",
                                "Landfill Avoidance (kgCO2e/tonne)",
                                "Composting EF (kgCO2e/tonne)",
                                "BAU Emission (tCO2e)",
                                "DOF Diverted (tonnes)",
                                "Avoided Landfill (kgCO2e)",
                                "Composting Emissions (kgCO2e)",
                                "Net Annual Reduction (tCO2e)",
                                "Mitigation Scenario Emission (tCO2e)"
                };
                createHeader(sheet, headerStyle, headers);
                DataFormat dataFormat = sheet.getWorkbook().createDataFormat();
                int rowIdx = 1;
                for (int i = 0; i < data.size(); i++) {
                        ISWMMitigation item = data.get(i);
                        Row r = sheet.createRow(rowIdx++);
                        r.setHeightInPoints(18);
                        boolean isAlternate = i % 2 == 1;

                        // Year column
                        Cell yearCell = r.createCell(0);
                        yearCell.setCellValue(item.getYear());
                        CellStyle baseYearStyle = isAlternate ? alternateDataStyle : dataStyle;
                        CellStyle yearCellStyle = sheet.getWorkbook().createCellStyle();
                        yearCellStyle.cloneStyleFrom(baseYearStyle);
                        yearCellStyle.setAlignment(HorizontalAlignment.CENTER);
                        yearCellStyle.setDataFormat(dataFormat.getFormat("0"));
                        yearCell.setCellStyle(yearCellStyle);

                        // Number columns
                        CellStyle numStyle = isAlternate ? createAlternateNumberStyle(sheet.getWorkbook())
                                        : numberStyle;
                        r.createCell(1).setCellValue(item.getWasteProcessed() != null ? item.getWasteProcessed() : 0.0);
                        r.getCell(1).setCellStyle(numStyle);
                        r.createCell(2).setCellValue(item.getDegradableOrganicFraction() != null
                                        ? item.getDegradableOrganicFraction()
                                        : 0.0);
                        r.getCell(2).setCellStyle(numStyle);
                        r.createCell(3).setCellValue(
                                        item.getLandfillAvoidance() != null ? item.getLandfillAvoidance() : 0.0);
                        r.getCell(3).setCellStyle(numStyle);
                        r.createCell(4).setCellValue(item.getCompostingEF() != null ? item.getCompostingEF() : 0.0);
                        r.getCell(4).setCellStyle(numStyle);
                        r.createCell(5).setCellValue(item.getBauEmission() != null ? item.getBauEmission() : 0.0);
                        r.getCell(5).setCellStyle(numStyle);
                        r.createCell(6).setCellValue(item.getDofDiverted() != null ? item.getDofDiverted() : 0.0);
                        r.getCell(6).setCellStyle(numStyle);
                        r.createCell(7).setCellValue(
                                        item.getAvoidedLandfill() != null ? item.getAvoidedLandfill() : 0.0);
                        r.getCell(7).setCellStyle(numStyle);
                        r.createCell(8).setCellValue(
                                        item.getCompostingEmissions() != null ? item.getCompostingEmissions() : 0.0);
                        r.getCell(8).setCellStyle(numStyle);
                        r.createCell(9).setCellValue(
                                        item.getNetAnnualReduction() != null ? item.getNetAnnualReduction() : 0.0);
                        r.getCell(9).setCellStyle(numStyle);
                        r.createCell(10).setCellValue(item.getMitigationScenarioEmission() != null
                                        ? item.getMitigationScenarioEmission()
                                        : 0.0);
                        r.getCell(10).setCellStyle(numStyle);
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

        private void autoSize(Sheet sheet, int columns) {
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

        private void autoSizeWithLimits(Sheet sheet, int columns) {
                autoSize(sheet, columns);
        }
}
