package com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.service;

import com.navyn.emissionlog.Enums.ExcelType;
import com.navyn.emissionlog.Enums.Metrics.MassPerYearUnit;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.enums.ESector;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.models.BAU;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.services.BAUService;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.dtos.WasteToEnergyMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.dtos.WasteToEnergyMitigationResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.dtos.WasteToWtEParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.models.WasteToEnergyMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.models.WasteToWtEParameter;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.repository.WasteToEnergyMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.service.WasteToWtEParameterService;
import com.navyn.emissionlog.modules.mitigationProjects.intervention.Intervention;
import com.navyn.emissionlog.modules.mitigationProjects.intervention.repositories.InterventionRepository;
import com.navyn.emissionlog.utils.ExcelReader;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.*;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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

import static com.navyn.emissionlog.utils.Specifications.MitigationSpecifications.hasYear;

@Service
@RequiredArgsConstructor
public class WasteToEnergyMitigationServiceImpl implements WasteToEnergyMitigationService {
    
    private final WasteToEnergyMitigationRepository repository;
    private final WasteToWtEParameterService parameterService;
    private final InterventionRepository interventionRepository;
    private final BAUService bauService;

    /**
     * Maps WasteToEnergyMitigation entity to Response DTO
     * This method loads intervention data within the transaction to avoid lazy loading issues
     */
    private WasteToEnergyMitigationResponseDto toResponseDto(WasteToEnergyMitigation mitigation) {
        WasteToEnergyMitigationResponseDto dto = new WasteToEnergyMitigationResponseDto();
        dto.setId(mitigation.getId());
        dto.setYear(mitigation.getYear());
        dto.setWasteToWtE(mitigation.getWasteToWtE());
        dto.setGhgReductionTonnes(mitigation.getGhgReductionTonnes());
        dto.setGhgReductionKilotonnes(mitigation.getGhgReductionKilotonnes());
        dto.setAdjustedEmissionsWithWtE(mitigation.getAdjustedEmissionsWithWtE());

        // Map intervention - FORCE initialization within transaction to avoid lazy loading
        if (mitigation.getProjectIntervention() != null) {
            // Force Hibernate to initialize the proxy while session is still open
            Hibernate.initialize(mitigation.getProjectIntervention());
            Intervention intervention = mitigation.getProjectIntervention();
            WasteToEnergyMitigationResponseDto.InterventionInfo interventionInfo =
                    new WasteToEnergyMitigationResponseDto.InterventionInfo(
                            intervention.getId(),
                            intervention.getName()
                    );
            dto.setProjectIntervention(interventionInfo);
        } else {
            dto.setProjectIntervention(null);
        }

        return dto;
    }

    /**
     * Internal method for Excel processing that returns entity
     */
    private WasteToEnergyMitigation createWasteToEnergyMitigationInternal(WasteToEnergyMitigationDto dto) {
        WasteToEnergyMitigation mitigation = new WasteToEnergyMitigation();
        
        // Get WasteToWtEParameter (latest active) - throws exception if none exists
        WasteToWtEParameterResponseDto paramDto;
        try {
            paramDto = parameterService.getLatestActive();
        } catch (RuntimeException e) {
            throw new RuntimeException(
                    "Cannot create Waste to Energy Mitigation: No active Waste to Energy Parameter found. " +
                            "Please create an active parameter first before creating mitigation records.",
                    e
            );
        }
        
        // Get Intervention
        Intervention intervention = interventionRepository.findById(dto.getProjectInterventionId())
                .orElseThrow(() -> new RuntimeException("Intervention not found with id: " + dto.getProjectInterventionId()));
        
        // Get BAU for Waste sector and same year
        Optional<BAU> bauOptional = bauService.getBAUByYearAndSector(dto.getYear(), ESector.WASTE);
        if (bauOptional.isEmpty()) {
            throw new RuntimeException("BAU record not found for year " + dto.getYear() + " and sector WASTE. Please create BAU record first.");
        }
        BAU bau = bauOptional.get();
        
        // Convert to standard units
        double wasteInTonnesPerYear = dto.getWasteToWtEUnit().toTonnesPerYear(dto.getWasteToWtE());
        
        // Set user inputs (store in standard units)
        mitigation.setYear(dto.getYear());
        mitigation.setWasteToWtE(wasteInTonnesPerYear);
        mitigation.setProjectIntervention(intervention);
        
        // Calculations
        // GHG Reduction (tCO2eq) = Net Emission Factor (tCO2eq/t) * Waste to WtE (t/year)
        Double ghgReductionTonnes = paramDto.getNetEmissionFactor() * wasteInTonnesPerYear;
        mitigation.setGhgReductionTonnes(ghgReductionTonnes);
        
        // GHG Reduction (KtCO2eq) = GHG Reduction (tCO2eq) / 1000
        Double ghgReductionKilotonnes = ghgReductionTonnes / 1000;
        mitigation.setGhgReductionKilotonnes(ghgReductionKilotonnes);
        
        // Adjusted Emissions (with WtE, ktCO₂e) = BAU (ktCO₂e) - GHG Reduction (KtCO2eq)
        Double adjustedEmissions = bau.getValue() - ghgReductionKilotonnes;
        mitigation.setAdjustedEmissionsWithWtE(adjustedEmissions);
        
        return repository.save(mitigation);
    }

