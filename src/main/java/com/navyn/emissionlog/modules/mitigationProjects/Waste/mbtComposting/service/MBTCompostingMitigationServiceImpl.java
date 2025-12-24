package com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.service;

import com.navyn.emissionlog.Enums.ExcelType;
import com.navyn.emissionlog.Enums.Metrics.EmissionsKilotonneUnit;
import com.navyn.emissionlog.Enums.Metrics.MassPerTimeUnit;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.constants.MBTCompostingConstants;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.constants.OperationStatus;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.dtos.MBTCompostingMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.models.MBTCompostingMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.repository.MBTCompostingMitigationRepository;
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
public class MBTCompostingMitigationServiceImpl implements MBTCompostingMitigationService {
    
    private final MBTCompostingMitigationRepository repository;
    
    @Override
    public MBTCompostingMitigation createMBTCompostingMitigation(MBTCompostingMitigationDto dto) {
        // Validate operation status precedence
        validateOperationStatusPrecedence(dto.getOperationStatus(), null);

        MBTCompostingMitigation mitigation = new MBTCompostingMitigation();
        
        // Convert to standard units
        double organicWasteInTonnesPerDay = dto.getOrganicWasteTreatedUnit().toTonnesPerDay(dto.getOrganicWasteTreatedTonsPerDay());
        double bauEmissionInKilotonnes = dto.getBauEmissionUnit().toKilotonnesCO2e(dto.getBauEmissionBiologicalTreatment());
        
        // Set user inputs (store in standard units)
        mitigation.setYear(dto.getYear());
        mitigation.setOperationStatus(dto.getOperationStatus());
        mitigation.setOrganicWasteTreatedTonsPerDay(organicWasteInTonnesPerDay);
        mitigation.setBauEmissionBiologicalTreatment(bauEmissionInKilotonnes);
        
        // Calculations
        // Organic Waste Treated (tons/year) = Organic Waste Treated (tons/day) × days based on operation status
        // - PRE_OPERATION: 0 days
        // - HALF_OPERATION: 182.5 days (365/2)
        // - FULL_OPERATION: 365 days
        Double daysPerYear = dto.getOperationStatus().getDaysPerYear();
        Double organicWasteTreatedTonsPerYear = organicWasteInTonnesPerDay * daysPerYear;
        mitigation.setOrganicWasteTreatedTonsPerYear(organicWasteTreatedTonsPerYear);
        
        // Estimated GHG Reduction (tCO2eq/year) = Emission Factor * Organic Waste Treated (tons/year)
        Double estimatedGhgReductionTonnes = MBTCompostingConstants.EMISSION_FACTOR.getValue() * organicWasteTreatedTonsPerYear;
        mitigation.setEstimatedGhgReductionTonnesPerYear(estimatedGhgReductionTonnes);
        
        // Convert to kilotonnes
        Double estimatedGhgReductionKilotonnes = estimatedGhgReductionTonnes / 1000;
        mitigation.setEstimatedGhgReductionKilotonnesPerYear(estimatedGhgReductionKilotonnes);
        
        // Adjusted BAU Emission Biological Treatment (ktCO2eq/year) = BAU Emission - GHG Reduction (kt)
        Double adjustedBauEmission = bauEmissionInKilotonnes - estimatedGhgReductionKilotonnes;
        mitigation.setAdjustedBauEmissionBiologicalTreatment(adjustedBauEmission);
        
        return repository.save(mitigation);
    }
    
    @Override
    public MBTCompostingMitigation updateMBTCompostingMitigation(UUID id, MBTCompostingMitigationDto dto) {
        MBTCompostingMitigation mitigation = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("MBT Composting Mitigation record not found with id: " + id));
        
        // Validate operation status precedence (exclude current record from validation)
        validateOperationStatusPrecedence(dto.getOperationStatus(), id);

        // Convert to standard units
        double organicWasteInTonnesPerDay = dto.getOrganicWasteTreatedUnit().toTonnesPerDay(dto.getOrganicWasteTreatedTonsPerDay());
        double bauEmissionInKilotonnes = dto.getBauEmissionUnit().toKilotonnesCO2e(dto.getBauEmissionBiologicalTreatment());
        
        // Update user inputs (store in standard units)
        mitigation.setYear(dto.getYear());
        mitigation.setOperationStatus(dto.getOperationStatus());
        mitigation.setOrganicWasteTreatedTonsPerDay(organicWasteInTonnesPerDay);
        mitigation.setBauEmissionBiologicalTreatment(bauEmissionInKilotonnes);
        
        // Recalculate derived fields
        Double daysPerYear = dto.getOperationStatus().getDaysPerYear();
        Double organicWasteTreatedTonsPerYear = organicWasteInTonnesPerDay * daysPerYear;
        mitigation.setOrganicWasteTreatedTonsPerYear(organicWasteTreatedTonsPerYear);
        
