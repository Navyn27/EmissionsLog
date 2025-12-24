package com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.service;

import com.navyn.emissionlog.Enums.ExcelType;
import com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.dto.CreateLightBulbDTO;
import com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.dto.LightBulbMitigationExcelDto;
import com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.dto.UpdateLightBulbDTO;
import com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.model.LightBulb;
import com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.repository.ILightBulbRepository;
import com.navyn.emissionlog.utils.ExcelReader;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
public class LightBulbServiceImpl implements ILightBulbService {
    private final ILightBulbRepository lightBulbRepository;

    @Override
    public LightBulb create(CreateLightBulbDTO lightBulbDTO) {
        LightBulb lightBulb = new LightBulb();
        lightBulb.setYear(lightBulbDTO.getYear());
        lightBulb.setTotalInstalledBulbsPerYear(lightBulbDTO.getTotalInstalledBulbsPerYear());
        lightBulb.setReductionCapacityPerBulb(lightBulbDTO.getReductionCapacityPerBulb());
        lightBulb.setEmissionFactor(lightBulbDTO.getEmissionFactor());
        lightBulb.setBau(lightBulbDTO.getBau());
        calculateAndSetFields(lightBulb);
        return lightBulbRepository.save(lightBulb);
    }

    @Override
    public List<LightBulb> getAll() {
        return lightBulbRepository.findAll();
    }