    @Override
    @Transactional
    public WasteToEnergyMitigationResponseDto createWasteToEnergyMitigation(WasteToEnergyMitigationDto dto) {
        WasteToEnergyMitigation saved = createWasteToEnergyMitigationInternal(dto);
        return toResponseDto(saved);
    }
    
    @Override
    @Transactional
    public WasteToEnergyMitigationResponseDto updateWasteToEnergyMitigation(UUID id, WasteToEnergyMitigationDto dto) {
        WasteToEnergyMitigation mitigation = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Waste to Energy Mitigation record not found with id: " + id));
        
        // Get WasteToWtEParameter (latest active) - throws exception if none exists
        WasteToWtEParameterResponseDto paramDto;
        try {
            paramDto = parameterService.getLatestActive();
        } catch (RuntimeException e) {
            throw new RuntimeException(
                    "Cannot update Waste to Energy Mitigation: No active Waste to Energy Parameter found. " +
                            "Please create an active parameter first before updating mitigation records.",
                    e
            );
        }
        
        // Get Intervention
        Intervention intervention = interventionRepository.findById(dto.getProjectInterventionId())
                .orElseThrow(() -> new RuntimeException("Intervention not found with id: " + dto.getProjectInterventionId()));
        
        // Get BAU for Waste sector and same year
        Optional<BAU> bauOptional = bauService.getBAUByYearAndSector(dto.getYear(), ESector.WASTE);
        if (bauOptional.isEmpty()) {
            throw new RuntimeException("BAU record not found for year " + dto.getYear() + " and sector WASTE. Please create BAU record first.");
        }
        BAU bau = bauOptional.get();
        
        // Convert to standard units
        double wasteInTonnesPerYear = dto.getWasteToWtEUnit().toTonnesPerYear(dto.getWasteToWtE());
        
        // Update user inputs (store in standard units)
        mitigation.setYear(dto.getYear());
        mitigation.setWasteToWtE(wasteInTonnesPerYear);
        mitigation.setProjectIntervention(intervention);
        
        // Recalculate derived fields
        Double ghgReductionTonnes = paramDto.getNetEmissionFactor() * wasteInTonnesPerYear;
        mitigation.setGhgReductionTonnes(ghgReductionTonnes);
        
        Double ghgReductionKilotonnes = ghgReductionTonnes / 1000;
        mitigation.setGhgReductionKilotonnes(ghgReductionKilotonnes);
        
        Double adjustedEmissions = bau.getValue() - ghgReductionKilotonnes;
        mitigation.setAdjustedEmissionsWithWtE(adjustedEmissions);
        
        WasteToEnergyMitigation saved = repository.save(mitigation);
        return toResponseDto(saved);
    }
    