        Double estimatedGhgReductionTonnes = MBTCompostingConstants.EMISSION_FACTOR.getValue() * organicWasteTreatedTonsPerYear;
        mitigation.setEstimatedGhgReductionTonnesPerYear(estimatedGhgReductionTonnes);
        
        Double estimatedGhgReductionKilotonnes = estimatedGhgReductionTonnes / 1000;
        mitigation.setEstimatedGhgReductionKilotonnesPerYear(estimatedGhgReductionKilotonnes);
        
        Double adjustedBauEmission = bauEmissionInKilotonnes - estimatedGhgReductionKilotonnes;
        mitigation.setAdjustedBauEmissionBiologicalTreatment(adjustedBauEmission);
        
        return repository.save(mitigation);
    }
    
    @Override
    public List<MBTCompostingMitigation> getAllMBTCompostingMitigation(Integer year) {
        Specification<MBTCompostingMitigation> spec = Specification.where(hasYear(year));
        return repository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
    }

    @Override
    public void deleteMBTCompostingMitigation(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("MBT Composting Mitigation record not found with id: " + id);
        }
        repository.deleteById(id);
    }

    /**
     * Validates that the new operation status is not smaller than the maximum existing status.
     * Operation status must progress in ascending order: CONSTRUCTION_PRE_OP < HALF_YEAR_OPERATION < FULL_OPERATION
     *
     * @param newStatus The operation status to validate
     * @param excludeId ID to exclude from validation (for updates)
     * @throws RuntimeException if operation status precedence is violated
     */
    private void validateOperationStatusPrecedence(OperationStatus newStatus, UUID excludeId) {
        // Get the maximum operation status from existing records
        repository.findTopByOrderByOperationStatusDesc()
            .ifPresent(maxRecord -> {
                // Exclude the current record being updated
                if (excludeId != null && maxRecord.getId().equals(excludeId)) {
                    return;
                }

                OperationStatus maxStatus = maxRecord.getOperationStatus();

                // Check if new status is smaller than max status
                if (newStatus.ordinal() < maxStatus.ordinal()) {
                    throw new RuntimeException(
                        String.format("Cannot set operation status to %s. The project has already reached %s. " +
                                     "Operation status cannot go backward.",
                                     newStatus.getDisplayName(), maxStatus.getDisplayName())
                    );
                }
            });
    }

    @Override
    public byte[] generateExcelTemplate() {
        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("MBT Composting Mitigation");

            // Create styles (similar to other templates)
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

            XSSFCellStyle yearStyle = (XSSFCellStyle) workbook.createCellStyle();
            yearStyle.cloneStyleFrom(dataStyle);
            yearStyle.setAlignment(HorizontalAlignment.CENTER);

            int rowIdx = 0;

            // Title row
            Row titleRow = sheet.createRow(rowIdx++);
            titleRow.setHeightInPoints(30);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("MBT Composting Mitigation Template");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));

            rowIdx++; // Blank row

            // Create header row
            Row headerRow = sheet.createRow(rowIdx++);
            headerRow.setHeightInPoints(22);
            String[] headers = {
                    "Year",
                    "Operation Status",
                    "Organic Waste Treated Tons Per Day",
                    "Organic Waste Treated Unit",
                    "BAU Emission Biological Treatment",
                    "BAU Emission Unit"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Get enum values for dropdowns
            String[] operationStatusValues = Arrays.stream(OperationStatus.values())
                    .map(Enum::name)
                    .toArray(String[]::new);
            String[] massPerTimeUnitValues = Arrays.stream(MassPerTimeUnit.values())
                    .map(Enum::name)
                    .toArray(String[]::new);
            String[] emissionsUnitValues = Arrays.stream(EmissionsKilotonneUnit.values())
                    .map(Enum::name)
                    .toArray(String[]::new);

            // Create data validation helper
            DataValidationHelper validationHelper = sheet.getDataValidationHelper();

            // Data validation for Operation Status column (Column B, index 1)
            CellRangeAddressList operationStatusList = new CellRangeAddressList(3, 1000, 1, 1);
            DataValidationConstraint operationStatusConstraint = validationHelper
                    .createExplicitListConstraint(operationStatusValues);
            DataValidation operationStatusValidation = validationHelper.createValidation(operationStatusConstraint,
                    operationStatusList);
            operationStatusValidation.setShowErrorBox(true);
            operationStatusValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
            operationStatusValidation.createErrorBox("Invalid Operation Status",
                    "Please select a valid operation status from the dropdown list.");
            operationStatusValidation.setShowPromptBox(true);
            operationStatusValidation.createPromptBox("Operation Status", "Select an operation status from the dropdown list.");
            sheet.addValidationData(operationStatusValidation);

            // Data validation for Organic Waste Treated Unit column (Column D, index 3)
            CellRangeAddressList massUnitList = new CellRangeAddressList(3, 1000, 3, 3);
            DataValidationConstraint massUnitConstraint = validationHelper
                    .createExplicitListConstraint(massPerTimeUnitValues);
            DataValidation massUnitValidation = validationHelper.createValidation(massUnitConstraint,
                    massUnitList);
            massUnitValidation.setShowErrorBox(true);
            massUnitValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
            massUnitValidation.createErrorBox("Invalid Unit",
                    "Please select a valid unit from the dropdown list.");
            massUnitValidation.setShowPromptBox(true);
            massUnitValidation.createPromptBox("Organic Waste Treated Unit", "Select a unit from the dropdown list.");
            sheet.addValidationData(massUnitValidation);

            // Data validation for BAU Emission Unit column (Column F, index 5)
            CellRangeAddressList bauUnitList = new CellRangeAddressList(3, 1000, 5, 5);
            DataValidationConstraint bauUnitConstraint = validationHelper
                    .createExplicitListConstraint(emissionsUnitValues);
            DataValidation bauUnitValidation = validationHelper.createValidation(bauUnitConstraint,
                    bauUnitList);
            bauUnitValidation.setShowErrorBox(true);
            bauUnitValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
            bauUnitValidation.createErrorBox("Invalid Unit",
                    "Please select a valid unit from the dropdown list.");
            bauUnitValidation.setShowPromptBox(true);
            bauUnitValidation.createPromptBox("BAU Emission Unit", "Select a unit from the dropdown list.");
            sheet.addValidationData(bauUnitValidation);

            // Create example data rows
            Object[] exampleData1 = {
                    2024,
                    "FULL_OPERATION",
                    50.0,
                    "TONNES_PER_DAY",
                    10.0,
                    "KILOTONNES_CO2E"
            };

            Object[] exampleData2 = {
                    2025,
                    "FULL_OPERATION",
                    55.0,
                    "TONNES_PER_DAY",
                    11.0,
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
                } else if (i == 2 || i == 4) { // Numbers
                    cell.setCellStyle(numberStyle);
                    cell.setCellValue(((Number) exampleData1[i]).doubleValue());
                } else { // Strings
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
                } else if (i == 2 || i == 4) { // Numbers
                    CellStyle altNumStyle = workbook.createCellStyle();
                    altNumStyle.cloneStyleFrom(numberStyle);
                    altNumStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                    altNumStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    cell.setCellStyle(altNumStyle);
                    cell.setCellValue(((Number) exampleData2[i]).doubleValue());
                } else { // Strings
                    cell.setCellStyle(alternateDataStyle);
                    cell.setCellValue((String) exampleData2[i]);
                }
            }

            // Auto-size columns
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
    public Map<String, Object> createMBTCompostingMitigationFromExcel(MultipartFile file) {
        List<MBTCompostingMitigation> savedRecords = new ArrayList<>();
        List<Integer> skippedYears = new ArrayList<>();
        int totalProcessed = 0;

        try {
            List<MBTCompostingMitigationDto> dtos = ExcelReader.readExcel(
                    file.getInputStream(),
                    MBTCompostingMitigationDto.class,
                    ExcelType.MBT_COMPOSTING_MITIGATION);

            for (int i = 0; i < dtos.size(); i++) {
                MBTCompostingMitigationDto dto = dtos.get(i);
                totalProcessed++;
                int actualRowNumber = i + 1 + 3;

                // Validate required fields
                List<String> missingFields = new ArrayList<>();
                if (dto.getYear() == null) {
                    missingFields.add("Year");
                }
                if (dto.getOperationStatus() == null) {
                    missingFields.add("Operation Status");
                }
                if (dto.getOrganicWasteTreatedTonsPerDay() == null) {
                    missingFields.add("Organic Waste Treated Tons Per Day");
                }
                if (dto.getOrganicWasteTreatedUnit() == null) {
                    missingFields.add("Organic Waste Treated Unit");
                }
                if (dto.getBauEmissionBiologicalTreatment() == null) {
                    missingFields.add("BAU Emission Biological Treatment");
                }
                if (dto.getBauEmissionUnit() == null) {
                    missingFields.add("BAU Emission Unit");
                }

                if (!missingFields.isEmpty()) {
                    throw new RuntimeException(String.format(
                            "Row %d: Missing required fields: %s. Please fill in all required fields in your Excel file.",
                            actualRowNumber, String.join(", ", missingFields)));
                }

                // Check if year already exists
                if (repository.findByYear(dto.getYear()).isPresent()) {
                    skippedYears.add(dto.getYear());
                    continue;
                }

                // Create the record
                MBTCompostingMitigation saved = createMBTCompostingMitigation(dto);
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
