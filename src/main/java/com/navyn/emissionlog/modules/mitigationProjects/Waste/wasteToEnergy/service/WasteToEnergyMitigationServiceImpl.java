package com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.service;

import com.navyn.emissionlog.Enums.ExcelType;
import com.navyn.emissionlog.Enums.Metrics.EmissionsKilotonneUnit;
import com.navyn.emissionlog.Enums.Metrics.MassPerYearUnit;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.constants.WasteToEnergyConstants;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.dtos.WasteToEnergyMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.models.WasteToEnergyMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.repository.WasteToEnergyMitigationRepository;
import com.navyn.emissionlog.utils.ExcelReader;
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
import java.util.UUID;

import static com.navyn.emissionlog.utils.Specifications.MitigationSpecifications.hasYear;

@Service
@RequiredArgsConstructor
public class WasteToEnergyMitigationServiceImpl implements WasteToEnergyMitigationService {
    
    private final WasteToEnergyMitigationRepository repository;
    
    @Override
    public WasteToEnergyMitigation createWasteToEnergyMitigation(WasteToEnergyMitigationDto dto) {
        WasteToEnergyMitigation mitigation = new WasteToEnergyMitigation();
        
        // Convert to standard units
        double wasteInTonnesPerYear = dto.getWasteToWtEUnit().toTonnesPerYear(dto.getWasteToWtE());
        double bauEmissionsInKilotonnes = dto.getBauEmissionsUnit().toKilotonnesCO2e(dto.getBauEmissionsSolidWaste());
        
        // Set user inputs (store in standard units)
        mitigation.setYear(dto.getYear());
        mitigation.setWasteToWtE(wasteInTonnesPerYear);
        mitigation.setBauEmissionsSolidWaste(bauEmissionsInKilotonnes);
        
        // Calculations
        // GHG Reduction (tCO2eq) = Net Emission Factor (tCO2eq/t) * Waste to WtE (t/year)
        Double ghgReductionTonnes = WasteToEnergyConstants.NET_EMISSION_FACTOR.getValue() * wasteInTonnesPerYear;
        mitigation.setGhgReductionTonnes(ghgReductionTonnes);
        
        // GHG Reduction (KtCO2eq) = GHG Reduction (tCO2eq) / 1000
        Double ghgReductionKilotonnes = ghgReductionTonnes / 1000;
        mitigation.setGhgReductionKilotonnes(ghgReductionKilotonnes);
        
        // Adjusted Emissions (with WtE, ktCO₂e) = BAU Emissions (Solid Waste, ktCO₂e) - GHG Reduction (KtCO2eq)
        Double adjustedEmissions = bauEmissionsInKilotonnes - ghgReductionKilotonnes;
        mitigation.setAdjustedEmissionsWithWtE(adjustedEmissions);
        
        return repository.save(mitigation);
    }
    
    @Override
    public WasteToEnergyMitigation updateWasteToEnergyMitigation(UUID id, WasteToEnergyMitigationDto dto) {
        WasteToEnergyMitigation mitigation = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Waste to Energy Mitigation record not found with id: " + id));
        
        // Convert to standard units
        double wasteInTonnesPerYear = dto.getWasteToWtEUnit().toTonnesPerYear(dto.getWasteToWtE());
        double bauEmissionsInKilotonnes = dto.getBauEmissionsUnit().toKilotonnesCO2e(dto.getBauEmissionsSolidWaste());
        
        // Update user inputs (store in standard units)
        mitigation.setYear(dto.getYear());
        mitigation.setWasteToWtE(wasteInTonnesPerYear);
        mitigation.setBauEmissionsSolidWaste(bauEmissionsInKilotonnes);
        
        // Recalculate derived fields
        Double ghgReductionTonnes = WasteToEnergyConstants.NET_EMISSION_FACTOR.getValue() * wasteInTonnesPerYear;
        mitigation.setGhgReductionTonnes(ghgReductionTonnes);
        
        Double ghgReductionKilotonnes = ghgReductionTonnes / 1000;
        mitigation.setGhgReductionKilotonnes(ghgReductionKilotonnes);
        
        Double adjustedEmissions = bauEmissionsInKilotonnes - ghgReductionKilotonnes;
        mitigation.setAdjustedEmissionsWithWtE(adjustedEmissions);
        
        return repository.save(mitigation);
    }
    
