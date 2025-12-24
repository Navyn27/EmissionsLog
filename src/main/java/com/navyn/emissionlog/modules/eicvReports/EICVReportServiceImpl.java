package com.navyn.emissionlog.modules.eicvReports;

import com.navyn.emissionlog.Enums.ExcelType;
import com.navyn.emissionlog.modules.eicvReports.dtos.EICVReportDto;
import com.navyn.emissionlog.utils.ExcelReader;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EICVReportServiceImpl implements EICVReportService {

    private final EICVReportRepository eicvReportRepository;

    @Override
    public EICVReport createEICVReport(EICVReportDto eicvReportDto) {
        EICVReport eicvReport = new EICVReport();
        eicvReport.setName(eicvReportDto.getName());
        eicvReport.setYear(eicvReportDto.getYear().intValue());
        eicvReport.setFlushToilet(eicvReportDto.getFlushToilet());
        eicvReport.setProtectedLatrines(eicvReportDto.getProtectedLatrines());
        eicvReport.setImprovedTypeNotSharedWithOtherHH(eicvReportDto.getImprovedTypeNotSharedWithOtherHH());
        eicvReport.setUnprotectedLatrines(eicvReportDto.getUnprotectedLatrines());
        eicvReport.setTotalHouseholds(eicvReportDto.getTotalHouseholds());
        eicvReport.setNoToiletFacilities(eicvReportDto.getNoToiletFacilities());
        eicvReport.setTotalImprovedSanitation(eicvReportDto.getTotalImprovedSanitation());
        eicvReport.setOthers(eicvReportDto.getOthers());
        return eicvReportRepository.save(eicvReport);
    }

    @Override
    public EICVReport getEICVReportByYear(int year) {
        Optional<EICVReport> eicvReport = eicvReportRepository.findByYear(year);
        return eicvReport.orElse(null);
    }

    @Override
    public EICVReport getEICVReportById(UUID id) {
        return eicvReportRepository.findById(id).orElseThrow(() -> new RuntimeException("EICV Report not found"));
    }

    @Override
    public List<EICVReport> findAll(String name, Integer year) {
        if (name != null && !name.trim().isEmpty() && year == null) {
            return eicvReportRepository.findByNameContainingIgnoreCaseOrderByYearDesc(name);
        }
        if ((name == null || name.trim().isEmpty()) && year != null) {
            EICVReport report = getEICVReportByYear(year);
            return report != null ? List.of(report) : new ArrayList<>();
        }
        if (name != null && !name.trim().isEmpty()) {
            return eicvReportRepository.findByNameContainingIgnoreCaseAndYearOrderByYearDesc(name, year);
        }
        return eicvReportRepository.findAllByOrderByYearDesc();
    }

    @Override
    public List<EICVReport> createReportsFromExcel(MultipartFile file) {
        List<EICVReport> savedEicvReports = new ArrayList<>();
        try {
            List<EICVReportDto> eicvReports = ExcelReader.readExcel(file.getInputStream(), EICVReportDto.class,
                    ExcelType.EICV_REPORT);
            for (EICVReportDto eicvReport : eicvReports) {
                savedEicvReports.add(createEICVReport(eicvReport));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error reading EICV Reports from Excel file", e);
        }
        return savedEicvReports;
    }

    @Override
    public EICVReport updateEICVReport(UUID eicvReportId, EICVReportDto eicvReportDto) {
        EICVReport eicvReport = eicvReportRepository.findById(eicvReportId)
                .orElseThrow(() -> new RuntimeException("EICV Report not found"));
        eicvReport.setName(eicvReportDto.getName());
        eicvReport.setYear(eicvReportDto.getYear().intValue());
        eicvReport.setFlushToilet(eicvReportDto.getFlushToilet());
        eicvReport.setProtectedLatrines(eicvReportDto.getProtectedLatrines());
        eicvReport.setImprovedTypeNotSharedWithOtherHH(eicvReportDto.getImprovedTypeNotSharedWithOtherHH());
        eicvReport.setUnprotectedLatrines(eicvReportDto.getUnprotectedLatrines());
        eicvReport.setTotalHouseholds(eicvReportDto.getTotalHouseholds());
        eicvReport.setNoToiletFacilities(eicvReportDto.getNoToiletFacilities());
        eicvReport.setTotalImprovedSanitation(eicvReportDto.getTotalImprovedSanitation());
        eicvReport.setOthers(eicvReportDto.getOthers());
        return eicvReportRepository.save(eicvReport);
    }

    @Override
    public void deleteEICVReport(UUID eicvReportId) {
        if (!eicvReportRepository.existsById(eicvReportId)) {
            throw new RuntimeException("EICV Report not found with id: " + eicvReportId);
        }
        eicvReportRepository.deleteById(eicvReportId);
    }

    @Override
    public EICVReport getEICVReportsByName(String name) {
        return eicvReportRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("EICV Report with name " + name + " not found"));
    }

    @Override
    public byte[] generateExcelTemplate() {
        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("EICV Reports");

            // Create title style
            XSSFCellStyle titleStyle = (XSSFCellStyle) workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 18);
            titleFont.setColor(IndexedColors.WHITE.getIndex());
            titleFont.setFontName("Calibri");
            titleStyle.setFont(titleFont);
            titleStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            titleStyle.setBorderTop(BorderStyle.MEDIUM);
            titleStyle.setBorderBottom(BorderStyle.MEDIUM);
            titleStyle.setBorderLeft(BorderStyle.MEDIUM);
            titleStyle.setBorderRight(BorderStyle.MEDIUM);
            // Border colors will use defaults for cleaner appearance

            // Create header style
            XSSFCellStyle headerStyle = (XSSFCellStyle) workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 11);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setFontName("Calibri");
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            // Border colors will use defaults for cleaner appearance
            headerStyle.setWrapText(true);

            // Create data style
            XSSFCellStyle dataStyle = (XSSFCellStyle) workbook.createCellStyle();
            Font dataFont = workbook.createFont();
            dataFont.setFontName("Calibri");
            dataFont.setFontHeightInPoints((short) 10);
            dataStyle.setFont(dataFont);
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            // Border colors will use defaults for cleaner appearance
            dataStyle.setAlignment(HorizontalAlignment.LEFT);
            dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            dataStyle.setWrapText(true);

            // Create alternate data style
            XSSFCellStyle alternateDataStyle = (XSSFCellStyle) workbook.createCellStyle();
            Font altDataFont = workbook.createFont();
            altDataFont.setFontName("Calibri");
            altDataFont.setFontHeightInPoints((short) 10);
            alternateDataStyle.setFont(altDataFont);
            alternateDataStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            alternateDataStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            alternateDataStyle.setBorderBottom(BorderStyle.THIN);
            alternateDataStyle.setBorderTop(BorderStyle.THIN);
            alternateDataStyle.setBorderLeft(BorderStyle.THIN);
            alternateDataStyle.setBorderRight(BorderStyle.THIN);
            // Border colors will use defaults for cleaner appearance
            alternateDataStyle.setAlignment(HorizontalAlignment.LEFT);
            alternateDataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            alternateDataStyle.setWrapText(true);

            // Create number style
            XSSFCellStyle numberStyle = (XSSFCellStyle) workbook.createCellStyle();
            Font numFont = workbook.createFont();
            numFont.setFontName("Calibri");
            numFont.setFontHeightInPoints((short) 10);
            numberStyle.setFont(numFont);
            DataFormat dataFormat = workbook.createDataFormat();
            numberStyle.setDataFormat(dataFormat.getFormat("#,##0.00"));
            numberStyle.setBorderBottom(BorderStyle.THIN);
            numberStyle.setBorderTop(BorderStyle.THIN);
            numberStyle.setBorderLeft(BorderStyle.THIN);
            numberStyle.setBorderRight(BorderStyle.THIN);
            // Border colors will use defaults for cleaner appearance
            numberStyle.setAlignment(HorizontalAlignment.RIGHT);
            numberStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            int rowIdx = 0;

            // Title row
            Row titleRow = sheet.createRow(rowIdx++);
            titleRow.setHeightInPoints(30);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("EICV Report Template");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 9));

            rowIdx++; // Blank row

            // Create header row
            Row headerRow = sheet.createRow(rowIdx++);
            headerRow.setHeightInPoints(22);
            String[] headers = {
                    "Name",
                    "Year",
                    "Total Improved Sanitation",
                    "Improved Type Not Shared With Other HH",
                    "Flush Toilet",
                    "Protected Latrines",
                    "Unprotected Latrines",
                    "Others",
                    "No Toilet Facilities",
                    "Total Households"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Create example data rows with alternating styles
            Object[] exampleData1 = {
                    "EICV Report 2022",
                    2022,
                    75.5,
                    25.3,
                    15.2,
                    30.1,
                    20.0,
                    5.0,
                    24.5,
                    1200000
            };

            Object[] exampleData2 = {
                    "EICV Report 2023",
                    2023,
                    78.2,
                    27.1,
                    16.5,
                    32.0,
                    18.5,
                    4.8,
                    21.3,
                    1250000
            };

            // First example row
            Row exampleRow1 = sheet.createRow(rowIdx++);
            exampleRow1.setHeightInPoints(18);
            for (int i = 0; i < exampleData1.length; i++) {
                Cell cell = exampleRow1.createCell(i);
                if (i == 0 || i == 1) { // Name and Year
                    cell.setCellStyle(dataStyle);
                    if (exampleData1[i] instanceof String) {
                        cell.setCellValue((String) exampleData1[i]);
                    } else if (exampleData1[i] instanceof Number) {
                        if (i == 1) { // Year
                            CellStyle yearStyle = workbook.createCellStyle();
                            yearStyle.cloneStyleFrom(dataStyle);
                            yearStyle.setAlignment(HorizontalAlignment.CENTER);
                            cell.setCellStyle(yearStyle);
                        }
                        cell.setCellValue(((Number) exampleData1[i]).doubleValue());
                    }
                } else { // Number columns
                    cell.setCellStyle(numberStyle);
                    if (exampleData1[i] instanceof Number) {
                        cell.setCellValue(((Number) exampleData1[i]).doubleValue());
                    }
                }
            }

            // Second example row with alternate style
            Row exampleRow2 = sheet.createRow(rowIdx++);
            exampleRow2.setHeightInPoints(18);
            for (int i = 0; i < exampleData2.length; i++) {
                Cell cell = exampleRow2.createCell(i);
                if (i == 0 || i == 1) { // Name and Year
                    cell.setCellStyle(alternateDataStyle);
                    if (exampleData2[i] instanceof String) {
                        cell.setCellValue((String) exampleData2[i]);
                    } else if (exampleData2[i] instanceof Number) {
                        if (i == 1) { // Year
                            CellStyle yearStyle = workbook.createCellStyle();
                            yearStyle.cloneStyleFrom(alternateDataStyle);
                            yearStyle.setAlignment(HorizontalAlignment.CENTER);
                            cell.setCellStyle(yearStyle);
                        }
                        cell.setCellValue(((Number) exampleData2[i]).doubleValue());
                    }
                } else { // Number columns
                    CellStyle altNumStyle = workbook.createCellStyle();
                    altNumStyle.cloneStyleFrom(numberStyle);
                    altNumStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                    altNumStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    cell.setCellStyle(altNumStyle);
                    if (exampleData2[i] instanceof Number) {
                        cell.setCellValue(((Number) exampleData2[i]).doubleValue());
                    }
                }
            }

            // Auto-size columns with limits
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                int currentWidth = sheet.getColumnWidth(i);
                int minWidth = 3000;
                int maxWidth = 20000;
                if (currentWidth < minWidth) {
                    sheet.setColumnWidth(i, minWidth);
                } else if (currentWidth > maxWidth) {
                    sheet.setColumnWidth(i, maxWidth);
                }
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error generating Excel template", e);
        }
    }
}
