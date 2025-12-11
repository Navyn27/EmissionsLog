package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.service;

import com.navyn.emissionlog.Enums.ExcelType;
import com.navyn.emissionlog.Enums.Metrics.AreaUnits;
import com.navyn.emissionlog.Enums.Metrics.VolumePerAreaUnit;
import com.navyn.emissionlog.Enums.Mitigation.WetlandParksConstants;
import com.navyn.emissionlog.Enums.Mitigation.WetlandTreeCategory;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.dtos.WetlandParksMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.models.WetlandParksMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.repositories.WetlandParksMitigationRepository;
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
import java.util.*;

@Service
@RequiredArgsConstructor
public class WetlandParksMitigationServiceImpl implements WetlandParksMitigationService {

    private final WetlandParksMitigationRepository repository;

    @Override
    public WetlandParksMitigation createWetlandParksMitigation(WetlandParksMitigationDto dto) {
        WetlandParksMitigation mitigation = new WetlandParksMitigation();

        Optional<WetlandParksMitigation> lastYearRecord = repository
                .findTopByYearLessThanAndTreeCategoryOrderByYearDesc(dto.getYear(), dto.getTreeCategory());
        Double cumulativeArea = lastYearRecord.map(wetlandParksMitigation -> wetlandParksMitigation.getAreaPlanted()
                + wetlandParksMitigation.getCumulativeArea()).orElse(0.0);

        // Convert units to standard values
        double areaPlantedInHectares = dto.getAreaUnit().toHectares(dto.getAreaPlanted());
        double agbInCubicMeterPerHA = dto.getAgbUnit().toCubicMeterPerHA(dto.getAbovegroundBiomassAGB());

        // Map input fields (store in standard units)
        mitigation.setYear(dto.getYear());
        mitigation.setTreeCategory(dto.getTreeCategory());
        mitigation.setCumulativeArea(cumulativeArea);
        mitigation.setAreaPlanted(areaPlantedInHectares);
        mitigation.setAbovegroundBiomassAGB(agbInCubicMeterPerHA);

        // 1. Get previous year's AGB for SAME category
        Double previousAGB = getPreviousYearAGB(dto.getYear(), dto.getTreeCategory());
        mitigation.setPreviousYearAGB(previousAGB);

        // 2. Calculate AGB Growth (m3/ha)
        // AGB growth = AGB in current year - AGB in previous year
        double agbGrowth = agbInCubicMeterPerHA - previousAGB;
        mitigation.setAgbGrowth(agbGrowth);

        // 3. Calculate Aboveground Biomass Growth (tonnes DM/ha)
        // Aboveground Biomass Growth = AGB growth × Conversion m3 to tonnes DM
        double abovegroundBiomassGrowth = agbGrowth *
                WetlandParksConstants.CONVERSION_M3_TO_TONNES_DM.getValue();
        mitigation.setAbovegroundBiomassGrowth(abovegroundBiomassGrowth);

        // 4. Calculate Total Biomass (tonnes DM/year)
        // Total biomass = Cumulative area × Aboveground Biomass Growth
        double totalBiomass = cumulativeArea * abovegroundBiomassGrowth;
        mitigation.setTotalBiomass(totalBiomass);

        // 5. Calculate Biomass Carbon Increase (tonnes C/year)
        // Biomass carbon increase = Total biomass × Carbon content in dry wood
        double biomassCarbonIncrease = totalBiomass *
                WetlandParksConstants.CARBON_CONTENT_DRY_WOOD.getValue();
        mitigation.setBiomassCarbonIncrease(biomassCarbonIncrease);

        // 6. Calculate Mitigated Emissions (Kt CO2e)
        // Mitigated emissions = (Biomass carbon increase × Conversion C to CO2) / 1000
        double mitigatedEmissions = (biomassCarbonIncrease *
                WetlandParksConstants.CONVERSION_C_TO_CO2.getValue()) / 1000.0;
        mitigation.setMitigatedEmissionsKtCO2e(mitigatedEmissions);

        return repository.save(mitigation);
    }

