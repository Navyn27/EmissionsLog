package com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.service;

import com.navyn.emissionlog.Enums.ExcelType;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.enums.ESector;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.models.BAU;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.services.BAUService;
import com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.dto.CreateLightBulbDTO;
import com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.dto.LightBulbMitigationExcelDto;
import com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.dto.LightBulbMitigationResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.dto.LightBulbParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.dto.UpdateLightBulbDTO;
import com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.model.LightBulb;
import com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.repository.ILightBulbRepository;
import com.navyn.emissionlog.modules.mitigationProjects.intervention.Intervention;
import com.navyn.emissionlog.modules.mitigationProjects.intervention.repositories.InterventionRepository;
import com.navyn.emissionlog.utils.ExcelReader;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.*;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

@Service
@AllArgsConstructor
public class LightBulbServiceImpl implements ILightBulbService {
    private final ILightBulbRepository lightBulbRepository;
    private final ILightBulbParameterService parameterService;
    private  final InterventionRepository interventionRepository;
    private  final BAUService bauService;
    @Override
    @Transactional
    public LightBulbMitigationResponseDto create(CreateLightBulbDTO lightBulbDTO) {

        LightBulb lightBulb = new LightBulb();
        // Get LightBulbParameter (the latest active) - throws an exception if none exists
        LightBulbParameterResponseDto paramDto;
        try {
            paramDto = parameterService.getLatestActive();
        } catch (RuntimeException e) {
            throw new RuntimeException(
                    "Cannot create Light Bulb Mitigation: No active Light Bulb Parameter found. " +
                            "Please create an active parameter first before creating mitigation records.",
                    e
            );
        }
        // Get Intervention
        Intervention intervention = interventionRepository.findById(lightBulbDTO.getProjectInterventionId())
                .orElseThrow(() -> new RuntimeException("Intervention not found with id: " + lightBulbDTO.getProjectInterventionId()));

        // Get BAU for ENERGY sector and same year
        Optional<BAU> bauOptional = bauService.getBAUByYearAndSector(lightBulbDTO.getYear(), ESector.ENERGY);
        if (bauOptional.isEmpty()) {
            throw new RuntimeException("BAU record not found for year " + lightBulbDTO.getYear() + " and sector ENERGY. Please create BAU record first.");
        }
        BAU bau = bauOptional.get();
        
        lightBulb.setYear(lightBulbDTO.getYear());
        lightBulb.setTotalInstalledBulbsPerYear(lightBulbDTO.getTotalInstalledBulbsPerYear());
        lightBulb.setReductionCapacityPerBulb(lightBulbDTO.getReductionCapacityPerBulb());
        lightBulb.setProjectIntervention(intervention);
        
        calculateAndSetFields(lightBulb, paramDto.getEmissionFactor(), bau.getValue());
        LightBulb saved = lightBulbRepository.save(lightBulb);
        return mapEntityToResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LightBulbMitigationResponseDto> getAll() {
        return lightBulbRepository.findAll().stream()
                .map(this::mapEntityToResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public LightBulbMitigationResponseDto getById(UUID id) {
        LightBulb lightBulb = lightBulbRepository.findByIdWithIntervention(id)
                .orElseThrow(() -> new EntityNotFoundException("LightBulb not found"));
        return mapEntityToResponseDto(lightBulb);
    }

    @Override
    @Transactional
    public LightBulbMitigationResponseDto update(UUID id, UpdateLightBulbDTO lightBulbDTO) {
        LightBulb lightBulb = lightBulbRepository.findByIdWithIntervention(id)
                .orElseThrow(() -> new EntityNotFoundException("LightBulb not found"));
        
        // Get latest active parameter for emission factor
        LightBulbParameterResponseDto paramDto = parameterService.getLatestActive();
        
        // Get BAU for the year (use existing year if not updated, otherwise use new year)
        int yearToUse = lightBulbDTO.getYear() != null ? lightBulbDTO.getYear() : lightBulb.getYear();
        Optional<BAU> bauOptional = bauService.getBAUByYearAndSector(yearToUse, ESector.ENERGY);
        if (bauOptional.isEmpty()) {
            throw new RuntimeException("BAU record not found for year " + yearToUse + " and sector ENERGY. Please create BAU record first.");
        }
        BAU bau = bauOptional.get();
        
        if (lightBulbDTO.getYear() != null) {
            lightBulb.setYear(lightBulbDTO.getYear());
        }
        if (lightBulbDTO.getTotalInstalledBulbsPerYear() != null) {
            lightBulb.setTotalInstalledBulbsPerYear(lightBulbDTO.getTotalInstalledBulbsPerYear());
        }
        if (lightBulbDTO.getReductionCapacityPerBulb() != null) {
            lightBulb.setReductionCapacityPerBulb(lightBulbDTO.getReductionCapacityPerBulb());
        }
        if (lightBulbDTO.getProjectInterventionId() != null) {
            Intervention intervention = interventionRepository.findById(lightBulbDTO.getProjectInterventionId())
                    .orElseThrow(() -> new RuntimeException("Intervention not found with id: " + lightBulbDTO.getProjectInterventionId()));
            lightBulb.setProjectIntervention(intervention);
        }
        
        calculateAndSetFields(lightBulb, paramDto.getEmissionFactor(), bau.getValue());
        LightBulb updated = lightBulbRepository.save(lightBulb);
        return mapEntityToResponseDto(updated);
    }

    @Override
    public void delete(UUID id) {
        lightBulbRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LightBulbMitigationResponseDto> getByYear(int year) {
        return lightBulbRepository.findAllByYear(year).stream()
                .map(this::mapEntityToResponseDto)
                .toList();
    }

    /**
     * Maps LightBulb entity to Response DTO
     * This method loads intervention data within the transaction to avoid lazy loading issues
     */
    private LightBulbMitigationResponseDto mapEntityToResponseDto(LightBulb lightBulb) {
        LightBulbMitigationResponseDto dto = new LightBulbMitigationResponseDto();
        dto.setId(lightBulb.getId());
        dto.setYear(lightBulb.getYear());
        dto.setTotalInstalledBulbsPerYear(lightBulb.getTotalInstalledBulbsPerYear());
        dto.setReductionCapacityPerBulb(lightBulb.getReductionCapacityPerBulb());
        dto.setTotalReductionPerYear(lightBulb.getTotalReductionPerYear());
        dto.setNetGhGMitigationAchieved(lightBulb.getNetGhGMitigationAchieved());
        dto.setScenarioGhGMitigationAchieved(lightBulb.getScenarioGhGMitigationAchieved());
        dto.setAdjustedBauEmissionMitigation(lightBulb.getAdjustedBauEmissionMitigation());
        dto.setCreatedAt(lightBulb.getCreatedAt());
        dto.setUpdatedAt(lightBulb.getUpdatedAt());

        // Map intervention - FORCE initialization within transaction to avoid lazy loading
        if (lightBulb.getProjectIntervention() != null) {
            // Force Hibernate to initialize the proxy while session is still open
            Hibernate.initialize(lightBulb.getProjectIntervention());
            Intervention intervention = lightBulb.getProjectIntervention();
            LightBulbMitigationResponseDto.InterventionInfo interventionInfo =
                    new LightBulbMitigationResponseDto.InterventionInfo(
                            intervention.getId(),
                            intervention.getName()
                    );
            dto.setProjectIntervention(interventionInfo);
        } else {
            dto.setProjectIntervention(null);
        }

        return dto;
    }

    private void calculateAndSetFields(LightBulb lightBulb, double emissionFactor, double bauValue) {
        double totalReductionPerYear = lightBulb.getReductionCapacityPerBulb()
                * lightBulb.getTotalInstalledBulbsPerYear();
        // Calculate in tCO2e: (kWh * kgCO2e/kWh) / 1000 = tCO2e
        double netGhGMitigationAchievedTCO2e = (totalReductionPerYear * emissionFactor) / 1000;
        // Convert to ktCO2e for calculations with BAU (which is in ktCO2e)
        double netGhGMitigationAchievedKtCO2e = netGhGMitigationAchievedTCO2e / 1000.0;
        double scenarioGhGMitigationAchieved = bauValue - netGhGMitigationAchievedKtCO2e;
        double adjustedBauEmissionMitigation = bauValue - netGhGMitigationAchievedKtCO2e;

        lightBulb.setTotalReductionPerYear(totalReductionPerYear);
        // Store in tCO2e for consistency with other energy projects (Rooftop, Waterheat)
        lightBulb.setNetGhGMitigationAchieved(netGhGMitigationAchievedTCO2e);
        // Store scenario and adjusted BAU in ktCO2e to match BAU units
        lightBulb.setScenarioGhGMitigationAchieved(scenarioGhGMitigationAchieved);
        lightBulb.setAdjustedBauEmissionMitigation(adjustedBauEmissionMitigation);
    }

    @Override
    public byte[] generateExcelTemplate() {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Light Bulb Mitigation");

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

            // Create an alternate data style
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
            titleCell.setCellValue("Light Bulb Mitigation Template");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

            rowIdx++; // Blank row

            // Create header row
            Row headerRow = sheet.createRow(rowIdx++);
            headerRow.setHeightInPoints(22);
            String[] headers = {
                    "Year",
                    "Total Installed Bulbs Per Year",
                    "Reduction Capacity Per Bulb",
                    "Project Intervention Name"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Get all interventions for dropdown
            List<Intervention> interventions = interventionRepository.findAll();
            String[] interventionNames = interventions.stream()
                    .map(Intervention::getName)
                    .toArray(String[]::new);

            // Create data validation helper
            DataValidationHelper validationHelper = sheet.getDataValidationHelper();

            // Data validation for Project Intervention Name column (Column D, index 3)
            if (interventionNames.length > 0) {
                CellRangeAddressList interventionList = new CellRangeAddressList(3, 1000, 3, 3);
                DataValidationConstraint interventionConstraint = validationHelper
                        .createExplicitListConstraint(interventionNames);
                DataValidation interventionValidation = validationHelper.createValidation(interventionConstraint,
                        interventionList);
                interventionValidation.setShowErrorBox(true);
                interventionValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
                interventionValidation.createErrorBox("Invalid Intervention",
                        "Please select a valid intervention from the dropdown list.");
                interventionValidation.setShowPromptBox(true);
                interventionValidation.createPromptBox("Project Intervention",
                        "Select an intervention from the dropdown list.");
                sheet.addValidationData(interventionValidation);
            }

            // Create example data rows
            Object[] exampleData1 = {
                    2024,
                    1000.0,
                    0.1,
                    interventions.isEmpty() ? "Example Intervention 1" : interventions.get(0).getName()
            };

            Object[] exampleData2 = {
                    2025,
                    1200.0,
                    0.12,
                    interventions.size() > 1 ? interventions.get(1).getName()
                            : (interventions.isEmpty() ? "Example Intervention 2" : interventions.get(0).getName())
            };

            // First example row
            Row exampleRow1 = sheet.createRow(rowIdx++);
            exampleRow1.setHeightInPoints(18);
            for (int i = 0; i < exampleData1.length; i++) {
                Cell cell = exampleRow1.createCell(i);
                if (i == 0) {
                    cell.setCellStyle(yearStyle);
                    cell.setCellValue(((Number) exampleData1[i]).intValue());
                } else if (i == 1 || i == 2) {
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
                } else if (i == 1 || i == 2) {
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
    public Map<String, Object> createLightBulbMitigationFromExcel(MultipartFile file) {
        List<LightBulbMitigationResponseDto> savedRecords = new ArrayList<>();
        List<Map<String, Object>> skippedRows = new ArrayList<>();
        int totalProcessed = 0;

        try {
            List<LightBulbMitigationExcelDto> dtos = ExcelReader.readExcel(
                    file.getInputStream(),
                    LightBulbMitigationExcelDto.class,
                    ExcelType.LIGHT_BULB_MITIGATION);

            for (int i = 0; i < dtos.size(); i++) {
                LightBulbMitigationExcelDto dto = dtos.get(i);
                totalProcessed++;
                int rowNumber = i + 1; // Excel row number (1-based, accounting for header row)
                int excelRowNumber = rowNumber + 2; // +2 for title row and blank row

                // Check if a row is effectively empty (missing critical fields) - skip it
                boolean isEffectivelyEmpty = (dto.getYear() == 0);

                if (isEffectivelyEmpty) {
                    // Skip this row - it's effectively empty (likely formatting or blank row)
                    continue;
                }

                // Validate required fields
                List<String> missingFields = new ArrayList<>();
                if (dto.getYear() == 0) {
                    missingFields.add("Year");
                }
                if (dto.getTotalInstalledBulbsPerYear() <= 0) {
                    missingFields.add("Total Installed Bulbs Per Year");
                }
                if (dto.getReductionCapacityPerBulb() <= 0) {
                    missingFields.add("Reduction Capacity Per Bulb");
                }
                if (dto.getProjectInterventionName() == null || dto.getProjectInterventionName().trim().isEmpty()) {
                    missingFields.add("Project Intervention Name");
                }

                if (!missingFields.isEmpty()) {
                    Map<String, Object> skipInfo = new HashMap<>();
                    skipInfo.put("row", excelRowNumber);
                    skipInfo.put("year", dto.getYear() > 0 ? dto.getYear() : "N/A");
                    skipInfo.put("reason", "Missing required fields: " + String.join(", ", missingFields));
                    skippedRows.add(skipInfo);
                    continue; // Skip this row
                }

                // Convert intervention name to UUID
                Optional<Intervention> interventionOpt = interventionRepository
                        .findByNameIgnoreCase(dto.getProjectInterventionName().trim());
                if (interventionOpt.isEmpty()) {
                    Map<String, Object> skipInfo = new HashMap<>();
                    skipInfo.put("row", excelRowNumber);
                    skipInfo.put("year", dto.getYear());
                    skipInfo.put("reason", String.format("Intervention '%s' not found. Please use a valid intervention name from the dropdown.", dto.getProjectInterventionName()));
                    skippedRows.add(skipInfo);
                    continue; // Skip this row
                }

                // Create DTO from Excel DTO
                CreateLightBulbDTO createDto = new CreateLightBulbDTO();
                createDto.setYear(dto.getYear());
                createDto.setTotalInstalledBulbsPerYear(dto.getTotalInstalledBulbsPerYear());
                createDto.setReductionCapacityPerBulb(dto.getReductionCapacityPerBulb());
                createDto.setProjectInterventionId(interventionOpt.get().getId());

                // Try to create the record - catch specific errors and skip instead of failing
                try {
                    LightBulbMitigationResponseDto saved = create(createDto);
                    savedRecords.add(saved);
                } catch (RuntimeException e) {
                    String errorMessage = e.getMessage();
                    if (errorMessage != null) {
                        // Check for BAU not found error
                        if ((errorMessage.contains("BAU record") || errorMessage.contains("BAU")) && errorMessage.contains("not found")) {
                            Map<String, Object> skipInfo = new HashMap<>();
                            skipInfo.put("row", excelRowNumber);
                            skipInfo.put("year", dto.getYear());
                            skipInfo.put("reason", errorMessage);
                            skippedRows.add(skipInfo);
                            continue; // Skip this row
                        }
                        // Check for Parameter not found error
                        if (errorMessage.contains("Light Bulb Parameter") || 
                            errorMessage.contains("active parameter") || 
                            errorMessage.contains("No active Light Bulb Parameter")) {
                            Map<String, Object> skipInfo = new HashMap<>();
                            skipInfo.put("row", excelRowNumber);
                            skipInfo.put("year", dto.getYear());
                            skipInfo.put("reason", errorMessage);
                            skippedRows.add(skipInfo);
                            continue; // Skip this row
                        }
                    }
                    // If it's a different error, re-throw it (e.g., file format issues)
                    throw e;
                }
            }

            // Calculate total skipped count
            int totalSkipped = skippedRows.size();

            Map<String, Object> result = new HashMap<>();
            result.put("saved", savedRecords);
            result.put("savedCount", savedRecords.size());
            result.put("skippedCount", totalSkipped);
            result.put("skippedRows", skippedRows);
            result.put("totalProcessed", totalProcessed);

            return result;
        } catch (IOException e) {
            // Re-throw IOException with user-friendly message
            String message = e.getMessage();
            if (message != null) {
                throw new RuntimeException("Error reading Excel file: " + message, e);
            }
            throw new RuntimeException("Error reading Excel file. Please ensure the file is a valid Excel format.", e);
        }
    }
}
