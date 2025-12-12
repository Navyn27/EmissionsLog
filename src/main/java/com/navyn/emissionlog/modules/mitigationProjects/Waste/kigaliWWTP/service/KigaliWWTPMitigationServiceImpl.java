package com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.service;

import com.navyn.emissionlog.Enums.ExcelType;
import com.navyn.emissionlog.Enums.Metrics.VolumePerTimeUnit;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.constants.KigaliWWTPConstants;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.constants.WWTPProjectPhase;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.dtos.KigaliWWTPMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.models.KigaliWWTPMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.repository.KigaliWWTPMitigationRepository;
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

import static com.navyn.emissionlog.utils.Specifications.MitigationSpecifications.hasProjectPhase;
import static com.navyn.emissionlog.utils.Specifications.MitigationSpecifications.hasYear;

@Service
@RequiredArgsConstructor
public class KigaliWWTPMitigationServiceImpl implements KigaliWWTPMitigationService {
    
    private final KigaliWWTPMitigationRepository repository;
    
    @Override
    public KigaliWWTPMitigation createKigaliWWTPMitigation(KigaliWWTPMitigationDto dto) {
        // Validate phase precedence
        validatePhasePrecedence(dto.getProjectPhase(), null);
        
        KigaliWWTPMitigation mitigation = new KigaliWWTPMitigation();
        
        // Convert phase capacity to standard unit (m³/day)
        double phaseCapacityInCubicMetersPerDay = dto.getPhaseCapacityUnit().toCubicMetersPerDay(dto.getPhaseCapacityPerDay());
        
        // Set user inputs (store in standard units)
        mitigation.setYear(dto.getYear());
        mitigation.setProjectPhase(dto.getProjectPhase());
        mitigation.setPhaseCapacityPerDay(phaseCapacityInCubicMetersPerDay);
        mitigation.setConnectedHouseholds(dto.getConnectedHouseholds());
        mitigation.setConnectedHouseholdsPercentage(dto.getConnectedHouseholdsPercentage());
        
        // Get constants
        double plantEfficiency = KigaliWWTPConstants.PLANT_OPERATIONAL_EFFICIENCY.getValue();
        double co2ePerM3 = KigaliWWTPConstants.CO2E_PER_M3_SLUDGE.getValue();
        
        // Calculations
        // 1. Effective Daily Flow (m³/day) = Plant Operational Efficiency × Phase capacity (m³/day) × Connected Households (%)
        double effectiveDailyFlow = plantEfficiency * phaseCapacityInCubicMetersPerDay * dto.getConnectedHouseholdsPercentage();
        mitigation.setEffectiveDailyFlow(effectiveDailyFlow);
        
        // 2. Annual Sludge Treated (m³/year) = Effective Daily Flow (m³/day) × 365
        double annualSludgeTreated = effectiveDailyFlow * 365;
        mitigation.setAnnualSludgeTreated(annualSludgeTreated);
        
        // 3. Annual Emissions Reduction (tCO₂e) = Annual Sludge Treated (m³) × CO₂e per m³ sludge (kg CO₂e per m³) / 1000
        double annualEmissionsReductionTonnes = (annualSludgeTreated * co2ePerM3) / 1000;
        mitigation.setAnnualEmissionsReductionTonnes(annualEmissionsReductionTonnes);
        
        // 4. Annual Emissions Reduction (ktCO₂e) = Annual Emissions Reduction (tCO₂e) / 1000
        double annualEmissionsReductionKilotonnes = annualEmissionsReductionTonnes / 1000;
        mitigation.setAnnualEmissionsReductionKilotonnes(annualEmissionsReductionKilotonnes);
        
        return repository.save(mitigation);
    }
    
