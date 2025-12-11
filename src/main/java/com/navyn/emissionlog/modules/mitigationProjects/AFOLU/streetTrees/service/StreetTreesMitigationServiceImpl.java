package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.streetTrees.service;

import com.navyn.emissionlog.Enums.ExcelType;
import com.navyn.emissionlog.Enums.Metrics.VolumeUnits;
import com.navyn.emissionlog.Enums.Mitigation.StreetTreesConstants;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.settlementTrees.models.SettlementTreesMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.streetTrees.dtos.StreetTreesMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.streetTrees.models.StreetTreesMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.streetTrees.repositories.StreetTreesMitigationRepository;
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
public class StreetTreesMitigationServiceImpl implements StreetTreesMitigationService {

    private final StreetTreesMitigationRepository repository;

    private static Double apply(StreetTreesMitigation streetTreesMitigation) {
        return streetTreesMitigation.getNumberOfTreesPlanted() + streetTreesMitigation.getCumulativeNumberOfTrees();
    }

    @Override
    public StreetTreesMitigation createStreetTreesMitigation(StreetTreesMitigationDto dto) {
        StreetTreesMitigation mitigation = new StreetTreesMitigation();

        Optional<StreetTreesMitigation> lastYearRecord = repository.findTopByYearLessThanOrderByYearDesc(dto.getYear());
        Double cumulativeNumberOfTrees = lastYearRecord.map(StreetTreesMitigationServiceImpl::apply).orElse(0.0);
        Double agbSingleTreePrevYear = lastYearRecord.map(StreetTreesMitigation::getAgbSingleTreeCurrentYear)
                .orElse(0.0);

        // Convert AGB to cubic meters (standard unit)
        double agbCurrentYearInCubicMeters = dto.getAgbUnit().toCubicMeters(dto.getAgbSingleTreeCurrentYear());

        // Map input fields (store in standard units)
        mitigation.setYear(dto.getYear());
        mitigation.setCumulativeNumberOfTrees(cumulativeNumberOfTrees);
        mitigation.setNumberOfTreesPlanted(dto.getNumberOfTreesPlanted());
        mitigation.setAgbSingleTreePreviousYear(agbSingleTreePrevYear);
        mitigation.setAgbSingleTreeCurrentYear(agbCurrentYearInCubicMeters);

        // 1. Calculate AGB Growth (tonnes m3)
        double agbGrowth = agbCurrentYearInCubicMeters - agbSingleTreePrevYear;
        mitigation.setAgbGrowth(agbGrowth);

        // 2. Calculate Aboveground Biomass Growth (tonnes DM)
        double abovegroundBiomassGrowth = StreetTreesConstants.CONVERSION_M3_TO_TONNES_DM.getValue() *
                agbGrowth *
                cumulativeNumberOfTrees;
        mitigation.setAbovegroundBiomassGrowth(abovegroundBiomassGrowth);

        // 3. Calculate Total Biomass (tonnes DM/year) - includes belowground
        double totalBiomass = abovegroundBiomassGrowth *
                (1 + StreetTreesConstants.RATIO_BGB_TO_AGB.getValue());
        mitigation.setTotalBiomass(totalBiomass);

        // 4. Calculate Biomass Carbon Increase (tonnes C/year)
        double biomassCarbonIncrease = totalBiomass *
                StreetTreesConstants.CARBON_CONTENT_DRY_WOOD.getValue();
        mitigation.setBiomassCarbonIncrease(biomassCarbonIncrease);

        // 5. Calculate Mitigated Emissions (Kt CO2e)
        double mitigatedEmissions = (biomassCarbonIncrease *
                StreetTreesConstants.CONVERSION_C_TO_CO2.getValue()) / 1000.0;
        mitigation.setMitigatedEmissionsKtCO2e(mitigatedEmissions);

        return repository.save(mitigation);
    }

    @Override
    public StreetTreesMitigation updateStreetTreesMitigation(UUID id, StreetTreesMitigationDto dto) {
        StreetTreesMitigation mitigation = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Street Trees Mitigation record not found with id: " + id));

        // Update the current record
        recalculateAndUpdateRecord(mitigation, dto);
        StreetTreesMitigation updatedRecord = repository.save(mitigation);

        // CASCADE: Find and recalculate all subsequent years
        List<StreetTreesMitigation> subsequentRecords = repository
                .findByYearGreaterThanOrderByYearAsc(dto.getYear());

        for (StreetTreesMitigation subsequent : subsequentRecords) {
            recalculateExistingRecord(subsequent);
            repository.save(subsequent);
        }

        return updatedRecord;
    }

    @Override
    public void deleteStreetTreesMitigation(UUID id) {
        StreetTreesMitigation mitigation = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Street Trees Mitigation record not found with id: " + id));

        Integer year = mitigation.getYear();
        repository.delete(mitigation);

        // Recalculate all subsequent years as cumulative fields depend on previous
        // records
        List<StreetTreesMitigation> subsequentRecords = repository.findByYearGreaterThanOrderByYearAsc(year);
        for (StreetTreesMitigation subsequent : subsequentRecords) {
            recalculateExistingRecord(subsequent);
            repository.save(subsequent);
        }
    }

