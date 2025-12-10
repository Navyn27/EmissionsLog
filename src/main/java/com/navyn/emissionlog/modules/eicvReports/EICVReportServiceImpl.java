package com.navyn.emissionlog.modules.eicvReports;

import com.navyn.emissionlog.Enums.ExcelType;
import com.navyn.emissionlog.modules.eicvReports.dtos.EICVReportDto;
import com.navyn.emissionlog.utils.ExcelReader;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

            // Create header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            // Create data style
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            // Create header row
            Row headerRow = sheet.createRow(0);
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

            // Create example data row (optional - can be removed if you want only headers)
            Row exampleRow = sheet.createRow(1);
            Object[] exampleData = {
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

            for (int i = 0; i < exampleData.length; i++) {
                Cell cell = exampleRow.createCell(i);
                cell.setCellStyle(dataStyle);
                if (exampleData[i] instanceof String) {
                    cell.setCellValue((String) exampleData[i]);
                } else if (exampleData[i] instanceof Number) {
                    cell.setCellValue(((Number) exampleData[i]).doubleValue());
                }
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                // Set minimum column width
                if (sheet.getColumnWidth(i) < 3000) {
                    sheet.setColumnWidth(i, 3000);
                }
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error generating Excel template", e);
        }
    }
}