    @Override
    public KigaliWWTPMitigation updateKigaliWWTPMitigation(UUID id, KigaliWWTPMitigationDto dto) {
        KigaliWWTPMitigation mitigation = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Kigali WWTP Mitigation record not found with id: " + id));
        
        // Validate phase precedence (exclude current record from validation)
        validatePhasePrecedence(dto.getProjectPhase(), id);
        
        // Convert phase capacity to standard unit (m³/day)
        double phaseCapacityInCubicMetersPerDay = dto.getPhaseCapacityUnit().toCubicMetersPerDay(dto.getPhaseCapacityPerDay());
        
        // Update user inputs (store in standard units)
        mitigation.setYear(dto.getYear());
        mitigation.setProjectPhase(dto.getProjectPhase());
        mitigation.setPhaseCapacityPerDay(phaseCapacityInCubicMetersPerDay);
        mitigation.setConnectedHouseholds(dto.getConnectedHouseholds());
        mitigation.setConnectedHouseholdsPercentage(dto.getConnectedHouseholdsPercentage());
        
        // Recalculate derived fields
        double plantEfficiency = KigaliWWTPConstants.PLANT_OPERATIONAL_EFFICIENCY.getValue();
        double co2ePerM3 = KigaliWWTPConstants.CO2E_PER_M3_SLUDGE.getValue();
        
        double effectiveDailyFlow = plantEfficiency * phaseCapacityInCubicMetersPerDay * dto.getConnectedHouseholdsPercentage();
        mitigation.setEffectiveDailyFlow(effectiveDailyFlow);
        
        double annualSludgeTreated = effectiveDailyFlow * 365;
        mitigation.setAnnualSludgeTreated(annualSludgeTreated);
        
        double annualEmissionsReductionTonnes = (annualSludgeTreated * co2ePerM3) / 1000;
        mitigation.setAnnualEmissionsReductionTonnes(annualEmissionsReductionTonnes);
        
        double annualEmissionsReductionKilotonnes = annualEmissionsReductionTonnes / 1000;
        mitigation.setAnnualEmissionsReductionKilotonnes(annualEmissionsReductionKilotonnes);
        
        return repository.save(mitigation);
    }
    
    @Override
    public List<KigaliWWTPMitigation> getAllKigaliWWTPMitigation(Integer year, WWTPProjectPhase projectPhase) {
        Specification<KigaliWWTPMitigation> spec = Specification
                .<KigaliWWTPMitigation>where(hasYear(year))
                .and(hasProjectPhase(projectPhase));
        return repository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
    }
    
    /**
     * Validates that the new phase is not smaller than the maximum existing phase.
     * Phases must progress in ascending order: NONE < PHASE_I < PHASE_II < PHASE_III
     * 
     * @param newPhase The phase to validate
     * @param excludeId ID to exclude from validation (for updates)
     * @throws RuntimeException if phase precedence is violated
     */
    private void validatePhasePrecedence(WWTPProjectPhase newPhase, UUID excludeId) {
        // Get the maximum phase from existing records
        repository.findTopByOrderByProjectPhaseDesc()
            .ifPresent(maxRecord -> {
                // Exclude the current record being updated
                if (excludeId != null && maxRecord.getId().equals(excludeId)) {
                    return;
                }
                
                WWTPProjectPhase maxPhase = maxRecord.getProjectPhase();
                
                // Check if new phase is smaller than max phase
                if (newPhase.ordinal() < maxPhase.ordinal()) {
                    throw new RuntimeException(
                        String.format("Cannot set phase to %s. The project has already reached %s. " +
                                     "Phases cannot go backward.", 
                                     newPhase.getDisplayName(), maxPhase.getDisplayName())
                    );
                }
            });
    }

    @Override
    public void deleteKigaliWWTPMitigation(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Kigali WWTP Mitigation record not found with id: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public byte[] generateExcelTemplate() {
        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Kigali WWTP Mitigation");

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
            titleCell.setCellValue("Kigali WWTP Mitigation Template");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));

            rowIdx++; // Blank row