    @Override
    public WetlandParksMitigation updateWetlandParksMitigation(UUID id, WetlandParksMitigationDto dto) {
        WetlandParksMitigation mitigation = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Wetland Parks Mitigation record not found with id: " + id));

        // Update the current record
        recalculateAndUpdateRecord(mitigation, dto);
        WetlandParksMitigation updatedRecord = repository.save(mitigation);

        // CASCADE: Find and recalculate all subsequent years for the same tree category
        List<WetlandParksMitigation> subsequentRecords = repository
                .findByYearGreaterThanAndTreeCategoryOrderByYearAsc(dto.getYear(), dto.getTreeCategory());

        for (WetlandParksMitigation subsequent : subsequentRecords) {
            recalculateExistingRecord(subsequent);
            repository.save(subsequent);
        }

        return updatedRecord;
    }

    @Override
    public void deleteWetlandParksMitigation(UUID id) {
        WetlandParksMitigation mitigation = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Wetland Parks Mitigation record not found with id: " + id));

        Integer year = mitigation.getYear();
        WetlandTreeCategory category = mitigation.getTreeCategory();
        repository.delete(mitigation);

        // Recalculate all subsequent years for this tree category because cumulative
        // fields depend on previous records
        List<WetlandParksMitigation> subsequentRecords = repository
                .findByYearGreaterThanAndTreeCategoryOrderByYearAsc(year, category);
        for (WetlandParksMitigation subsequent : subsequentRecords) {
            recalculateExistingRecord(subsequent);
            repository.save(subsequent);
        }
    }

    /**
     * Recalculates an existing record based on its current year and stored input
     * values
     */
    private void recalculateExistingRecord(WetlandParksMitigation mitigation) {
        Optional<WetlandParksMitigation> lastYearRecord = repository
                .findTopByYearLessThanAndTreeCategoryOrderByYearDesc(mitigation.getYear(),
                        mitigation.getTreeCategory());
        Double cumulativeArea = lastYearRecord.map(wetlandParksMitigation -> wetlandParksMitigation.getAreaPlanted()
                + wetlandParksMitigation.getCumulativeArea()).orElse(0.0);

        mitigation.setCumulativeArea(cumulativeArea);

        // Fetch previous year's AGB
        Double previousAGB = getPreviousYearAGB(mitigation.getYear(), mitigation.getTreeCategory());
        mitigation.setPreviousYearAGB(previousAGB);

        // Recalculate all derived fields using existing AGB value
        double agbInCubicMeterPerHA = mitigation.getAbovegroundBiomassAGB();

        double agbGrowth = agbInCubicMeterPerHA - previousAGB;
        mitigation.setAgbGrowth(agbGrowth);

        double abovegroundBiomassGrowth = agbGrowth *
                WetlandParksConstants.CONVERSION_M3_TO_TONNES_DM.getValue();
        mitigation.setAbovegroundBiomassGrowth(abovegroundBiomassGrowth);

        double totalBiomass = cumulativeArea * abovegroundBiomassGrowth;
        mitigation.setTotalBiomass(totalBiomass);

        double biomassCarbonIncrease = totalBiomass *
                WetlandParksConstants.CARBON_CONTENT_DRY_WOOD.getValue();
        mitigation.setBiomassCarbonIncrease(biomassCarbonIncrease);

        double mitigatedEmissions = (biomassCarbonIncrease *
                WetlandParksConstants.CONVERSION_C_TO_CO2.getValue()) / 1000.0;
        mitigation.setMitigatedEmissionsKtCO2e(mitigatedEmissions);
    }