    /**
     * Recalculates an existing record based on its current year and stored input
     * values
     */
    private void recalculateExistingRecord(StreetTreesMitigation mitigation) {
        Optional<StreetTreesMitigation> lastYearRecord = repository
                .findTopByYearLessThanOrderByYearDesc(mitigation.getYear());
        Double cumulativeNumberOfTrees = lastYearRecord.map(StreetTreesMitigationServiceImpl::apply).orElse(0.0);
        Double agbSingleTreePrevYear = lastYearRecord.map(StreetTreesMitigation::getAgbSingleTreeCurrentYear)
                .orElse(0.0);

        mitigation.setCumulativeNumberOfTrees(cumulativeNumberOfTrees);
        mitigation.setAgbSingleTreePreviousYear(agbSingleTreePrevYear);

        // Recalculate all derived fields using existing AGB value
        double agbCurrentYearInCubicMeters = mitigation.getAgbSingleTreeCurrentYear();

        double agbGrowth = agbCurrentYearInCubicMeters - agbSingleTreePrevYear;
        mitigation.setAgbGrowth(agbGrowth);

        double abovegroundBiomassGrowth = StreetTreesConstants.CONVERSION_M3_TO_TONNES_DM.getValue() *
                agbGrowth *
                cumulativeNumberOfTrees;
        mitigation.setAbovegroundBiomassGrowth(abovegroundBiomassGrowth);

        double totalBiomass = abovegroundBiomassGrowth *
                (1 + StreetTreesConstants.RATIO_BGB_TO_AGB.getValue());
        mitigation.setTotalBiomass(totalBiomass);

        double biomassCarbonIncrease = totalBiomass *
                StreetTreesConstants.CARBON_CONTENT_DRY_WOOD.getValue();
        mitigation.setBiomassCarbonIncrease(biomassCarbonIncrease);

        double mitigatedEmissions = (biomassCarbonIncrease *
                StreetTreesConstants.CONVERSION_C_TO_CO2.getValue()) / 1000.0;
        mitigation.setMitigatedEmissionsKtCO2e(mitigatedEmissions);
    }

    /**
     * Recalculates a record with new DTO values
     */
    private void recalculateAndUpdateRecord(StreetTreesMitigation mitigation, StreetTreesMitigationDto dto) {
        Optional<StreetTreesMitigation> lastYearRecord = repository.findTopByYearLessThanOrderByYearDesc(dto.getYear());
        Double cumulativeNumberOfTrees = lastYearRecord.map(StreetTreesMitigationServiceImpl::apply).orElse(0.0);
        Double agbSingleTreePrevYear = lastYearRecord.map(StreetTreesMitigation::getAgbSingleTreeCurrentYear)
                .orElse(0.0);

        // Convert AGB to cubic meters (standard unit)
        double agbCurrentYearInCubicMeters = dto.getAgbUnit().toCubicMeters(dto.getAgbSingleTreeCurrentYear());

        // Update input fields (store in standard units)
        mitigation.setYear(dto.getYear());
        mitigation.setCumulativeNumberOfTrees(cumulativeNumberOfTrees);
        mitigation.setNumberOfTreesPlanted(dto.getNumberOfTreesPlanted());
        mitigation.setAgbSingleTreePreviousYear(agbSingleTreePrevYear);
        mitigation.setAgbSingleTreeCurrentYear(agbCurrentYearInCubicMeters);

        // Recalculate derived fields
        double agbGrowth = agbCurrentYearInCubicMeters - agbSingleTreePrevYear;
        mitigation.setAgbGrowth(agbGrowth);

        double abovegroundBiomassGrowth = StreetTreesConstants.CONVERSION_M3_TO_TONNES_DM.getValue() *
                agbGrowth *
                cumulativeNumberOfTrees;
        mitigation.setAbovegroundBiomassGrowth(abovegroundBiomassGrowth);

        double totalBiomass = abovegroundBiomassGrowth *
                (1 + StreetTreesConstants.RATIO_BGB_TO_AGB.getValue());
        mitigation.setTotalBiomass(totalBiomass);

        double biomassCarbonIncrease = totalBiomass *
                StreetTreesConstants.CARBON_CONTENT_DRY_WOOD.getValue();
        mitigation.setBiomassCarbonIncrease(biomassCarbonIncrease);

        double mitigatedEmissions = (biomassCarbonIncrease *
                StreetTreesConstants.CONVERSION_C_TO_CO2.getValue()) / 1000.0;
        mitigation.setMitigatedEmissionsKtCO2e(mitigatedEmissions);
    }

