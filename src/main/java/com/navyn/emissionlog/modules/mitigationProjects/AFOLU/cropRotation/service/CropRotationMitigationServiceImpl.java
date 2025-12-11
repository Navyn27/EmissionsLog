package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.service;

import com.navyn.emissionlog.Enums.ExcelType;
import com.navyn.emissionlog.Enums.Metrics.AreaUnits;
import com.navyn.emissionlog.Enums.Metrics.BiomassDensityUnit;
import com.navyn.emissionlog.Enums.Mitigation.CropRotationConstants;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.dtos.CropRotationMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.models.CropRotationMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.repositories.CropRotationMitigationRepository;
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
public class CropRotationMitigationServiceImpl implements CropRotationMitigationService {

    private final CropRotationMitigationRepository repository;

    @Override
    public CropRotationMitigation createCropRotationMitigation(CropRotationMitigationDto dto) {
        CropRotationMitigation mitigation = new CropRotationMitigation();

        // Convert units to standard values
        double croplandInHectares = dto.getCroplandAreaUnit().toHectares(dto.getCroplandUnderCropRotation());
        double abgInTonnesDMPerHA = dto.getAbovegroundBiomassUnit().toTonnesDMPerHA(dto.getAbovegroundBiomass());
        double increasedBiomassInTonnesDMPerHA = dto.getIncreasedBiomassUnit()
                .toTonnesDMPerHA(dto.getIncreasedBiomass());

        // Map input fields (store in standard units)
        mitigation.setYear(dto.getYear());
        mitigation.setCroplandUnderCropRotation(croplandInHectares);
        mitigation.setAbovegroundBiomass(abgInTonnesDMPerHA);
        mitigation.setIncreasedBiomass(increasedBiomassInTonnesDMPerHA);

        // 1. Calculate Total Increased Biomass (tonnes DM/year)
        // Total increased biomass = Cropland × ABG × Increased biomass × (1 + Ratio BGB
        // to AGB)
        double totalIncreasedBiomass = croplandInHectares *
                abgInTonnesDMPerHA *
                increasedBiomassInTonnesDMPerHA *
                (1 + CropRotationConstants.RATIO_BGB_TO_AGB.getValue());
        mitigation.setTotalIncreasedBiomass(totalIncreasedBiomass);

        // 2. Calculate Biomass Carbon (tonnes C/year)
        // Biomass carbon = Total increased biomass × Carbon content in dry biomass
        double biomassCarbonIncrease = totalIncreasedBiomass *
                CropRotationConstants.CARBON_CONTENT_DRY_BIOMASS.getValue();
        mitigation.setBiomassCarbonIncrease(biomassCarbonIncrease);

        // 3. Calculate Mitigated Emissions (Kt CO2e)
        // Mitigated emissions = Biomass carbon × Conversion C to CO2 / 1000
        double mitigatedEmissions = (biomassCarbonIncrease *
                CropRotationConstants.CONVERSION_C_TO_CO2.getValue()) / 1000.0;
        mitigation.setMitigatedEmissionsKtCO2e(mitigatedEmissions);

        return repository.save(mitigation);
    }