    /**
     * Recalculates a record with new DTO values
     */
    private void recalculateAndUpdateRecord(WetlandParksMitigation mitigation, WetlandParksMitigationDto dto) {
        Optional<WetlandParksMitigation> lastYearRecord = repository
                .findTopByYearLessThanAndTreeCategoryOrderByYearDesc(dto.getYear(), dto.getTreeCategory());
        Double cumulativeArea = lastYearRecord.map(wetlandParksMitigation -> wetlandParksMitigation.getAreaPlanted()
                + wetlandParksMitigation.getCumulativeArea()).orElse(0.0);

        // Convert units to standard values
        double areaPlantedInHectares = dto.getAreaUnit().toHectares(dto.getAreaPlanted());
        double agbInCubicMeterPerHA = dto.getAgbUnit().toCubicMeterPerHA(dto.getAbovegroundBiomassAGB());

        // Update input fields (store in standard units)
        mitigation.setYear(dto.getYear());
        mitigation.setTreeCategory(dto.getTreeCategory());
        mitigation.setCumulativeArea(cumulativeArea);
        mitigation.setAreaPlanted(areaPlantedInHectares);
        mitigation.setAbovegroundBiomassAGB(agbInCubicMeterPerHA);

        // Recalculate derived fields
        Double previousAGB = getPreviousYearAGB(dto.getYear(), dto.getTreeCategory());
        mitigation.setPreviousYearAGB(previousAGB);

        double agbGrowth = agbInCubicMeterPerHA - previousAGB;
        mitigation.setAgbGrowth(agbGrowth);

        double abovegroundBiomassGrowth = agbGrowth *
                WetlandParksConstants.CONVERSION_M3_TO_TONNES_DM.getValue();
        mitigation.setAbovegroundBiomassGrowth(abovegroundBiomassGrowth);

        double totalBiomass = cumulativeArea * abovegroundBiomassGrowth;
        mitigation.setTotalBiomass(totalBiomass);

        double biomassCarbonIncrease = totalBiomass *
                WetlandParksConstants.CARBON_CONTENT_DRY_WOOD.getValue();
        mitigation.setBiomassCarbonIncrease(biomassCarbonIncrease);

        double mitigatedEmissions = (biomassCarbonIncrease *
                WetlandParksConstants.CONVERSION_C_TO_CO2.getValue()) / 1000.0;
        mitigation.setMitigatedEmissionsKtCO2e(mitigatedEmissions);
    }

    @Override
    public List<WetlandParksMitigation> getAllWetlandParksMitigation(
            Integer year, WetlandTreeCategory category) {
        Specification<WetlandParksMitigation> spec = Specification
                .<WetlandParksMitigation>where(MitigationSpecifications.hasYear(year))
                .and(MitigationSpecifications.hasWetlandTreeCategory(category));
        return repository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
    }

    @Override
    public Optional<WetlandParksMitigation> getByYearAndCategory(
            Integer year, WetlandTreeCategory category) {
        return repository.findByYearAndTreeCategory(year, category);
    }

    // Helper method to get previous year's AGB for same category
    private Double getPreviousYearAGB(Integer currentYear, WetlandTreeCategory category) {
        Optional<WetlandParksMitigation> previous = repository
                .findByYearAndTreeCategory(currentYear - 1, category);
        return previous.map(WetlandParksMitigation::getAbovegroundBiomassAGB).orElse(0.0);
    }