            // Create header row
            Row headerRow = sheet.createRow(rowIdx++);
            headerRow.setHeightInPoints(22);
            String[] headers = {
                    "Year",
                    "Project Phase",
                    "Phase Capacity Per Day",
                    "Phase Capacity Unit",
                    "Connected Households",
                    "Connected Households Percentage"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Get enum values for dropdowns
            String[] projectPhaseValues = Arrays.stream(WWTPProjectPhase.values())
                    .map(Enum::name)
                    .toArray(String[]::new);
            String[] volumePerTimeUnitValues = Arrays.stream(VolumePerTimeUnit.values())
                    .map(Enum::name)
                    .toArray(String[]::new);

            // Create data validation helper
            DataValidationHelper validationHelper = sheet.getDataValidationHelper();

            // Data validation for Project Phase column (Column B, index 1)
            CellRangeAddressList phaseList = new CellRangeAddressList(3, 1000, 1, 1);
            DataValidationConstraint phaseConstraint = validationHelper
                    .createExplicitListConstraint(projectPhaseValues);
            DataValidation phaseValidation = validationHelper.createValidation(phaseConstraint, phaseList);
            phaseValidation.setShowErrorBox(true);
            phaseValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
            phaseValidation.createErrorBox("Invalid Project Phase", "Please select a valid project phase from the dropdown list.");
            phaseValidation.setShowPromptBox(true);
            phaseValidation.createPromptBox("Project Phase", "Select a project phase from the dropdown list.");
            sheet.addValidationData(phaseValidation);

            // Data validation for Phase Capacity Unit column (Column D, index 3)
            CellRangeAddressList volumeUnitList = new CellRangeAddressList(3, 1000, 3, 3);
            DataValidationConstraint volumeUnitConstraint = validationHelper
                    .createExplicitListConstraint(volumePerTimeUnitValues);
            DataValidation volumeUnitValidation = validationHelper.createValidation(volumeUnitConstraint, volumeUnitList);
            volumeUnitValidation.setShowErrorBox(true);
            volumeUnitValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
            volumeUnitValidation.createErrorBox("Invalid Unit", "Please select a valid unit from the dropdown list.");
            volumeUnitValidation.setShowPromptBox(true);
            volumeUnitValidation.createPromptBox("Phase Capacity Unit", "Select a unit from the dropdown list.");
            sheet.addValidationData(volumeUnitValidation);

            // Create example data rows
            Object[] exampleData1 = {2027, "PHASE_I", 12000.0, "CUBIC_METERS_PER_DAY", 208000.0, 0.65};
            Object[] exampleData2 = {2028, "PHASE_I", 12000.0, "CUBIC_METERS_PER_DAY", 208000.0, 0.70};

            // First example row
            Row exampleRow1 = sheet.createRow(rowIdx++);
            exampleRow1.setHeightInPoints(18);
            for (int i = 0; i < exampleData1.length; i++) {
                Cell cell = exampleRow1.createCell(i);
                if (i == 0) {
                    cell.setCellStyle(yearStyle);
                    cell.setCellValue(((Number) exampleData1[i]).intValue());
                } else if (i == 2 || i == 4 || i == 5) {
                    cell.setCellStyle(numberStyle);
                    cell.setCellValue(((Number) exampleData1[i]).doubleValue());
                } else {
                    cell.setCellStyle(dataStyle);
                    cell.setCellValue((String) exampleData1[i]);
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
                } else if (i == 2 || i == 4 || i == 5) {
                    CellStyle altNumStyle = workbook.createCellStyle();
                    altNumStyle.cloneStyleFrom(numberStyle);
                    altNumStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                    altNumStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    cell.setCellStyle(altNumStyle);
                    cell.setCellValue(((Number) exampleData2[i]).doubleValue());
                } else {
                    cell.setCellStyle(alternateDataStyle);
                    cell.setCellValue((String) exampleData2[i]);
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
    public Map<String, Object> createKigaliWWTPMitigationFromExcel(MultipartFile file) {
        List<KigaliWWTPMitigation> savedRecords = new ArrayList<>();
        List<Integer> skippedYears = new ArrayList<>();
        int totalProcessed = 0;

        try {
            List<KigaliWWTPMitigationDto> dtos = ExcelReader.readExcel(
                    file.getInputStream(),
                    KigaliWWTPMitigationDto.class,
                    ExcelType.KIGALI_WWTP_MITIGATION);

            for (int i = 0; i < dtos.size(); i++) {
                KigaliWWTPMitigationDto dto = dtos.get(i);
                totalProcessed++;
                int actualRowNumber = i + 1 + 3;

                // Validate required fields
                List<String> missingFields = new ArrayList<>();
                if (dto.getYear() == null) missingFields.add("Year");
                if (dto.getProjectPhase() == null) missingFields.add("Project Phase");
                if (dto.getPhaseCapacityPerDay() == null) missingFields.add("Phase Capacity Per Day");
                if (dto.getPhaseCapacityUnit() == null) missingFields.add("Phase Capacity Unit");
                if (dto.getConnectedHouseholds() == null) missingFields.add("Connected Households");
                if (dto.getConnectedHouseholdsPercentage() == null) missingFields.add("Connected Households Percentage");

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
                KigaliWWTPMitigation saved = createKigaliWWTPMitigation(dto);
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