    @Override
    public CropRotationMitigation updateCropRotationMitigation(UUID id, CropRotationMitigationDto dto) {
        CropRotationMitigation mitigation = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Crop Rotation Mitigation record not found with id: " + id));

        // Convert units to standard values
        double croplandInHectares = dto.getCroplandAreaUnit().toHectares(dto.getCroplandUnderCropRotation());
        double abgInTonnesDMPerHA = dto.getAbovegroundBiomassUnit().toTonnesDMPerHA(dto.getAbovegroundBiomass());
        double increasedBiomassInTonnesDMPerHA = dto.getIncreasedBiomassUnit()
                .toTonnesDMPerHA(dto.getIncreasedBiomass());

        // Update input fields (store in standard units)
        mitigation.setYear(dto.getYear());
        mitigation.setCroplandUnderCropRotation(croplandInHectares);
        mitigation.setAbovegroundBiomass(abgInTonnesDMPerHA);
        mitigation.setIncreasedBiomass(increasedBiomassInTonnesDMPerHA);

        // Recalculate derived fields
        double totalIncreasedBiomass = croplandInHectares *
                abgInTonnesDMPerHA *
                increasedBiomassInTonnesDMPerHA *
                (1 + CropRotationConstants.RATIO_BGB_TO_AGB.getValue());
        mitigation.setTotalIncreasedBiomass(totalIncreasedBiomass);

        double biomassCarbonIncrease = totalIncreasedBiomass *
                CropRotationConstants.CARBON_CONTENT_DRY_BIOMASS.getValue();
        mitigation.setBiomassCarbonIncrease(biomassCarbonIncrease);

        double mitigatedEmissions = (biomassCarbonIncrease *
                CropRotationConstants.CONVERSION_C_TO_CO2.getValue()) / 1000.0;
        mitigation.setMitigatedEmissionsKtCO2e(mitigatedEmissions);

        return repository.save(mitigation);
    }

    @Override
    public void deleteCropRotationMitigation(UUID id) {
        CropRotationMitigation mitigation = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Crop Rotation Mitigation record not found with id: " + id));
        repository.delete(mitigation);
    }

    @Override
    public List<CropRotationMitigation> getAllCropRotationMitigation(Integer year) {
        Specification<CropRotationMitigation> spec = Specification
                .<CropRotationMitigation>where(MitigationSpecifications.hasYear(year));
        return repository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
    }

    @Override
    public Optional<CropRotationMitigation> getByYear(Integer year) {
        return repository.findByYear(year);
    }