    @Override
    public byte[] generateExcelTemplate() {
        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Wetland Parks Mitigation");

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
            titleCell.setCellValue("Wetland Parks Mitigation Template");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));

            rowIdx++; // Blank row

            // Create header row
            Row headerRow = sheet.createRow(rowIdx++);
            headerRow.setHeightInPoints(22);
            String[] headers = {
                    "Year",
                    "Tree Category",
                    "Area Planted",
                    "Area Unit",
                    "Aboveground Biomass AGB",
                    "AGB Unit"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Get enum values for dropdowns
            String[] treeCategoryValues = Arrays.stream(WetlandTreeCategory.values())
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

            // Data validation for Tree Category column (Column B, index 1)
            CellRangeAddressList treeCategoryList = new CellRangeAddressList(
                    3, 1000, 1, 1);
            DataValidationConstraint treeCategoryConstraint = validationHelper
                    .createExplicitListConstraint(treeCategoryValues);
            DataValidation treeCategoryValidation = validationHelper.createValidation(treeCategoryConstraint,
                    treeCategoryList);
            treeCategoryValidation.setShowErrorBox(true);
            treeCategoryValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
            treeCategoryValidation.createErrorBox("Invalid Tree Category",
                    "Please select a valid tree category from the dropdown list.");
            treeCategoryValidation.setShowPromptBox(true);
            treeCategoryValidation.createPromptBox("Tree Category", "Select a tree category from the dropdown list.");
            sheet.addValidationData(treeCategoryValidation);

            // Data validation for Area Unit column (Column D, index 3)
            CellRangeAddressList areaUnitList = new CellRangeAddressList(
                    3, 1000, 3, 3);
            DataValidationConstraint areaUnitConstraint = validationHelper.createExplicitListConstraint(areaUnitValues);
            DataValidation areaUnitValidation = validationHelper.createValidation(areaUnitConstraint, areaUnitList);
            areaUnitValidation.setShowErrorBox(true);
            areaUnitValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
            areaUnitValidation.createErrorBox("Invalid Area Unit",
                    "Please select a valid area unit from the dropdown list.");
            areaUnitValidation.setShowPromptBox(true);
            areaUnitValidation.createPromptBox("Area Unit", "Select an area unit from the dropdown list.");
            sheet.addValidationData(areaUnitValidation);

            // Data validation for AGB Unit column (Column F, index 5)
            CellRangeAddressList agbUnitList = new CellRangeAddressList(
                    3, 1000, 5, 5);
            DataValidationConstraint agbUnitConstraint = validationHelper.createExplicitListConstraint(agbUnitValues);
            DataValidation agbUnitValidation = validationHelper.createValidation(agbUnitConstraint, agbUnitList);
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
                    "BAMBOO_SPP",
                    50.5,
                    "HECTARES",
                    25.3,
                    "CUBIC_METER_PER_HA"
            };

            Object[] exampleData2 = {
                    2025,
                    "ACACIA_SPP",
                    75.25,
                    "ACRES",
                    30.5,
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
                } else if (i == 1) { // Tree Category
                    cell.setCellStyle(dataStyle);
                    cell.setCellValue((String) exampleData1[i]);
                } else if (i == 2 || i == 4) { // Area Planted or AGB (numbers)
                    cell.setCellStyle(numberStyle);
                    cell.setCellValue(((Number) exampleData1[i]).doubleValue());
                } else { // Units (strings)
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
                } else if (i == 1) { // Tree Category
                    cell.setCellStyle(alternateDataStyle);
                    cell.setCellValue((String) exampleData2[i]);
                } else if (i == 2 || i == 4) { // Area Planted or AGB (numbers)
                    CellStyle altNumStyle = workbook.createCellStyle();
                    altNumStyle.cloneStyleFrom(numberStyle);
                    altNumStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                    altNumStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    cell.setCellStyle(altNumStyle);
                    cell.setCellValue(((Number) exampleData2[i]).doubleValue());
                } else { // Units (strings)
                    cell.setCellStyle(alternateDataStyle);
                    cell.setCellValue((String) exampleData2[i]);
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
    public Map<String, Object> createWetlandParksMitigationFromExcel(MultipartFile file) {
        List<WetlandParksMitigation> savedRecords = new ArrayList<>();
        List<String> skippedRecords = new ArrayList<>(); // Store "year-category" strings
        int totalProcessed = 0;

        try {
            List<WetlandParksMitigationDto> dtos = ExcelReader.readExcel(
                    file.getInputStream(),
                    WetlandParksMitigationDto.class,
                    ExcelType.WETLAND_PARKS_MITIGATION);

            for (WetlandParksMitigationDto dto : dtos) {
                totalProcessed++;

                // Check if record with same year AND category already exists
                if (repository.findByYearAndTreeCategory(dto.getYear(), dto.getTreeCategory()).isPresent()) {
                    skippedRecords.add(dto.getYear() + "-" + dto.getTreeCategory().name());
                    continue; // Skip this row
                }

                // Create the record
                WetlandParksMitigation saved = createWetlandParksMitigation(dto);
                savedRecords.add(saved);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("saved", savedRecords);
            result.put("savedCount", savedRecords.size());
            result.put("skippedCount", skippedRecords.size());
            result.put("skippedRecords", skippedRecords);
            result.put("totalProcessed", totalProcessed);

            return result;
        } catch (Exception e) {
            throw new RuntimeException("Error reading Wetland Parks Mitigation from Excel file: " + e.getMessage(), e);
        }
    }
}