    @Override
    public void deleteWasteToEnergyMitigation(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Waste to Energy Mitigation record not found with id: " + id);
        }
        repository.deleteById(id);
    }
    
    @Override
    @Transactional
    public List<WasteToEnergyMitigationResponseDto> getAllWasteToEnergyMitigation(Integer year) {
        Specification<WasteToEnergyMitigation> spec = Specification.where(hasYear(year));
        List<WasteToEnergyMitigation> mitigations = repository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
        return mitigations.stream()
                .map(this::toResponseDto)
                .toList();
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
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));

            rowIdx++; // Blank row

            // Create header row
            Row headerRow = sheet.createRow(rowIdx++);
            headerRow.setHeightInPoints(22);
            String[] headers = {
                    "Year",
                    "Waste to WtE",
                    "Waste to WtE Unit",
                    "Project Intervention Name"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Get enum values for dropdowns
            String[] wasteToWtEUnitValues = java.util.Arrays.stream(MassPerYearUnit.values())
                    .map(Enum::name)
                    .toArray(String[]::new);

            // Get all intervention names for dropdown
            List<Intervention> interventions = interventionRepository.findAll();
            String[] interventionNames = interventions.stream()
                    .map(Intervention::getName)
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

            // Data validation for Project Intervention Name column (Column D, index 3)
            if (interventionNames.length > 0) {
                CellRangeAddressList interventionNameList = new CellRangeAddressList(3, 1000, 3, 3);
                DataValidationConstraint interventionNameConstraint = validationHelper
                        .createExplicitListConstraint(interventionNames);
                DataValidation interventionNameValidation = validationHelper.createValidation(interventionNameConstraint,
                        interventionNameList);
                interventionNameValidation.setShowErrorBox(true);
                interventionNameValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
                interventionNameValidation.createErrorBox("Invalid Intervention",
                        "Please select a valid intervention from the dropdown list.");
                interventionNameValidation.setShowPromptBox(true);
                interventionNameValidation.createPromptBox("Project Intervention Name", "Select an intervention from the dropdown list.");
                sheet.addValidationData(interventionNameValidation);
            }

            // Create example data rows
            Object[] exampleData1 = {
                    2024,
                    1000.0,
                    "TONNES_PER_YEAR",
                    interventions.isEmpty() ? "Example Intervention" : interventions.get(0).getName()
            };

            Object[] exampleData2 = {
                    2025,
                    1200.0,
                    "TONNES_PER_YEAR",
                    interventions.size() > 1 ? interventions.get(1).getName() :
                            (interventions.isEmpty() ? "Example Intervention" : interventions.get(0).getName())
            };

            // First example row
            Row exampleRow1 = sheet.createRow(rowIdx++);
            exampleRow1.setHeightInPoints(18);
            for (int i = 0; i < exampleData1.length; i++) {
                Cell cell = exampleRow1.createCell(i);
                if (i == 0) { // Year
                    cell.setCellStyle(yearStyle);
                    cell.setCellValue(((Number) exampleData1[i]).intValue());
                } else if (i == 1) { // Waste to WtE (number)
                    cell.setCellStyle(numberStyle);
                    cell.setCellValue(((Number) exampleData1[i]).doubleValue());
                } else { // Units and Intervention Name (string)
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
                } else if (i == 1) { // Waste to WtE (number)
                    CellStyle altNumStyle = workbook.createCellStyle();
                    altNumStyle.cloneStyleFrom(numberStyle);
                    altNumStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                    altNumStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    cell.setCellStyle(altNumStyle);
                    cell.setCellValue(((Number) exampleData2[i]).doubleValue());
                } else { // Units and Intervention Name (string)
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
    @Transactional
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
                if (dto.getProjectInterventionName() == null || dto.getProjectInterventionName().trim().isEmpty()) {
                    missingFields.add("Project Intervention Name");
                }

                if (!missingFields.isEmpty()) {
                    throw new RuntimeException(String.format(
                            "Row %d: Missing required fields: %s. Please fill in all required fields in your Excel file.",
                            actualRowNumber, String.join(", ", missingFields)));
                }

                // Convert intervention name to UUID
                Optional<Intervention> interventionOpt = interventionRepository.findByNameIgnoreCase(dto.getProjectInterventionName().trim());
                if (interventionOpt.isEmpty()) {
                    throw new RuntimeException(String.format(
                            "Row %d: Intervention '%s' not found. Please use a valid intervention name from the dropdown.",
                            actualRowNumber, dto.getProjectInterventionName()));
                }
                dto.setProjectInterventionId(interventionOpt.get().getId());

                // Check if year already exists
                if (repository.findByYear(dto.getYear()).isPresent()) {
                    skippedYears.add(dto.getYear());
                    continue; // Skip this row
                }

                // Create the record
                WasteToEnergyMitigation saved = createWasteToEnergyMitigationInternal(dto);
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