    @Override
    public LightBulb getById(UUID id) {
        return lightBulbRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("LightBulb not found"));
    }

    @Override
    public LightBulb update(UUID id, UpdateLightBulbDTO lightBulbDTO) {
        LightBulb lightBulb = getById(id);
        if (lightBulbDTO.getYear() != null) {
            lightBulb.setYear(lightBulbDTO.getYear());
        }
        if (lightBulbDTO.getTotalInstalledBulbsPerYear() != null) {
            lightBulb.setTotalInstalledBulbsPerYear(lightBulbDTO.getTotalInstalledBulbsPerYear());
        }
        if (lightBulbDTO.getReductionCapacityPerBulb() != null) {
            lightBulb.setReductionCapacityPerBulb(lightBulbDTO.getReductionCapacityPerBulb());
        }
        if (lightBulbDTO.getEmissionFactor() != null) {
            lightBulb.setEmissionFactor(lightBulbDTO.getEmissionFactor());
        }
        if (lightBulbDTO.getBau() != null) {
            lightBulb.setBau(lightBulbDTO.getBau());
        }
        calculateAndSetFields(lightBulb);
        return lightBulbRepository.save(lightBulb);
    }

    @Override
    public void delete(UUID id) {
        lightBulbRepository.deleteById(id);
    }

    @Override
    public List<LightBulb> getByYear(int year) {
        return lightBulbRepository.findAllByYear(year);
    }

    private void calculateAndSetFields(LightBulb lightBulb) {
        double totalReductionPerYear = lightBulb.getReductionCapacityPerBulb()
                * lightBulb.getTotalInstalledBulbsPerYear();
        double netGhGMitigationAchieved = (totalReductionPerYear * lightBulb.getEmissionFactor()) / 1000;
        double scenarioGhGMitigationAchieved = lightBulb.getBau() - netGhGMitigationAchieved;

        lightBulb.setTotalReductionPerYear(totalReductionPerYear);
        lightBulb.setNetGhGMitigationAchieved(netGhGMitigationAchieved);
        lightBulb.setScenarioGhGMitigationAchieved(scenarioGhGMitigationAchieved);
    }

    @Override
    public byte[] generateExcelTemplate() {
        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Light Bulb Mitigation");

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
            numberStyle.setAlignment(HorizontalAlignment.RIGHT);
            numberStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // Create year style (centered number)
            XSSFCellStyle yearStyle = (XSSFCellStyle) workbook.createCellStyle();
            yearStyle.cloneStyleFrom(dataStyle);
            yearStyle.setAlignment(HorizontalAlignment.CENTER);

            int rowIdx = 0;

            // Title row
            Row titleRow = sheet.createRow(rowIdx++);
            titleRow.setHeightInPoints(30);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Light Bulb Mitigation Template");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));

            rowIdx++; // Blank row

            // Create header row
            Row headerRow = sheet.createRow(rowIdx++);
            headerRow.setHeightInPoints(22);
            String[] headers = {
                    "Year",
                    "Total Installed Bulbs Per Year",
                    "Reduction Capacity Per Bulb",
                    "Emission Factor",
                    "BAU"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Create example data rows
            Object[] exampleData1 = {
                    2024,
                    1000.0,
                    0.1,
                    0.5,
                    500.0
            };

            Object[] exampleData2 = {
                    2025,
                    1200.0,
                    0.12,
                    0.55,
                    600.0
            };

            // First example row
            Row exampleRow1 = sheet.createRow(rowIdx++);
            exampleRow1.setHeightInPoints(18);
            for (int i = 0; i < exampleData1.length; i++) {
                Cell cell = exampleRow1.createCell(i);
                if (i == 0) { // Year
                    cell.setCellStyle(yearStyle);
                    cell.setCellValue(((Number) exampleData1[i]).intValue());
                } else { // All other fields are numbers
                    cell.setCellStyle(numberStyle);
                    cell.setCellValue(((Number) exampleData1[i]).doubleValue());
                }
            }

            // Second example row with alternate style
            Row exampleRow2 = sheet.createRow(rowIdx++);
            exampleRow2.setHeightInPoints(18);
            for (int i = 0; i < exampleData2.length; i++) {
                Cell cell = exampleRow2.createCell(i);
                if (i == 0) { // Year
                    CellStyle altYearStyle = workbook.createCellStyle();
                    altYearStyle.cloneStyleFrom(alternateDataStyle);
                    altYearStyle.setAlignment(HorizontalAlignment.CENTER);
                    cell.setCellStyle(altYearStyle);
                    cell.setCellValue(((Number) exampleData2[i]).intValue());
                } else { // All other fields are numbers
                    CellStyle altNumStyle = workbook.createCellStyle();
                    altNumStyle.cloneStyleFrom(numberStyle);
                    altNumStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                    altNumStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    cell.setCellStyle(altNumStyle);
                    cell.setCellValue(((Number) exampleData2[i]).doubleValue());
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

    @Override
    public Map<String, Object> createLightBulbMitigationFromExcel(MultipartFile file) {
        List<LightBulb> savedRecords = new ArrayList<>();
        List<Integer> skippedYears = new ArrayList<>();
        int totalProcessed = 0;

        try {
            List<LightBulbMitigationExcelDto> dtos = ExcelReader.readExcel(
                    file.getInputStream(),
                    LightBulbMitigationExcelDto.class,
                    ExcelType.LIGHT_BULB_MITIGATION);

            for (int i = 0; i < dtos.size(); i++) {
                LightBulbMitigationExcelDto dto = dtos.get(i);
                // Calculate actual Excel row number: headerRowIndex(2) + 1 + i + 1 (1-based) =
                // i + 4
                int actualRowNumber = i + 4;

                // Check if row is effectively empty (missing critical fields) - skip it
                // A row is effectively empty if Year is missing
                boolean isEffectivelyEmpty = (dto.getYear() == 0);

                if (isEffectivelyEmpty) {
                    // Skip this row - it's effectively empty (likely formatting or blank row)
                    continue;
                }

                totalProcessed++;

                // Validate required fields
                List<String> missingFields = new ArrayList<>();
                if (dto.getYear() == 0) {
                    missingFields.add("Year");
                }
                if (dto.getTotalInstalledBulbsPerYear() <= 0) {
                    missingFields.add("Total Installed Bulbs Per Year");
                }
                if (dto.getReductionCapacityPerBulb() <= 0) {
                    missingFields.add("Reduction Capacity Per Bulb");
                }
                if (dto.getEmissionFactor() <= 0) {
                    missingFields.add("Emission Factor");
                }
                if (dto.getBau() < 0) {
                    missingFields.add("BAU");
                }

                if (!missingFields.isEmpty()) {
                    throw new RuntimeException(String.format(
                            "Missing required fields at row %d: %s. Please fill in all required fields in your Excel file.",
                            actualRowNumber,
                            String.join(", ", missingFields)));
                }

                // Check if year already exists
                if (lightBulbRepository.findByYear(dto.getYear()).isPresent()) {
                    skippedYears.add(dto.getYear());
                    continue; // Skip this row
                }

                // Create DTO from Excel DTO
                CreateLightBulbDTO createDto = new CreateLightBulbDTO();
                createDto.setYear(dto.getYear());
                createDto.setTotalInstalledBulbsPerYear(dto.getTotalInstalledBulbsPerYear());
                createDto.setReductionCapacityPerBulb(dto.getReductionCapacityPerBulb());
                createDto.setEmissionFactor(dto.getEmissionFactor());
                createDto.setBau(dto.getBau());

                // Create the record using existing create method
                LightBulb saved = create(createDto);
                savedRecords.add(saved);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("saved", savedRecords);
            result.put("savedCount", savedRecords.size());
            result.put("skippedCount", skippedYears.size());
            result.put("skippedYears", skippedYears);
            result.put("totalProcessed", totalProcessed);

            return result;
        } catch (IOException e) {
            // Re-throw IOException with user-friendly message
            String message = e.getMessage();
            if (message != null) {
                throw new RuntimeException(message, e);
            } else {
                throw new RuntimeException(
                        "Incorrect template. Please download the correct template and try again.",
                        e);
            }
        } catch (NullPointerException e) {
            // Handle null pointer exceptions with clear message
            throw new RuntimeException(
                    "Missing required fields. Please fill in all required fields in your Excel file.", e);
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if (errorMsg != null) {
                throw new RuntimeException(errorMsg, e);
            }
            throw new RuntimeException("Error processing Excel file. Please check your file and try again.", e);
        }
    }
}
