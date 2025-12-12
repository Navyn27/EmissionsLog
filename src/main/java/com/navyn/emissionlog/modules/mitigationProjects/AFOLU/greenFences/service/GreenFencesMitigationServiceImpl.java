package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.greenFences.service;

import com.navyn.emissionlog.Enums.ExcelType;
import com.navyn.emissionlog.Enums.Metrics.BiomassUnit;
import com.navyn.emissionlog.Enums.Mitigation.GreenFencesConstants;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.greenFences.dtos.GreenFencesMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.greenFences.models.GreenFencesMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.greenFences.repositories.GreenFencesMitigationRepository;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GreenFencesMitigationServiceImpl implements GreenFencesMitigationService {
    
    private final GreenFencesMitigationRepository repository;
    
    @Override
    public GreenFencesMitigation createGreenFencesMitigation(GreenFencesMitigationDto dto) {
        GreenFencesMitigation mitigation = new GreenFencesMitigation();

        Optional<GreenFencesMitigation> latestYear = repository.findTopByYearLessThanOrderByYearDesc(dto.getYear());
        Double cumulativeHouseholds = latestYear.map(greenFencesMitigation -> greenFencesMitigation.getCumulativeNumberOfHouseholds() + greenFencesMitigation.getNumberOfHouseholdsWith10m2Fence()).orElse(0.0);

        // Convert AGB to tonnes DM (standard unit)
        double agbInTonnesDM = dto.getAgbUnit().toTonnesDM(dto.getAgbOf10m2LiveFence());

        // Map input fields (store in standard units)
        mitigation.setYear(dto.getYear());
        mitigation.setCumulativeNumberOfHouseholds(cumulativeHouseholds);
        mitigation.setNumberOfHouseholdsWith10m2Fence(dto.getNumberOfHouseholdsWith10m2Fence());
        mitigation.setAgbOf10m2LiveFence(agbInTonnesDM);
        
        // 1. Calculate AGB of 10m3 fence biomass from cumulative households (Tonnes C)
        // AGB fence biomass = AGB of 10m2 fence × Carbon content × Cumulative households
        double agbFenceBiomass = agbInTonnesDM * 
            GreenFencesConstants.CARBON_CONTENT_DRY_AGB.getValue() * 
            cumulativeHouseholds;
        mitigation.setAgbFenceBiomassCumulativeHouseholds(agbFenceBiomass);
        
        // 2. Calculate AGB+BGB from cumulative households (Tonnes C)
        // Total biomass = AGB fence biomass × (1 + Ratio BGB to AGB)
        double totalBiomass = agbFenceBiomass * 
            (1 + GreenFencesConstants.RATIO_BGB_TO_AGB.getValue());
        mitigation.setAgbPlusBgbCumulativeHouseholds(totalBiomass);
        
        // 3. Calculate Mitigated Emissions (Kt CO2e)
        // NOTE: Uses AGB only (not total with BGB) for emissions calculation
        // Mitigated emissions = AGB fence biomass × Conversion C to CO2 / 1000
        double mitigatedEmissions = (agbFenceBiomass * 
            GreenFencesConstants.CONVERSION_C_TO_CO2.getValue()) / 1000.0;
        mitigation.setMitigatedEmissionsKtCO2e(mitigatedEmissions);
        
        return repository.save(mitigation);
    }
    
    @Override
    public GreenFencesMitigation updateGreenFencesMitigation(UUID id, GreenFencesMitigationDto dto) {
        GreenFencesMitigation mitigation = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Green Fences Mitigation record not found with id: " + id));

        // Update the current record
        recalculateAndUpdateRecord(mitigation, dto);
        GreenFencesMitigation updatedRecord = repository.save(mitigation);
        
        // CASCADE: Find and recalculate all subsequent years that depend on this record
        List<GreenFencesMitigation> subsequentRecords = repository.findByYearGreaterThanOrderByYearAsc(dto.getYear());
        
        for (GreenFencesMitigation subsequent : subsequentRecords) {
            recalculateExistingRecord(subsequent);
            repository.save(subsequent);
        }
        
        return updatedRecord;
    }

    @Override
    public void deleteGreenFencesMitigation(UUID id) {
        GreenFencesMitigation mitigation = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Green Fences Mitigation record not found with id: " + id));

        Integer year = mitigation.getYear();
        repository.delete(mitigation);

        // Recalculate all subsequent years as cumulative fields depend on previous records
        List<GreenFencesMitigation> subsequentRecords = repository.findByYearGreaterThanOrderByYearAsc(year);
        for (GreenFencesMitigation subsequent : subsequentRecords) {
            recalculateExistingRecord(subsequent);
            repository.save(subsequent);
        }
    }
    
    /**
     * Recalculates an existing record based on its current year and stored input values
     */
    private void recalculateExistingRecord(GreenFencesMitigation mitigation) {
        // Fetch previous year's data for cumulative calculation
        Optional<GreenFencesMitigation> latestYear = repository.findTopByYearLessThanOrderByYearDesc(mitigation.getYear());
        Double cumulativeHouseholds = latestYear.map(greenFencesMitigation -> 
            greenFencesMitigation.getCumulativeNumberOfHouseholds() + greenFencesMitigation.getNumberOfHouseholdsWith10m2Fence()
        ).orElse(0.0);

        // Update cumulative field
        mitigation.setCumulativeNumberOfHouseholds(cumulativeHouseholds);
        
        // Recalculate derived fields using existing AGB value
        double agbInTonnesDM = mitigation.getAgbOf10m2LiveFence();
        
        double agbFenceBiomass = agbInTonnesDM * 
            GreenFencesConstants.CARBON_CONTENT_DRY_AGB.getValue() * 
            cumulativeHouseholds;
        mitigation.setAgbFenceBiomassCumulativeHouseholds(agbFenceBiomass);
        
        double totalBiomass = agbFenceBiomass * 
            (1 + GreenFencesConstants.RATIO_BGB_TO_AGB.getValue());
        mitigation.setAgbPlusBgbCumulativeHouseholds(totalBiomass);
        
        double mitigatedEmissions = (agbFenceBiomass * 
            GreenFencesConstants.CONVERSION_C_TO_CO2.getValue()) / 1000.0;
        mitigation.setMitigatedEmissionsKtCO2e(mitigatedEmissions);
    }
    
    /**
     * Recalculates a record with new DTO values
     */
    private void recalculateAndUpdateRecord(GreenFencesMitigation mitigation, GreenFencesMitigationDto dto) {
        Optional<GreenFencesMitigation> latestYear = repository.findTopByYearLessThanOrderByYearDesc(dto.getYear());
        Double cumulativeHouseholds = latestYear.map(greenFencesMitigation -> 
            greenFencesMitigation.getCumulativeNumberOfHouseholds() + greenFencesMitigation.getNumberOfHouseholdsWith10m2Fence()
        ).orElse(0.0);

        // Convert AGB to tonnes DM (standard unit)
        double agbInTonnesDM = dto.getAgbUnit().toTonnesDM(dto.getAgbOf10m2LiveFence());

        // Update input fields (store in standard units)
        mitigation.setYear(dto.getYear());
        mitigation.setCumulativeNumberOfHouseholds(cumulativeHouseholds);
        mitigation.setNumberOfHouseholdsWith10m2Fence(dto.getNumberOfHouseholdsWith10m2Fence());
        mitigation.setAgbOf10m2LiveFence(agbInTonnesDM);
        
        // Recalculate derived fields
        double agbFenceBiomass = agbInTonnesDM * 
            GreenFencesConstants.CARBON_CONTENT_DRY_AGB.getValue() * 
            cumulativeHouseholds;
        mitigation.setAgbFenceBiomassCumulativeHouseholds(agbFenceBiomass);
        
        double totalBiomass = agbFenceBiomass * 
            (1 + GreenFencesConstants.RATIO_BGB_TO_AGB.getValue());
        mitigation.setAgbPlusBgbCumulativeHouseholds(totalBiomass);
        
        double mitigatedEmissions = (agbFenceBiomass * 
            GreenFencesConstants.CONVERSION_C_TO_CO2.getValue()) / 1000.0;
        mitigation.setMitigatedEmissionsKtCO2e(mitigatedEmissions);
    }
    
    @Override
    public List<GreenFencesMitigation> getAllGreenFencesMitigation(Integer year) {
        Specification<GreenFencesMitigation> spec = 
            Specification.<GreenFencesMitigation>where(MitigationSpecifications.hasYear(year));
        return repository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
    }

    @Override
    public Optional<GreenFencesMitigation> getByYear(Integer year) {
        return repository.findByYear(year);
    }

    @Override
    public byte[] generateExcelTemplate() {
        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Green Fences Mitigation");

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
            titleCell.setCellValue("Green Fences Mitigation Template");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

            rowIdx++; // Blank row

            // Create header row
            Row headerRow = sheet.createRow(rowIdx++);
            headerRow.setHeightInPoints(22);
            String[] headers = {
                    "Year",
                    "Number of Households with 10m2 Fence",
                    "AGB of 10m2 Live Fence",
                    "AGB Unit"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Get enum values for dropdowns
            String[] agbUnitValues = Arrays.stream(BiomassUnit.values())
                    .map(Enum::name)
                    .toArray(String[]::new);

            // Create data validation helper
            DataValidationHelper validationHelper = sheet.getDataValidationHelper();

            // Data validation for AGB Unit column (Column D, index 3)
            CellRangeAddressList agbUnitList = new CellRangeAddressList(3, 1000, 3, 3);
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
                    1000.0,
                    5.0,
                    "TONNES_DM"
            };

            Object[] exampleData2 = {
                    2025,
                    1500.0,
                    6.0,
                    "TONNES_DM"
            };

            // First example row
            Row exampleRow1 = sheet.createRow(rowIdx++);
            exampleRow1.setHeightInPoints(18);
            for (int i = 0; i < exampleData1.length; i++) {
                Cell cell = exampleRow1.createCell(i);
                if (i == 0) { // Year
                    cell.setCellStyle(yearStyle);
                    cell.setCellValue(((Number) exampleData1[i]).intValue());
                } else if (i == 1 || i == 2) { // Numbers
                    cell.setCellStyle(numberStyle);
                    cell.setCellValue(((Number) exampleData1[i]).doubleValue());
                } else { // Unit (string)
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
                } else if (i == 1 || i == 2) { // Numbers
                    CellStyle altNumStyle = workbook.createCellStyle();
                    altNumStyle.cloneStyleFrom(numberStyle);
                    altNumStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                    altNumStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    cell.setCellStyle(altNumStyle);
                    cell.setCellValue(((Number) exampleData2[i]).doubleValue());
                } else { // Unit (string)
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
    public Map<String, Object> createGreenFencesMitigationFromExcel(MultipartFile file) {
        List<GreenFencesMitigation> savedRecords = new ArrayList<>();
        List<Integer> skippedYears = new ArrayList<>();
        int totalProcessed = 0;

        try {
            List<GreenFencesMitigationDto> dtos = ExcelReader.readExcel(
                    file.getInputStream(),
                    GreenFencesMitigationDto.class,
                    ExcelType.GREEN_FENCES_MITIGATION);

            for (int i = 0; i < dtos.size(); i++) {
                GreenFencesMitigationDto dto = dtos.get(i);
                totalProcessed++;
                int actualRowNumber = i + 1 + 3; // +1 for 1-based, +3 for title(1) + blank(1) + header(1)

                // Validate required fields
                List<String> missingFields = new ArrayList<>();
                if (dto.getYear() == null) {
                    missingFields.add("Year");
                }
                if (dto.getNumberOfHouseholdsWith10m2Fence() == null) {
                    missingFields.add("Number of Households with 10m2 Fence");
                }
                if (dto.getAgbOf10m2LiveFence() == null) {
                    missingFields.add("AGB of 10m2 Live Fence");
                }
                if (dto.getAgbUnit() == null) {
                    missingFields.add("AGB Unit");
                }

                if (!missingFields.isEmpty()) {
                    throw new RuntimeException(String.format(
                            "Row %d: Missing required fields: %s. Please fill in all required fields in your Excel file.",
                            actualRowNumber, String.join(", ", missingFields)));
                }

                // Check if year already exists
                if (repository.findByYear(dto.getYear()).isPresent()) {
                    skippedYears.add(dto.getYear());
                    continue; // Skip this row
                }

                // Create the record
                GreenFencesMitigation saved = createGreenFencesMitigation(dto);
                savedRecords.add(saved);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("savedCount", savedRecords.size());
            result.put("skippedCount", skippedYears.size());
            result.put("skippedYears", skippedYears);
            result.put("totalProcessed", totalProcessed);
            return result;

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
