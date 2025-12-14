package com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.service;

import com.navyn.emissionlog.Enums.ExcelType;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.models.CookstoveMitigationExcelDto;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.models.StoveInstallationDTO;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.models.StoveMitigationYear;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.models.StoveType;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.repository.StoveMitigationYearRepository;
import com.navyn.emissionlog.utils.ExcelReader;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class StoveMitigationService {

    private final StoveMitigationYearRepository mitigationRepository;
    private final StoveTypeService stoveTypeService;

    public StoveMitigationService(StoveMitigationYearRepository mitigationRepository,
            StoveTypeService stoveTypeService) {
        this.mitigationRepository = mitigationRepository;
        this.stoveTypeService = stoveTypeService;
    }

    /**
     * Create a mitigation record based on user input and apply the required
     * formulas.
     *
     * constant = (baselinePercentage * 0.15) / 0.25
     * avoidedEmission = (constant * differenceInstalled) * 0.029 * ((4*112*0.647) +
     * 8.43) * 10^-3
     * totalAvoidedEmission = sum of avoidedEmission for all types in that year
     * adjustment = bau - totalAvoidedEmission
     */
    @Transactional
    public StoveMitigationYear createMitigation(StoveInstallationDTO dto) {
        UUID stoveTypeId = dto.getStoveTypeId();
        StoveType stoveType = stoveTypeService.findById(stoveTypeId)
                .orElseThrow(() -> new IllegalArgumentException("StoveType not found: " + stoveTypeId));

        int year = dto.getYear();

        // Determine differenceInstalled compared to previous year for this stove type
        int previousUnits = mitigationRepository
                .findTopByStoveTypeIdAndYearLessThanOrderByYearDesc(stoveTypeId, year)
                .map(StoveMitigationYear::getUnitsInstalled)
                .orElse(0);

        int unitsInstalledThisYear = dto.getUnitsInstalledThisYear();
        int differenceInstalled = unitsInstalledThisYear - previousUnits;

        // constant = (baselinePercentage * 0.15) / 0.25
        double constant = (stoveType.getBaselinePercentage() * 0.15) / 0.25;

        // avoidedEmission = (constant * differenceInstalled) * 0.029 * ((4*112*0.647) +
        // 8.43) * 10^-3
        double conversionFactor = 0.029;
        double stoveFactor = 4 * 112 * 0.647;
        double assumptionsC11 = 8.43;
        double scale = 1e-3;

        double avoidedEmissions = (constant * differenceInstalled)
                * conversionFactor
                * (stoveFactor + assumptionsC11)
                * scale;

        StoveMitigationYear entity = new StoveMitigationYear();
        entity.setStoveType(stoveType);
        entity.setYear(year);
        entity.setUnitsInstalled(unitsInstalledThisYear);
        entity.setBau(dto.getBau());
        entity.setDifferenceInstalled(differenceInstalled);
        entity.setConstantValue(constant);
        entity.setAvoidedEmissions(avoidedEmissions);

        // Persist first to include this record in the yearly sum
        StoveMitigationYear saved = mitigationRepository.save(entity);

        // totalAvoidedEmission = sum of avoidedEmission of each type for this year
        double totalAvoided = mitigationRepository.sumAvoidedEmissionsByYear(year);
        double adjustment = dto.getBau() - totalAvoided;

        saved.setTotalAvoidedEmissions(totalAvoided);
        saved.setAdjustment(adjustment);

        return mitigationRepository.save(saved);
    }

    public List<StoveMitigationYear> findAll() {
        return mitigationRepository.findAll();
    }

    public List<StoveMitigationYear> findByStoveType(UUID stoveTypeId) {
        return mitigationRepository.findByStoveTypeId(stoveTypeId);
    }

    public List<StoveMitigationYear> findByYear(int year) {
        return mitigationRepository.findByYear(year);
    }

    public StoveMitigationYear findById(UUID id) {
        return mitigationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mitigation record not found: " + id));
    }

    @Transactional
    public void deleteById(UUID id) {
        mitigationRepository.deleteById(id);
    }

    @Transactional
    public StoveMitigationYear updateById(UUID id, StoveInstallationDTO dto) {
        // Fetch existing record
        StoveMitigationYear existing = mitigationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mitigation record not found: " + id));

        // Validate and fetch StoveType
        UUID stoveTypeId = dto.getStoveTypeId();
        StoveType stoveType = stoveTypeService.findById(stoveTypeId)
                .orElseThrow(() -> new IllegalArgumentException("StoveType not found: " + stoveTypeId));

        int year = dto.getYear();

        // Determine differenceInstalled compared to previous year for this stove type
        int previousUnits = mitigationRepository
                .findTopByStoveTypeIdAndYearLessThanOrderByYearDesc(stoveTypeId, year)
                .map(StoveMitigationYear::getUnitsInstalled)
                .orElse(0);

        int unitsInstalledThisYear = dto.getUnitsInstalledThisYear();
        int differenceInstalled = unitsInstalledThisYear - previousUnits;

        // constant = (baselinePercentage * 0.15) / 0.25
        double constant = (stoveType.getBaselinePercentage() * 0.15) / 0.25;

        // avoidedEmission = (constant * differenceInstalled) * 0.029 * ((4*112*0.647) +
        // 8.43) * 10^-3
        double conversionFactor = 0.029;
        double stoveFactor = 4 * 112 * 0.647;
        double assumptionsC11 = 8.43;
        double scale = 1e-3;

        double avoidedEmissions = (constant * differenceInstalled)
                * conversionFactor
                * (stoveFactor + assumptionsC11)
                * scale;

        // Update entity fields
        existing.setStoveType(stoveType);
        existing.setYear(year);
        existing.setUnitsInstalled(unitsInstalledThisYear);
        existing.setBau(dto.getBau());
        existing.setDifferenceInstalled(differenceInstalled);
        existing.setConstantValue(constant);
        existing.setAvoidedEmissions(avoidedEmissions);

        // Persist first to include this record in the yearly sum
        StoveMitigationYear saved = mitigationRepository.save(existing);

        // totalAvoidedEmission = sum of avoidedEmission of each type for this year
        double totalAvoided = mitigationRepository.sumAvoidedEmissionsByYear(year);
        double adjustment = dto.getBau() - totalAvoided;

        saved.setTotalAvoidedEmissions(totalAvoided);
        saved.setAdjustment(adjustment);

        return mitigationRepository.save(saved);
    }

    public byte[] generateExcelTemplate() {
        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Cookstove Mitigation");

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
            titleCell.setCellValue("Cookstove Mitigation Template");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));

            rowIdx++; // Blank row

            // Create header row
            Row headerRow = sheet.createRow(rowIdx++);
            headerRow.setHeightInPoints(22);
            String[] headers = {
                    "Year",
                    "Stove Type Name",
                    "Baseline Percentage",
                    "Units Installed This Year",
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
                    "Efficient Stove A",
                    75.5,
                    1000,
                    150.5
            };

            Object[] exampleData2 = {
                    2025,
                    "Improved Cookstove B",
                    80.0,
                    1200,
                    180.75
            };

            // First example row
            Row exampleRow1 = sheet.createRow(rowIdx++);
            exampleRow1.setHeightInPoints(18);
            for (int i = 0; i < exampleData1.length; i++) {
                Cell cell = exampleRow1.createCell(i);
                if (i == 0) { // Year
                    cell.setCellStyle(yearStyle);
                    cell.setCellValue(((Number) exampleData1[i]).intValue());
                } else if (i == 1) { // Stove Type Name (text)
                    cell.setCellStyle(dataStyle);
                    cell.setCellValue((String) exampleData1[i]);
                } else { // Baseline Percentage, Units, BAU (numbers)
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
                } else if (i == 1) { // Stove Type Name (text)
                    cell.setCellStyle(alternateDataStyle);
                    cell.setCellValue((String) exampleData2[i]);
                } else { // Baseline Percentage, Units, BAU (numbers)
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

    @Transactional
    public Map<String, Object> createCookstoveMitigationFromExcel(MultipartFile file) {
        List<StoveMitigationYear> savedRecords = new ArrayList<>();
        List<String> skippedRecords = new ArrayList<>();
        int totalProcessed = 0;

        try {
            List<CookstoveMitigationExcelDto> dtos = ExcelReader.readExcel(
                    file.getInputStream(),
                    CookstoveMitigationExcelDto.class,
                    ExcelType.COOKSTOVE_MITIGATION);

            for (int i = 0; i < dtos.size(); i++) {
                CookstoveMitigationExcelDto dto = dtos.get(i);
                // Calculate actual Excel row number: headerRowIndex(2) + 1 + i + 1 (1-based) =
                // i + 4
                int actualRowNumber = i + 4;

                // Check if row is effectively empty (missing critical fields) - skip it
                // A row is effectively empty if Year is missing AND StoveTypeName is empty
                // This handles cases where Excel has formatting but no actual data
                boolean isEffectivelyEmpty = (dto.getYear() == 0)
                        && (dto.getStoveTypeName() == null || dto.getStoveTypeName().trim().isEmpty());

                if (isEffectivelyEmpty) {
                    // Skip this row - it's effectively empty (likely formatting or blank row)
                    continue;
                }

                totalProcessed++;

                // Validate required fields for non-empty rows
                List<String> missingFields = new ArrayList<>();
                if (dto.getYear() == 0) {
                    missingFields.add("Year");
                }
                if (dto.getStoveTypeName() == null || dto.getStoveTypeName().trim().isEmpty()) {
                    missingFields.add("Stove Type Name");
                }
                // Baseline percentage validation - only required if we need to create new stove
                // type
                // We'll check this after trying to find the stove type
                if (dto.getUnitsInstalledThisYear() <= 0) {
                    missingFields.add("Units Installed This Year");
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

                // Normalize stove type name (trim for comparison)
                String normalizedName = dto.getStoveTypeName().trim();

                // Check if stove type exists (case-insensitive) and find or create
                StoveType stoveType;
                try {
                    // Need baselinePercentage if creating new stove type
                    double baselinePercentage = dto.getBaselinePercentage() != null
                            ? dto.getBaselinePercentage()
                            : 0.0;

                    // Check if we need to create new stove type
                    Optional<StoveType> existingStoveType = stoveTypeService.findByNameIgnoreCase(normalizedName);
                    if (existingStoveType.isEmpty()) {
                        // Need to create new stove type - baselinePercentage is required
                        if (dto.getBaselinePercentage() == null || dto.getBaselinePercentage() < 0) {
                            throw new RuntimeException(String.format(
                                    "Row %d: Stove type '%s' does not exist. Baseline Percentage is required for new stove types.",
                                    actualRowNumber, normalizedName));
                        }
                        stoveType = stoveTypeService.findOrCreateStoveType(normalizedName, baselinePercentage);
                    } else {
                        // Use existing stove type (ignore baselinePercentage from Excel)
                        stoveType = existingStoveType.get();
                    }
                } catch (RuntimeException e) {
                    // Re-throw with row number context if not already included
                    if (e.getMessage().contains("Row")) {
                        throw e;
                    }
                    throw new RuntimeException(String.format("Row %d: %s", actualRowNumber, e.getMessage()), e);
                }

                // Check if record with same year AND stoveTypeId already exists (composite
                // uniqueness)
                if (mitigationRepository.findByYearAndStoveTypeId(dto.getYear(), stoveType.getId()).isPresent()) {
                    skippedRecords.add(dto.getYear() + "-" + normalizedName);
                    continue; // Skip this row
                }

                // Create StoveInstallationDTO from Excel DTO
                StoveInstallationDTO installationDto = new StoveInstallationDTO();
                installationDto.setYear(dto.getYear());
                installationDto.setStoveTypeId(stoveType.getId());
                installationDto.setUnitsInstalledThisYear(dto.getUnitsInstalledThisYear());
                installationDto.setBau(dto.getBau());

                // Create the mitigation record using existing createMitigation method
                StoveMitigationYear saved = createMitigation(installationDto);
                savedRecords.add(saved);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("saved", savedRecords);
            result.put("savedCount", savedRecords.size());
            result.put("skippedCount", skippedRecords.size());
            result.put("skippedRecords", skippedRecords);
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
