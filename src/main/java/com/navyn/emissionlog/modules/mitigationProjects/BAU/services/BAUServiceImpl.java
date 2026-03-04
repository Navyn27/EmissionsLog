package com.navyn.emissionlog.modules.mitigationProjects.BAU.services;

import com.navyn.emissionlog.Enums.ExcelType;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.enums.ESector;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.models.BAU;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.dtos.BAUDto;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.repositories.BAURepository;
import com.navyn.emissionlog.utils.ExcelReader;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BAUServiceImpl implements BAUService {

    private final BAURepository bauRepository;

    @Override
    @Transactional
    public BAU createBAU(BAUDto dto) {
        // Check if BAU with same year and sector already exists
        Optional<BAU> existingBAU = bauRepository.findByYearAndSector(dto.getYear(), dto.getSector());
        if (existingBAU.isPresent()) {
            throw new RuntimeException(
                String.format("BAU record for year %d and sector %s already exists. Please use a different year or sector, or update the existing record.", 
                    dto.getYear(), dto.getSector())
            );
        }

        BAU bau = new BAU();
        bau.setValue(dto.getValue());
        bau.setSector(dto.getSector());
        bau.setYear(dto.getYear());
        
        return bauRepository.save(bau);
    }

    @Override
    @Transactional
    public BAU updateBAU(UUID id, BAUDto dto) {
        return bauRepository.findById(id)
                .map(existingBAU -> {
                    // Check if another BAU with the same year+sector exists (excluding current one)
                    Optional<BAU> bauWithSameYearAndSector = bauRepository.findByYearAndSector(dto.getYear(), dto.getSector());
                    if (bauWithSameYearAndSector.isPresent() && !bauWithSameYearAndSector.get().getId().equals(id)) {
                        throw new RuntimeException(
                            String.format("BAU record for year %d and sector %s already exists. Please use a different year or sector.", 
                                dto.getYear(), dto.getSector())
                        );
                    }
                    
                    existingBAU.setValue(dto.getValue());
                    existingBAU.setSector(dto.getSector());
                    existingBAU.setYear(dto.getYear());
                    
                    return bauRepository.save(existingBAU);
                })
                .orElseThrow(() -> new RuntimeException("BAU not found with id: " + id));
    }

    @Override
    public Optional<BAU> getBAUById(UUID id) {
        return bauRepository.findById(id);
    }

    @Override
    public List<BAU> getAllBAUs() {
        return bauRepository.findAll(Sort.by(Sort.Direction.ASC, "year").and(Sort.by(Sort.Direction.ASC, "sector")));
    }

    @Override
    public List<BAU> getBAUsByYear(Integer year) {
        return bauRepository.findByYearOrderBySectorAsc(year);
    }

    @Override
    public List<BAU> getBAUsBySector(ESector sector) {
        return bauRepository.findBySectorOrderByYearAsc(sector);
    }

    @Override
    public Optional<BAU> getBAUByYearAndSector(Integer year, ESector sector) {
        return bauRepository.findByYearAndSector(year, sector);
    }

    @Override
    @Transactional
    public void deleteBAU(UUID id) {
        if (!bauRepository.existsById(id)) {
            throw new RuntimeException("BAU not found with id: " + id);
        }
        bauRepository.deleteById(id);
    }

    @Override
    public byte[] generateExcelTemplate() {
        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("BAU");

            // Create styles
            XSSFCellStyle titleStyle = (XSSFCellStyle) workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setFontName("Calibri");
            titleFont.setFontHeightInPoints((short) 16);
            titleFont.setBold(true);
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            titleStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            XSSFCellStyle headerStyle = createHeaderStyle(workbook);
            XSSFCellStyle dataStyle = createDataStyle(workbook);
            XSSFCellStyle alternateDataStyle = createAlternateDataStyle(workbook);
            XSSFCellStyle numberStyle = createNumberStyle(workbook);
            XSSFCellStyle yearStyle = createYearStyle(workbook, dataStyle);

            int rowIdx = 0;

            // Title row
            Row titleRow = sheet.createRow(rowIdx++);
            titleRow.setHeightInPoints(30);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("BAU Template");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));

            rowIdx++; // Blank row

            // Create header row
            Row headerRow = sheet.createRow(rowIdx++);
            headerRow.setHeightInPoints(22);
            String[] headers = {
                    "Year",
                    "Sector",
                    "Value (ktCO₂e)"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Get all sector enum values for dropdown
            String[] sectorNames = Arrays.stream(ESector.values())
                    .map(Enum::name)
                    .toArray(String[]::new);

            // Create data validation helper
            DataValidationHelper validationHelper = sheet.getDataValidationHelper();

            // Data validation for Sector column (Column B, index 1)
            if (sectorNames.length > 0) {
                CellRangeAddressList sectorList = new CellRangeAddressList(3, 1000, 1, 1);
                DataValidationConstraint sectorConstraint = validationHelper
                        .createExplicitListConstraint(sectorNames);
                DataValidation sectorValidation = validationHelper.createValidation(sectorConstraint,
                        sectorList);
                sectorValidation.setShowErrorBox(true);
                sectorValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
                sectorValidation.createErrorBox("Invalid Sector",
                        "Please select a valid sector from the dropdown list: ENERGY, IPPU, WASTE, or AFOLU.");
                sectorValidation.setShowPromptBox(true);
                sectorValidation.createPromptBox("Sector",
                        "Select a sector from the dropdown list: ENERGY, IPPU, WASTE, or AFOLU.");
                sheet.addValidationData(sectorValidation);
            }

            // Create example data rows
            Object[] exampleData1 = {
                    2024,
                    "ENERGY",
                    150.5
            };
            Object[] exampleData2 = {
                    2025,
                    "WASTE",
                    200.75
            };

            // First example row
            Row exampleRow1 = sheet.createRow(rowIdx++);
            exampleRow1.setHeightInPoints(18);
            for (int i = 0; i < exampleData1.length; i++) {
                Cell cell = exampleRow1.createCell(i);
                if (i == 0) {
                    cell.setCellStyle(yearStyle);
                    cell.setCellValue(((Number) exampleData1[i]).intValue());
                } else if (i == 1) {
                    cell.setCellStyle(dataStyle);
                    cell.setCellValue((String) exampleData1[i]);
                } else {
                    cell.setCellStyle(numberStyle);
                    cell.setCellValue(((Number) exampleData1[i]).doubleValue());
                }
            }

            // Second example row with alternate style
            Row exampleRow2 = sheet.createRow(rowIdx++);
            exampleRow2.setHeightInPoints(18);
            for (int i = 0; i < exampleData2.length; i++) {
                Cell cell = exampleRow2.createCell(i);
                if (i == 0) {
                    CellStyle altYearStyle = workbook.createCellStyle();
                    altYearStyle.cloneStyleFrom(alternateDataStyle);
                    altYearStyle.setAlignment(HorizontalAlignment.CENTER);
                    cell.setCellStyle(altYearStyle);
                    cell.setCellValue(((Number) exampleData2[i]).intValue());
                } else if (i == 1) {
                    cell.setCellStyle(alternateDataStyle);
                    cell.setCellValue((String) exampleData2[i]);
                } else {
                    CellStyle altNumStyle = workbook.createCellStyle();
                    altNumStyle.cloneStyleFrom(numberStyle);
                    altNumStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                    altNumStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    cell.setCellStyle(altNumStyle);
                    cell.setCellValue(((Number) exampleData2[i]).doubleValue());
                }
            }

            // Auto-size columns
            autoSizeColumns(sheet, headers.length);

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error generating Excel template", e);
        }
    }

    @Override
    @Transactional
    public Map<String, Object> createBAUFromExcel(MultipartFile file) {
        List<BAU> savedRecords = new ArrayList<>();
        List<Map<String, Object>> skippedRows = new ArrayList<>();
        Set<String> processedYearSector = new HashSet<>(); // Track duplicates in file
        int totalProcessed = 0;

        try {
            List<BAUDto> dtos = ExcelReader.readExcel(
                    file.getInputStream(),
                    BAUDto.class,
                    ExcelType.BAU);

            for (int i = 0; i < dtos.size(); i++) {
                BAUDto dto = dtos.get(i);
                totalProcessed++;
                int rowNumber = i + 1; // Excel row number (1-based, accounting for header row)
                int excelRowNumber = rowNumber + 2; // +2 for title row and blank row

                // Validate required fields
                List<String> missingFields = new ArrayList<>();
                if (dto.getYear() == null) {
                    missingFields.add("Year");
                }
                if (dto.getSector() == null) {
                    missingFields.add("Sector");
                }
                if (dto.getValue() == null) {
                    missingFields.add("Value");
                }

                if (!missingFields.isEmpty()) {
                    Map<String, Object> skipInfo = new HashMap<>();
                    skipInfo.put("row", excelRowNumber);
                    skipInfo.put("year", dto.getYear() != null ? dto.getYear() : "N/A");
                    skipInfo.put("sector", dto.getSector() != null ? dto.getSector().name() : "N/A");
                    skipInfo.put("reason", "Missing required fields: " + String.join(", ", missingFields));
                    skippedRows.add(skipInfo);
                    continue; // Skip this row
                }

                // Validate sector enum
                try {
                    ESector.valueOf(dto.getSector().name());
                } catch (IllegalArgumentException e) {
                    Map<String, Object> skipInfo = new HashMap<>();
                    skipInfo.put("row", excelRowNumber);
                    skipInfo.put("year", dto.getYear());
                    skipInfo.put("sector", dto.getSector() != null ? dto.getSector().name() : "N/A");
                    skipInfo.put("reason", "Invalid sector. Must be one of: ENERGY, IPPU, WASTE, AFOLU");
                    skippedRows.add(skipInfo);
                    continue; // Skip this row
                }

                // Validate year (must be >= 1900)
                if (dto.getYear() < 1900) {
                    Map<String, Object> skipInfo = new HashMap<>();
                    skipInfo.put("row", excelRowNumber);
                    skipInfo.put("year", dto.getYear());
                    skipInfo.put("sector", dto.getSector().name());
                    skipInfo.put("reason", "Year must be 1900 or later");
                    skippedRows.add(skipInfo);
                    continue; // Skip this row
                }

                // Validate value (must be >= 0)
                if (dto.getValue() < 0) {
                    Map<String, Object> skipInfo = new HashMap<>();
                    skipInfo.put("row", excelRowNumber);
                    skipInfo.put("year", dto.getYear());
                    skipInfo.put("sector", dto.getSector().name());
                    skipInfo.put("reason", "Value must be a positive number or zero");
                    skippedRows.add(skipInfo);
                    continue; // Skip this row
                }

                // Check for duplicate in same file
                String yearSectorKey = dto.getYear() + "_" + dto.getSector().name();
                if (processedYearSector.contains(yearSectorKey)) {
                    Map<String, Object> skipInfo = new HashMap<>();
                    skipInfo.put("row", excelRowNumber);
                    skipInfo.put("year", dto.getYear());
                    skipInfo.put("sector", dto.getSector().name());
                    skipInfo.put("reason", "Duplicate year and sector combination in the same file");
                    skippedRows.add(skipInfo);
                    continue; // Skip this row
                }
                processedYearSector.add(yearSectorKey);

                // Try to create the record - catch duplicate errors and skip instead of failing
                try {
                    BAU saved = createBAU(dto);
                    savedRecords.add(saved);
                } catch (RuntimeException e) {
                    String errorMessage = e.getMessage();
                    if (errorMessage != null && errorMessage.contains("already exists")) {
                        Map<String, Object> skipInfo = new HashMap<>();
                        skipInfo.put("row", excelRowNumber);
                        skipInfo.put("year", dto.getYear());
                        skipInfo.put("sector", dto.getSector().name());
                        skipInfo.put("reason", errorMessage);
                        skippedRows.add(skipInfo);
                        continue; // Skip this row
                    }
                    // If it's a different error, re-throw it (e.g., file format issues)
                    throw e;
                }
            }

            // Calculate total skipped count
            int totalSkipped = skippedRows.size();

            Map<String, Object> result = new HashMap<>();
            result.put("saved", savedRecords);
            result.put("savedCount", savedRecords.size());
            result.put("skippedCount", totalSkipped);
            result.put("skippedRows", skippedRows);
            result.put("totalProcessed", totalProcessed);

            return result;
        } catch (IOException e) {
            // Re-throw IOException with user-friendly message
            String message = e.getMessage();
            if (message != null) {
                throw new RuntimeException("Error reading Excel file: " + message, e);
            }
            throw new RuntimeException("Error reading Excel file. Please ensure the file is a valid Excel format.", e);
        }
    }

    // Helper methods for styles
    private XSSFCellStyle createHeaderStyle(Workbook workbook) {
        XSSFCellStyle headerStyle = (XSSFCellStyle) workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setFontName("Calibri");
        headerFont.setFontHeightInPoints((short) 11);
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setWrapText(true);
        return headerStyle;
    }

    private XSSFCellStyle createDataStyle(Workbook workbook) {
        XSSFCellStyle dataStyle = (XSSFCellStyle) workbook.createCellStyle();
        Font dataFont = workbook.createFont();
        dataFont.setFontName("Calibri");
        dataFont.setFontHeightInPoints((short) 10);
        dataStyle.setFont(dataFont);
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);
        dataStyle.setAlignment(HorizontalAlignment.LEFT);
        dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        dataStyle.setWrapText(true);
        return dataStyle;
    }

    private XSSFCellStyle createAlternateDataStyle(Workbook workbook) {
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
        alternateDataStyle.setAlignment(HorizontalAlignment.LEFT);
        alternateDataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        alternateDataStyle.setWrapText(true);
        return alternateDataStyle;
    }

    private XSSFCellStyle createNumberStyle(Workbook workbook) {
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
        numberStyle.setAlignment(HorizontalAlignment.RIGHT);
        numberStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        return numberStyle;
    }

    private XSSFCellStyle createYearStyle(Workbook workbook, XSSFCellStyle dataStyle) {
        XSSFCellStyle yearStyle = (XSSFCellStyle) workbook.createCellStyle();
        yearStyle.cloneStyleFrom(dataStyle);
        yearStyle.setAlignment(HorizontalAlignment.CENTER);
        return yearStyle;
    }

    private void autoSizeColumns(Sheet sheet, int numColumns) {
        for (int i = 0; i < numColumns; i++) {
            sheet.autoSizeColumn(i);
            int currentWidth = sheet.getColumnWidth(i);
            int minWidth = 5000;
            int maxWidth = 30000;
            if (currentWidth < minWidth) {
                sheet.setColumnWidth(i, minWidth);
            } else if (currentWidth > maxWidth) {
                sheet.setColumnWidth(i, maxWidth);
            } else {
                sheet.setColumnWidth(i, (int) (currentWidth * 1.3));
            }
        }
    }
}