    @Override
    public List<StreetTreesMitigation> getAllStreetTreesMitigation(Integer year) {
        Specification<StreetTreesMitigation> spec = Specification
                .<StreetTreesMitigation>where(MitigationSpecifications.hasYear(year));
        return repository.findAll(spec, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    @Override
    public Optional<StreetTreesMitigation> getByYear(Integer year) {
        return repository.findByYear(year);
    }

    @Override
    public byte[] generateExcelTemplate() {
        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Street Trees Mitigation");

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
            titleCell.setCellValue("Street Trees Mitigation Template");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

            rowIdx++; // Blank row

            // Create header row
            Row headerRow = sheet.createRow(rowIdx++);
            headerRow.setHeightInPoints(22);
            String[] headers = {
                    "Year",
                    "Number of Trees Planted",
                    "AGB Single Tree Current Year",
                    "AGB Unit"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Get enum values for dropdowns
            String[] agbUnitValues = Arrays.stream(VolumeUnits.values())
                    .map(Enum::name)
                    .toArray(String[]::new);

            // Create data validation helper
            DataValidationHelper validationHelper = sheet.getDataValidationHelper();

            // Data validation for AGB Unit column (Column D, index 3)
            CellRangeAddressList agbUnitList = new CellRangeAddressList(3, 1000, 3, 3);
            DataValidationConstraint agbUnitConstraint = validationHelper
                    .createExplicitListConstraint(agbUnitValues);
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
                    1000.0,
                    0.5,
                    "CUBIC_METER"
            };

            Object[] exampleData2 = {
                    2025,
                    1500.0,
                    0.75,
                    "CUBIC_METER"
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

            // Auto-size columns with wider limits (bigger columns)
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                int currentWidth = sheet.getColumnWidth(i);
                int minWidth = 5000; // Increased from 3000 - wider minimum
                int maxWidth = 30000; // Increased from 20000 - wider maximum
                if (currentWidth < minWidth) {
                    sheet.setColumnWidth(i, minWidth);
                } else if (currentWidth > maxWidth) {
                    sheet.setColumnWidth(i, maxWidth);
                } else {
                    // Make columns even wider - add 30% more width
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
    public Map<String, Object> createStreetTreesMitigationFromExcel(MultipartFile file) {
        List<StreetTreesMitigation> savedRecords = new ArrayList<>();
        List<Integer> skippedYears = new ArrayList<>();
        int totalProcessed = 0;

        try {
            List<StreetTreesMitigationDto> dtos = ExcelReader.readExcel(
                    file.getInputStream(),
                    StreetTreesMitigationDto.class,
                    ExcelType.STREET_TREES_MITIGATION);

            for (int i = 0; i < dtos.size(); i++) {
                StreetTreesMitigationDto dto = dtos.get(i);
                totalProcessed++;
                int actualRowNumber = i + 1 + 3; // +1 for 1-based, +3 for title(1) + blank(1) + header(1)

                // Validate required fields
                List<String> missingFields = new ArrayList<>();
                if (dto.getYear() == null) {
                    missingFields.add("Year");
                }
                if (dto.getNumberOfTreesPlanted() == null) {
                    missingFields.add("Number of Trees Planted");
                }
                if (dto.getAgbSingleTreeCurrentYear() == null) {
                    missingFields.add("AGB Single Tree Current Year");
                }
                if (dto.getAgbUnit() == null) {
                    missingFields.add("AGB Unit");
                }

                if (!missingFields.isEmpty()) {
                    throw new RuntimeException(String.format(
                            "Missing required fields: %s. Please fill in all required fields in your Excel file.",
                            String.join(", ", missingFields)));
                }

                // Check if year already exists
                if (repository.findByYear(dto.getYear()).isPresent()) {
                    skippedYears.add(dto.getYear());
                    continue; // Skip this row
                }

                // Create the record
                StreetTreesMitigation saved = createStreetTreesMitigation(dto);
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
            if (message != null && message.contains("Template")) {
                throw new RuntimeException(message, e);
            } else {
                throw new RuntimeException("Template format error: " + (message != null ? message
                        : "The uploaded file does not match the required template format. Please download the template and ensure all columns and data are correct."),
                        e);
            }
        } catch (NullPointerException e) {
            // Handle null pointer exceptions with clear message
            String errorMsg = e.getMessage();
            if (errorMsg != null && errorMsg.contains("getAgbUnit")) {
                throw new RuntimeException(
                        "Template validation error: Missing required field 'AGB Unit'. Please ensure the AGB Unit column is filled with a valid value from the dropdown list.",
                        e);
            } else {
                throw new RuntimeException(
                        "Template validation error: One or more required fields are missing. Please ensure all required fields are filled in the Excel file.",
                        e);
            }
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if (errorMsg != null && (errorMsg.contains("Template") || errorMsg.contains("validation")
                    || errorMsg.contains("format") || errorMsg.contains("missing required field"))) {
                throw new RuntimeException(errorMsg, e);
            }
            throw new RuntimeException("Error processing Excel file: "
                    + (errorMsg != null ? errorMsg : "Please ensure the file matches the template format exactly."), e);
        }
    }
}