    @Override
    public void deleteWasteToEnergyMitigation(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Waste to Energy Mitigation record not found with id: " + id);
        }
        repository.deleteById(id);
    }
    
    @Override
    public List<WasteToEnergyMitigation> getAllWasteToEnergyMitigation(Integer year) {
        Specification<WasteToEnergyMitigation> spec = Specification.where(hasYear(year));
        return repository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
    }

    @Override
    public byte[] generateExcelTemplate() {
        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Waste to Energy Mitigation");

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
            titleCell.setCellValue("Waste to Energy Mitigation Template");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));

            rowIdx++; // Blank row

            // Create header row
            Row headerRow = sheet.createRow(rowIdx++);
            headerRow.setHeightInPoints(22);
            String[] headers = {
                    "Year",
                    "Waste to WtE",
                    "Waste to WtE Unit",
                    "BAU Emissions Solid Waste",
                    "BAU Emissions Unit"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Get enum values for dropdowns
            String[] wasteToWtEUnitValues = Arrays.stream(MassPerYearUnit.values())
                    .map(Enum::name)
                    .toArray(String[]::new);
            String[] bauEmissionsUnitValues = Arrays.stream(EmissionsKilotonneUnit.values())
                    .map(Enum::name)
                    .toArray(String[]::new);

            // Create data validation helper
            DataValidationHelper validationHelper = sheet.getDataValidationHelper();

            // Data validation for Waste to WtE Unit column (Column C, index 2)
            CellRangeAddressList wasteUnitList = new CellRangeAddressList(3, 1000, 2, 2);
            DataValidationConstraint wasteUnitConstraint = validationHelper
                    .createExplicitListConstraint(wasteToWtEUnitValues);
            DataValidation wasteUnitValidation = validationHelper.createValidation(wasteUnitConstraint,
                    wasteUnitList);
            wasteUnitValidation.setShowErrorBox(true);
            wasteUnitValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
            wasteUnitValidation.createErrorBox("Invalid Waste to WtE Unit",
                    "Please select a valid unit from the dropdown list.");
            wasteUnitValidation.setShowPromptBox(true);
            wasteUnitValidation.createPromptBox("Waste to WtE Unit", "Select a unit from the dropdown list.");
            sheet.addValidationData(wasteUnitValidation);

            // Data validation for BAU Emissions Unit column (Column E, index 4)
            CellRangeAddressList bauUnitList = new CellRangeAddressList(3, 1000, 4, 4);
            DataValidationConstraint bauUnitConstraint = validationHelper
                    .createExplicitListConstraint(bauEmissionsUnitValues);
            DataValidation bauUnitValidation = validationHelper.createValidation(bauUnitConstraint,
                    bauUnitList);
            bauUnitValidation.setShowErrorBox(true);
            bauUnitValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
            bauUnitValidation.createErrorBox("Invalid BAU Emissions Unit",
                    "Please select a valid unit from the dropdown list.");
            bauUnitValidation.setShowPromptBox(true);
            bauUnitValidation.createPromptBox("BAU Emissions Unit", "Select a unit from the dropdown list.");
            sheet.addValidationData(bauUnitValidation);

            // Create example data rows
            Object[] exampleData1 = {
                    2024,
                    1000.0,
                    "TONNES_PER_YEAR",
                    50.0,
                    "KILOTONNES_CO2E"
            };

            Object[] exampleData2 = {
                    2025,
                    1200.0,
                    "TONNES_PER_YEAR",
                    55.0,
                    "KILOTONNES_CO2E"
            };

            // First example row
            Row exampleRow1 = sheet.createRow(rowIdx++);
            exampleRow1.setHeightInPoints(18);
            for (int i = 0; i < exampleData1.length; i++) {
                Cell cell = exampleRow1.createCell(i);
                if (i == 0) { // Year
                    cell.setCellStyle(yearStyle);
                    cell.setCellValue(((Number) exampleData1[i]).intValue());
                } else if (i == 1 || i == 3) { // Numbers
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
                } else if (i == 1 || i == 3) { // Numbers
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
    public Map<String, Object> createWasteToEnergyMitigationFromExcel(MultipartFile file) {
        List<WasteToEnergyMitigation> savedRecords = new ArrayList<>();
        List<Integer> skippedYears = new ArrayList<>();
        int totalProcessed = 0;

        try {
            List<WasteToEnergyMitigationDto> dtos = ExcelReader.readExcel(
                    file.getInputStream(),
                    WasteToEnergyMitigationDto.class,
                    ExcelType.WASTE_TO_ENERGY_MITIGATION);

            for (int i = 0; i < dtos.size(); i++) {
                WasteToEnergyMitigationDto dto = dtos.get(i);
                totalProcessed++;
                int actualRowNumber = i + 1 + 3; // +1 for 1-based, +3 for title(1) + blank(1) + header(1)

                // Validate required fields
                List<String> missingFields = new ArrayList<>();
                if (dto.getYear() == null) {
                    missingFields.add("Year");
                }
                if (dto.getWasteToWtE() == null) {
                    missingFields.add("Waste to WtE");
                }
                if (dto.getWasteToWtEUnit() == null) {
                    missingFields.add("Waste to WtE Unit");
                }
                if (dto.getBauEmissionsSolidWaste() == null) {
                    missingFields.add("BAU Emissions Solid Waste");
                }
                if (dto.getBauEmissionsUnit() == null) {
                    missingFields.add("BAU Emissions Unit");
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
                WasteToEnergyMitigation saved = createWasteToEnergyMitigation(dto);
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
