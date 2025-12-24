package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.protectiveForest.service;

import com.navyn.emissionlog.Enums.ExcelType;
import com.navyn.emissionlog.Enums.Metrics.AreaUnits;
import com.navyn.emissionlog.Enums.Metrics.VolumePerAreaUnit;
import com.navyn.emissionlog.Enums.Mitigation.ProtectiveForestCategory;
import com.navyn.emissionlog.Enums.Mitigation.ProtectiveForestConstants;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.protectiveForest.dtos.ProtectiveForestMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.protectiveForest.models.ProtectiveForestMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.protectiveForest.repositories.ProtectiveForestMitigationRepository;
import com.navyn.emissionlog.utils.ExcelReader;
import com.navyn.emissionlog.utils.Specifications.MitigationSpecifications;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProtectiveForestMitigationServiceImpl implements ProtectiveForestMitigationService {
    
    private final ProtectiveForestMitigationRepository repository;
    
    @Override
    public ProtectiveForestMitigation createProtectiveForestMitigation(ProtectiveForestMitigationDto dto) {
        ProtectiveForestMitigation mitigation = new ProtectiveForestMitigation();

        Optional<ProtectiveForestMitigation> lastYearRecord = repository.findTopByYearLessThanAndCategoryOrderByYearDesc(dto.getYear(), dto.getCategory());
        Double cumulativeArea = lastYearRecord.map(protectiveForestMitigation -> protectiveForestMitigation.getCumulativeArea() + protectiveForestMitigation.getAreaPlanted()).orElse(0.0);
        
        // Convert units to standard values
        double areaPlantedInHectares = dto.getAreaPlantedUnit().toHectares(dto.getAreaPlanted());
        double agbCurrentYearInCubicMeterPerHA = dto.getAgbUnit().toCubicMeterPerHA(dto.getAgbCurrentYear());
        
        // Map input fields (store in standard units)
        mitigation.setYear(dto.getYear());
        mitigation.setCategory(dto.getCategory());
        mitigation.setCumulativeArea(cumulativeArea);
        mitigation.setAreaPlanted(areaPlantedInHectares);
        mitigation.setAgbCurrentYear(agbCurrentYearInCubicMeterPerHA);
        
        // AUTO-FETCH previous year's AGB from DB
        double previousYearAGB = repository
            .findTopByYearLessThanAndCategoryOrderByYearDesc(dto.getYear(), dto.getCategory())
            .map(ProtectiveForestMitigation::getAgbCurrentYear)
            .orElse(0.0);
        
        mitigation.setAgbPreviousYear(previousYearAGB);
        
        // 1. Calculate AGB Growth (tonnes m3/ha)
        double agbGrowth = agbCurrentYearInCubicMeterPerHA - previousYearAGB;
        mitigation.setAgbGrowth(agbGrowth);
        
        // 2. Calculate Aboveground Biomass Growth (tonnes DM/ha)
        double abovegroundBiomassGrowth = agbGrowth * 
            ProtectiveForestConstants.CONVERSION_M3_TO_TONNES_DM.getValue();
        mitigation.setAbovegroundBiomassGrowth(abovegroundBiomassGrowth);
        
        // 3. Calculate Total Biomass (tonnes DM/year)
        double totalBiomass = cumulativeArea * abovegroundBiomassGrowth;
        mitigation.setTotalBiomass(totalBiomass);
        
        // 4. Calculate Biomass Carbon Increase (tonnes C/year)
        double biomassCarbonIncrease = totalBiomass * 
            ProtectiveForestConstants.CARBON_CONTENT_DRY_WOOD.getValue();
        mitigation.setBiomassCarbonIncrease(biomassCarbonIncrease);
        
        // 5. Calculate Mitigated Emissions (Kt CO2e)
        double mitigatedEmissions = (biomassCarbonIncrease * 
            ProtectiveForestConstants.CONVERSION_C_TO_CO2.getValue()) / 1000.0;
        mitigation.setMitigatedEmissionsKtCO2e(mitigatedEmissions);
        
        return repository.save(mitigation);
    }
    
    @Override
    public ProtectiveForestMitigation updateProtectiveForestMitigation(UUID id, ProtectiveForestMitigationDto dto) {
        ProtectiveForestMitigation mitigation = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Protective Forest Mitigation record not found with id: " + id));

        // Update the current record
        recalculateAndUpdateRecord(mitigation, dto);
        ProtectiveForestMitigation updatedRecord = repository.save(mitigation);
        
        // CASCADE: Find and recalculate all subsequent years for the same category
        List<ProtectiveForestMitigation> subsequentRecords = repository
            .findByYearGreaterThanAndCategoryOrderByYearAsc(dto.getYear(), dto.getCategory());
        
        for (ProtectiveForestMitigation subsequent : subsequentRecords) {
            recalculateExistingRecord(subsequent);
            repository.save(subsequent);
        }
        
        return updatedRecord;
    }

    @Override
    public void deleteProtectiveForestMitigation(UUID id) {
        ProtectiveForestMitigation mitigation = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Protective Forest Mitigation record not found with id: " + id));

        Integer year = mitigation.getYear();
        ProtectiveForestCategory category = mitigation.getCategory();
        repository.delete(mitigation);

        // Recalculate all subsequent years for this category because cumulative fields depend on previous records
        List<ProtectiveForestMitigation> subsequentRecords =
            repository.findByYearGreaterThanAndCategoryOrderByYearAsc(year, category);
        for (ProtectiveForestMitigation subsequent : subsequentRecords) {
            recalculateExistingRecord(subsequent);
            repository.save(subsequent);
        }
    }
    
    /**
     * Recalculates an existing record based on its current year and stored input values
     */
    private void recalculateExistingRecord(ProtectiveForestMitigation mitigation) {
        Optional<ProtectiveForestMitigation> lastYearRecord = repository
            .findTopByYearLessThanAndCategoryOrderByYearDesc(mitigation.getYear(), mitigation.getCategory());
        Double cumulativeArea = lastYearRecord.map(protectiveForestMitigation -> 
            protectiveForestMitigation.getCumulativeArea() + protectiveForestMitigation.getAreaPlanted()
        ).orElse(0.0);

        mitigation.setCumulativeArea(cumulativeArea);
        
        // AUTO-FETCH previous year's AGB from DB
        double previousYearAGB = repository
            .findTopByYearLessThanAndCategoryOrderByYearDesc(mitigation.getYear(), mitigation.getCategory())
            .map(ProtectiveForestMitigation::getAgbCurrentYear)
            .orElse(0.0);
        
        mitigation.setAgbPreviousYear(previousYearAGB);
        
        // Recalculate all derived fields using existing values
        double agbCurrentYearInCubicMeterPerHA = mitigation.getAgbCurrentYear();
        
        double agbGrowth = agbCurrentYearInCubicMeterPerHA - previousYearAGB;
        mitigation.setAgbGrowth(agbGrowth);
        
        double abovegroundBiomassGrowth = agbGrowth * 
            ProtectiveForestConstants.CONVERSION_M3_TO_TONNES_DM.getValue();
        mitigation.setAbovegroundBiomassGrowth(abovegroundBiomassGrowth);
        
        double totalBiomass = cumulativeArea * abovegroundBiomassGrowth;
        mitigation.setTotalBiomass(totalBiomass);
        
        double biomassCarbonIncrease = totalBiomass * 
            ProtectiveForestConstants.CARBON_CONTENT_DRY_WOOD.getValue();
        mitigation.setBiomassCarbonIncrease(biomassCarbonIncrease);
        
        double mitigatedEmissions = (biomassCarbonIncrease * 
            ProtectiveForestConstants.CONVERSION_C_TO_CO2.getValue()) / 1000.0;
        mitigation.setMitigatedEmissionsKtCO2e(mitigatedEmissions);
    }
    
    /**
     * Recalculates a record with new DTO values
     */
    private void recalculateAndUpdateRecord(ProtectiveForestMitigation mitigation, ProtectiveForestMitigationDto dto) {
        Optional<ProtectiveForestMitigation> lastYearRecord = repository
            .findTopByYearLessThanAndCategoryOrderByYearDesc(dto.getYear(), dto.getCategory());
        Double cumulativeArea = lastYearRecord.map(protectiveForestMitigation -> 
            protectiveForestMitigation.getCumulativeArea() + protectiveForestMitigation.getAreaPlanted()
        ).orElse(0.0);
        
        // Convert units to standard values
        double areaPlantedInHectares = dto.getAreaPlantedUnit().toHectares(dto.getAreaPlanted());
        double agbCurrentYearInCubicMeterPerHA = dto.getAgbUnit().toCubicMeterPerHA(dto.getAgbCurrentYear());
        
        // Update input fields (store in standard units)
        mitigation.setYear(dto.getYear());
        mitigation.setCategory(dto.getCategory());
        mitigation.setCumulativeArea(cumulativeArea);
        mitigation.setAreaPlanted(areaPlantedInHectares);
        mitigation.setAgbCurrentYear(agbCurrentYearInCubicMeterPerHA);
        
        // AUTO-FETCH previous year's AGB from DB
        double previousYearAGB = repository
            .findTopByYearLessThanAndCategoryOrderByYearDesc(dto.getYear(), dto.getCategory())
            .map(ProtectiveForestMitigation::getAgbCurrentYear)
            .orElse(0.0);
        
        mitigation.setAgbPreviousYear(previousYearAGB);
        
        // Recalculate derived fields
        double agbGrowth = agbCurrentYearInCubicMeterPerHA - previousYearAGB;
        mitigation.setAgbGrowth(agbGrowth);
        
        double abovegroundBiomassGrowth = agbGrowth * 
            ProtectiveForestConstants.CONVERSION_M3_TO_TONNES_DM.getValue();
        mitigation.setAbovegroundBiomassGrowth(abovegroundBiomassGrowth);
        
        double totalBiomass = cumulativeArea * abovegroundBiomassGrowth;
        mitigation.setTotalBiomass(totalBiomass);
        
        double biomassCarbonIncrease = totalBiomass * 
            ProtectiveForestConstants.CARBON_CONTENT_DRY_WOOD.getValue();
        mitigation.setBiomassCarbonIncrease(biomassCarbonIncrease);
        
        double mitigatedEmissions = (biomassCarbonIncrease * 
            ProtectiveForestConstants.CONVERSION_C_TO_CO2.getValue()) / 1000.0;
        mitigation.setMitigatedEmissionsKtCO2e(mitigatedEmissions);
    }
    
    @Override
    public List<ProtectiveForestMitigation> getAllProtectiveForestMitigation(
            Integer year, 
            ProtectiveForestCategory category) {
        
        Specification<ProtectiveForestMitigation> spec = Specification.where(null);
        
        if (year != null) {
            spec = spec.and(MitigationSpecifications.hasYear(year));
        }
        
        if (category != null) {
            spec = spec.and(MitigationSpecifications.hasProtectiveForestCategory(category));
        }
        
        return repository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
    }
    
    @Override
    public Optional<ProtectiveForestMitigation> getByYearAndCategory(
            Integer year, 
            ProtectiveForestCategory category) {
        return repository.findByYearAndCategory(year, category);
    }

    @Override
    public byte[] generateExcelTemplate() {
        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Protective Forest Mitigation");

            // Create title style
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

            // Create header style
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
            titleCell.setCellValue("Protective Forest Mitigation Template");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));

            rowIdx++; // Blank row

            // Create header row
            Row headerRow = sheet.createRow(rowIdx++);
            headerRow.setHeightInPoints(22);
            String[] headers = {
                    "Year",
                    "Category",
                    "Area Planted",
                    "Area Planted Unit",
                    "AGB Current Year",
                    "AGB Unit"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Get enum values for dropdowns
            String[] categoryValues = Arrays.stream(ProtectiveForestCategory.values())
                    .map(Enum::name)
                    .toArray(String[]::new);
            String[] areaUnitValues = Arrays.stream(AreaUnits.values())
                    .map(Enum::name)
                    .toArray(String[]::new);
            String[] agbUnitValues = Arrays.stream(VolumePerAreaUnit.values())
                    .map(Enum::name)
                    .toArray(String[]::new);

            // Create data validation helper
            DataValidationHelper validationHelper = sheet.getDataValidationHelper();

            // Data validation for Category column (Column B, index 1)
            CellRangeAddressList categoryList = new CellRangeAddressList(3, 1000, 1, 1);
            DataValidationConstraint categoryConstraint = validationHelper
                    .createExplicitListConstraint(categoryValues);
            DataValidation categoryValidation = validationHelper.createValidation(categoryConstraint,
                    categoryList);
            categoryValidation.setShowErrorBox(true);
            categoryValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
            categoryValidation.createErrorBox("Invalid Category",
                    "Please select a valid category from the dropdown list.");
            categoryValidation.setShowPromptBox(true);
            categoryValidation.createPromptBox("Category", "Select a category from the dropdown list.");
            sheet.addValidationData(categoryValidation);

            // Data validation for Area Planted Unit column (Column D, index 3)
            CellRangeAddressList areaUnitList = new CellRangeAddressList(3, 1000, 3, 3);
            DataValidationConstraint areaUnitConstraint = validationHelper
                    .createExplicitListConstraint(areaUnitValues);
            DataValidation areaUnitValidation = validationHelper.createValidation(areaUnitConstraint,
                    areaUnitList);
            areaUnitValidation.setShowErrorBox(true);
            areaUnitValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
            areaUnitValidation.createErrorBox("Invalid Area Unit",
                    "Please select a valid area unit from the dropdown list.");
            areaUnitValidation.setShowPromptBox(true);
            areaUnitValidation.createPromptBox("Area Planted Unit", "Select an area unit from the dropdown list.");
            sheet.addValidationData(areaUnitValidation);

            // Data validation for AGB Unit column (Column F, index 5)
            CellRangeAddressList agbUnitList = new CellRangeAddressList(3, 1000, 5, 5);
            DataValidationConstraint agbUnitConstraint = validationHelper
                    .createExplicitListConstraint(agbUnitValues);
            DataValidation agbUnitValidation = validationHelper.createValidation(agbUnitConstraint,
                    agbUnitList);
            agbUnitValidation.setShowErrorBox(true);
            agbUnitValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
            agbUnitValidation.createErrorBox("Invalid AGB Unit",
                    "Please select a valid AGB unit from the dropdown list.");
            agbUnitValidation.setShowPromptBox(true);
            agbUnitValidation.createPromptBox("AGB Unit", "Select an AGB unit from the dropdown list.");
            sheet.addValidationData(agbUnitValidation);

            // Create example data rows
            Object[] exampleData1 = {
                    2024,
                    "PINUS_SPP",
                    100.0,
                    "HECTARES",
                    50.0,
                    "CUBIC_METER_PER_HA"
            };

            Object[] exampleData2 = {
                    2025,
                    "EUCALYPTUS_SPP",
                    150.0,
                    "HECTARES",
                    60.0,
                    "CUBIC_METER_PER_HA"
            };

            // First example row
            Row exampleRow1 = sheet.createRow(rowIdx++);
            exampleRow1.setHeightInPoints(18);
            for (int i = 0; i < exampleData1.length; i++) {
                Cell cell = exampleRow1.createCell(i);
                if (i == 0) { // Year
                    cell.setCellStyle(yearStyle);
                    cell.setCellValue(((Number) exampleData1[i]).intValue());
                } else if (i == 1) { // Category (string)
                    cell.setCellStyle(dataStyle);
                    cell.setCellValue((String) exampleData1[i]);
                } else if (i == 2 || i == 4) { // Numbers
                    cell.setCellStyle(numberStyle);
                    cell.setCellValue(((Number) exampleData1[i]).doubleValue());
                } else { // Units (string)
                    cell.setCellStyle(dataStyle);
                    cell.setCellValue((String) exampleData1[i]);
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
                } else if (i == 1) { // Category (string)
                    cell.setCellStyle(alternateDataStyle);
                    cell.setCellValue((String) exampleData2[i]);
                } else if (i == 2 || i == 4) { // Numbers
                    CellStyle altNumStyle = workbook.createCellStyle();
                    altNumStyle.cloneStyleFrom(numberStyle);
                    altNumStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                    altNumStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    cell.setCellStyle(altNumStyle);
                    cell.setCellValue(((Number) exampleData2[i]).doubleValue());
                } else { // Units (string)
                    cell.setCellStyle(alternateDataStyle);
                    cell.setCellValue((String) exampleData2[i]);
                }
            }

            // Auto-size columns with wider limits
            for (int i = 0; i < headers.length; i++) {
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

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error generating Excel template", e);
        }
    }

    @Override
    public Map<String, Object> createProtectiveForestMitigationFromExcel(MultipartFile file) {
        List<ProtectiveForestMitigation> savedRecords = new ArrayList<>();
        List<String> skippedRecords = new ArrayList<>();
        int totalProcessed = 0;

        try {
            List<ProtectiveForestMitigationDto> dtos = ExcelReader.readExcel(
                    file.getInputStream(),
                    ProtectiveForestMitigationDto.class,
                    ExcelType.PROTECTIVE_FOREST_MITIGATION);

            for (int i = 0; i < dtos.size(); i++) {
                ProtectiveForestMitigationDto dto = dtos.get(i);
                totalProcessed++;
                int actualRowNumber = i + 1 + 3; // +1 for 1-based, +3 for title(1) + blank(1) + header(1)

                // Validate required fields
                List<String> missingFields = new ArrayList<>();
                if (dto.getYear() == null) {
                    missingFields.add("Year");
                }
                if (dto.getCategory() == null) {
                    missingFields.add("Category");
                }
                if (dto.getAreaPlanted() == null) {
                    missingFields.add("Area Planted");
                }
                if (dto.getAreaPlantedUnit() == null) {
                    missingFields.add("Area Planted Unit");
                }
                if (dto.getAgbCurrentYear() == null) {
                    missingFields.add("AGB Current Year");
                }
                if (dto.getAgbUnit() == null) {
                    missingFields.add("AGB Unit");
                }

                if (!missingFields.isEmpty()) {
                    throw new RuntimeException(String.format(
                            "Row %d: Missing required fields: %s. Please fill in all required fields in your Excel file.",
                            actualRowNumber, String.join(", ", missingFields)));
                }

                // Check if year + category combination already exists (composite uniqueness)
                if (repository.findByYearAndCategory(dto.getYear(), dto.getCategory()).isPresent()) {
                    skippedRecords.add(String.format("Year %d, Category %s", dto.getYear(), dto.getCategory()));
                    continue; // Skip this row
                }

                // Create the record
                ProtectiveForestMitigation saved = createProtectiveForestMitigation(dto);
                savedRecords.add(saved);
            }

            Map<String, Object> result = new java.util.HashMap<>();
            result.put("savedCount", savedRecords.size());
            result.put("skippedCount", skippedRecords.size());
            result.put("skippedRecords", skippedRecords);
            result.put("totalProcessed", totalProcessed);
            return result;

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