    @Override
    public byte[] generateExcelTemplate() {
        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Crop Rotation Mitigation");

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
            titleCell.setCellValue("Crop Rotation Mitigation Template");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

            rowIdx++; // Blank row

            // Create header row
            Row headerRow = sheet.createRow(rowIdx++);
            headerRow.setHeightInPoints(22);
            String[] headers = {
                    "Year",
                    "Cropland Under Crop Rotation",
                    "Cropland Area Unit",
                    "Aboveground Biomass",
                    "Aboveground Biomass Unit",
                    "Increased Biomass",
                    "Increased Biomass Unit"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Get enum values for dropdowns
            String[] areaUnitValues = Arrays.stream(AreaUnits.values())
                    .map(Enum::name)
                    .toArray(String[]::new);
            String[] biomassDensityUnitValues = Arrays.stream(BiomassDensityUnit.values())
                    .map(Enum::name)
                    .toArray(String[]::new);

            // Create data validation helper
            DataValidationHelper validationHelper = sheet.getDataValidationHelper();

            // Data validation for Cropland Area Unit column (Column C, index 2)
            CellRangeAddressList areaUnitList = new CellRangeAddressList(3, 1000, 2, 2);
            DataValidationConstraint areaUnitConstraint = validationHelper.createExplicitListConstraint(areaUnitValues);
            DataValidation areaUnitValidation = validationHelper.createValidation(areaUnitConstraint, areaUnitList);
            areaUnitValidation.setShowErrorBox(true);
            areaUnitValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
            areaUnitValidation.createErrorBox("Invalid Area Unit",
                    "Please select a valid area unit from the dropdown list.");
            areaUnitValidation.setShowPromptBox(true);
            areaUnitValidation.createPromptBox("Area Unit", "Select an area unit from the dropdown list.");
            sheet.addValidationData(areaUnitValidation);

            // Data validation for Aboveground Biomass Unit column (Column E, index 4)
            CellRangeAddressList abgUnitList = new CellRangeAddressList(3, 1000, 4, 4);
            DataValidationConstraint abgUnitConstraint = validationHelper
                    .createExplicitListConstraint(biomassDensityUnitValues);
            DataValidation abgUnitValidation = validationHelper.createValidation(abgUnitConstraint, abgUnitList);
            abgUnitValidation.setShowErrorBox(true);
            abgUnitValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
            abgUnitValidation.createErrorBox("Invalid Biomass Unit",
                    "Please select a valid biomass unit from the dropdown list.");
            abgUnitValidation.setShowPromptBox(true);
            abgUnitValidation.createPromptBox("Biomass Unit", "Select a biomass unit from the dropdown list.");
            sheet.addValidationData(abgUnitValidation);

            // Data validation for Increased Biomass Unit column (Column G, index 6)
            CellRangeAddressList increasedBiomassUnitList = new CellRangeAddressList(3, 1000, 6, 6);
            DataValidationConstraint increasedBiomassUnitConstraint = validationHelper
                    .createExplicitListConstraint(biomassDensityUnitValues);
            DataValidation increasedBiomassUnitValidation = validationHelper
                    .createValidation(increasedBiomassUnitConstraint, increasedBiomassUnitList);
            increasedBiomassUnitValidation.setShowErrorBox(true);
            increasedBiomassUnitValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
            increasedBiomassUnitValidation.createErrorBox("Invalid Biomass Unit",
                    "Please select a valid biomass unit from the dropdown list.");
            increasedBiomassUnitValidation.setShowPromptBox(true);
            increasedBiomassUnitValidation.createPromptBox("Biomass Unit",
                    "Select a biomass unit from the dropdown list.");
            sheet.addValidationData(increasedBiomassUnitValidation);

            // Create example data rows
            Object[] exampleData1 = {
                    2024,
                    100.5,
                    "HECTARES",
                    25.3,
                    "TONNES_DM_PER_HA",
                    5.5,
                    "TONNES_DM_PER_HA"
            };

            Object[] exampleData2 = {
                    2025,
                    150.75,
                    "ACRES",
                    30.5,
                    "TONNES_DM_PER_HA",
                    6.2,
                    "TONNES_DM_PER_HA"
            };

            // First example row
            Row exampleRow1 = sheet.createRow(rowIdx++);
            exampleRow1.setHeightInPoints(18);
            for (int i = 0; i < exampleData1.length; i++) {
                Cell cell = exampleRow1.createCell(i);
                if (i == 0) { // Year
                    cell.setCellStyle(yearStyle);
                    cell.setCellValue(((Number) exampleData1[i]).intValue());
                } else if (i == 1 || i == 3 || i == 5) { // Numbers
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
                } else if (i == 1 || i == 3 || i == 5) { // Numbers
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
    public Map<String, Object> createCropRotationMitigationFromExcel(MultipartFile file) {
        List<CropRotationMitigation> savedRecords = new ArrayList<>();
        List<Integer> skippedYears = new ArrayList<>();
        int totalProcessed = 0;

        try {
            List<CropRotationMitigationDto> dtos = ExcelReader.readExcel(
                    file.getInputStream(),
                    CropRotationMitigationDto.class,
                    ExcelType.CROP_ROTATION_MITIGATION);

            for (CropRotationMitigationDto dto : dtos) {
                totalProcessed++;

                // Check if year already exists
                if (repository.findByYear(dto.getYear()).isPresent()) {
                    skippedYears.add(dto.getYear());
                    continue; // Skip this row
                }

                // Create the record
                CropRotationMitigation saved = createCropRotationMitigation(dto);
                savedRecords.add(saved);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("saved", savedRecords);
            result.put("savedCount", savedRecords.size());
            result.put("skippedCount", skippedYears.size());
            result.put("skippedYears", skippedYears);
            result.put("totalProcessed", totalProcessed);

            return result;
        } catch (Exception e) {
            throw new RuntimeException("Error reading Crop Rotation Mitigation from Excel file: " + e.getMessage(), e);
        }
    }
}
