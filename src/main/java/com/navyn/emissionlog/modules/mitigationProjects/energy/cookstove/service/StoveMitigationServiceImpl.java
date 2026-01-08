package com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.service;

import com.navyn.emissionlog.Enums.ExcelType;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.enums.ESector;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.models.BAU;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.services.BAUService;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.dto.CreateStoveMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.dto.StoveMitigationExcelDto;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.dto.StoveMitigationResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.dto.UpdateStoveMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.enums.EStoveType;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.dto.parameters.*;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.models.StoveMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.repository.StoveMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.service.parameters.*;
import com.navyn.emissionlog.modules.mitigationProjects.intervention.Intervention;
import com.navyn.emissionlog.modules.mitigationProjects.intervention.repositories.InterventionRepository;
import com.navyn.emissionlog.utils.ExcelReader;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class StoveMitigationServiceImpl implements IStoveMitigationService {

    private final StoveMitigationRepository repository;
    private final IElectricityParameterService electricityParameterService;
    private final ILGPParameterService lgpParameterService;
    private final ICharcoalParameterService charcoalParameterService;
    private final IFireWoodParameterService fireWoodParameterService;
    private final InterventionRepository interventionRepository;
    private final BAUService bauService;

    @Override
    @Transactional
    public StoveMitigationResponseDto createStoveMitigation(CreateStoveMitigationDto dto) {
        StoveMitigation mitigation = new StoveMitigation();

        mitigation.setYear(dto.getYear());
        mitigation.setStoveType(dto.getStoveType());
        mitigation.setUnitsInstalled(dto.getUnitsInstalled());
        mitigation.setEfficiency(dto.getEfficiency());

        // Set intervention if provided
        if (dto.getProjectInterventionId() != null) {
            Intervention intervention = interventionRepository.findById(dto.getProjectInterventionId())
                    .orElseThrow(() -> new RuntimeException("Intervention not found with id: " + dto.getProjectInterventionId()));
            mitigation.setProjectIntervention(intervention);
        }

        // Calculate fuelConsumption and projectEmission based on stoveType
        calculateAndSetFields(mitigation, dto.getStoveType());

        StoveMitigation saved = repository.save(mitigation);
        return mapEntityToResponseDto(saved);
    }

    @Override
    @Transactional
    public StoveMitigationResponseDto updateStoveMitigation(UUID id, UpdateStoveMitigationDto dto) {
        StoveMitigation mitigation = repository.findByIdWithIntervention(id)
                .orElseThrow(() -> new EntityNotFoundException("StoveMitigation not found with id: " + id));

        if (dto.getYear() != null) {
            mitigation.setYear(dto.getYear());
        }
        if (dto.getStoveType() != null) {
            mitigation.setStoveType(dto.getStoveType());
        }
        if (dto.getUnitsInstalled() != null) {
            mitigation.setUnitsInstalled(dto.getUnitsInstalled());
        }
        if (dto.getEfficiency() != null) {
            mitigation.setEfficiency(dto.getEfficiency());
        }
        if (dto.getProjectInterventionId() != null) {
            Intervention intervention = interventionRepository.findById(dto.getProjectInterventionId())
                    .orElseThrow(() -> new RuntimeException("Intervention not found with id: " + dto.getProjectInterventionId()));
            mitigation.setProjectIntervention(intervention);
        } else if (dto.getProjectInterventionId() == null && mitigation.getProjectIntervention() != null) {
            // Allow clearing intervention by passing null
            mitigation.setProjectIntervention(null);
        }

        // Recalculate fields based on current stoveType
        EStoveType stoveTypeToUse = dto.getStoveType() != null ? dto.getStoveType() : mitigation.getStoveType();
        calculateAndSetFields(mitigation, stoveTypeToUse);

        StoveMitigation updated = repository.save(mitigation);
        return mapEntityToResponseDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public StoveMitigationResponseDto getStoveMitigationById(UUID id) {
        StoveMitigation mitigation = repository.findByIdWithIntervention(id)
                .orElseThrow(() -> new EntityNotFoundException("StoveMitigation not found with id: " + id));
        return mapEntityToResponseDto(mitigation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StoveMitigationResponseDto> getAllStoveMitigations(Integer year, EStoveType stoveType) {
        List<StoveMitigation> mitigations;
        if (year != null && stoveType != null) {
            mitigations = repository.findAllByYearAndStoveType(year, stoveType);
        } else if (year != null) {
            mitigations = repository.findAllByYear(year);
        } else if (stoveType != null) {
            mitigations = repository.findAllByStoveType(stoveType);
        } else {
            mitigations = repository.findAll();
        }

        // Calculate totalProjectEmission (sum of ALL records, not just filtered ones)
        double totalProjectEmission = repository.findAll().stream()
                .mapToDouble(m -> m.getProjectEmission() != null ? m.getProjectEmission() : 0.0)
                .sum();

        final double finalTotalProjectEmission = totalProjectEmission;

        return mitigations.stream()
                .map(m -> {
                    StoveMitigationResponseDto dto = mapEntityToResponseDtoBasic(m);
                    dto.setTotalProjectEmission(finalTotalProjectEmission);
                    // Calculate emissionReduction = BAU ENERGY (for the record's year) - totalProjectEmission
                    Optional<BAU> bauOptional = bauService.getBAUByYearAndSector(m.getYear(), ESector.ENERGY);
                    if (bauOptional.isPresent()) {
                        double bauValue = bauOptional.get().getValue();
                        dto.setEmissionReduction(bauValue - finalTotalProjectEmission);
                    }
                    return dto;
                })
                .toList();
    }

    @Override
    @Transactional
    public void deleteStoveMitigation(UUID id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("StoveMitigation not found with id: " + id);
        }
        repository.deleteById(id);
    }

    private void calculateAndSetFields(StoveMitigation mitigation, EStoveType stoveType) {
        double fuelConsumptionPerProjectDevice;
        double projectEmission;

        switch (stoveType) {
            case ELECTRIC:
                ElectricityParameterResponseDto elecParam = electricityParameterService.getLatestActive();
                System.out.println(elecParam.getEmissionFactor());
                // Fuel consumption per project device type = Per capita Electricity Consumption per project device * HH size
                fuelConsumptionPerProjectDevice = elecParam.getFuelConsumption() * elecParam.getSize();
                System.out.println(fuelConsumptionPerProjectDevice);
                // Project emissions (PEy) = (Fuel consumption per project device type * Grid emission factor) / 1000
                projectEmission = (fuelConsumptionPerProjectDevice * elecParam.getEmissionFactor()) / 1000;
                System.out.println(projectEmission);
                break;

            case LGP:
                LGPParameterResponseDto lgpParam = lgpParameterService.getLatestActive();
                // Fuel consumption per project device type = Per capita fuel consumption by a project device * HH size
                fuelConsumptionPerProjectDevice = lgpParam.getFuelConsumption() * lgpParam.getSize();
                // Project emissions = ((CO2 emission factor + Non CO2 emission factor) * Number of cookstoves * Net calorific value * Fuel consumption per project device type) / 1000
                projectEmission = ((lgpParam.getEmissionFactor() + lgpParam.getAdjustedEmissionFactor())
                        * mitigation.getUnitsInstalled()
                        * lgpParam.getNetCalorificValue()
                        * fuelConsumptionPerProjectDevice) / 1000.0;
                break;

            case CHARCOAL:
                CharcoalParameterResponseDto charcoalParam = charcoalParameterService.getLatestActive();
                // Fuel consumption per project device type = (Per capita fuel consumption by a baseline device * Baseline efficiency) / Efficiency
                // Percentage fields are divided by 100
                fuelConsumptionPerProjectDevice = (charcoalParam.getFuelConsumption() * (charcoalParam.getEfficiency() / 100.0)) / (mitigation.getEfficiency() / 100.0);
                // Project emission = (((hh size * CO2 emission factor * Fraction of non-renewable biomass) + Non CO2 emission factor) * Fuel consumption per project device type * Net calorific value * Number of cookstoves HH) / 1000
                double charcoalBiomassFraction = charcoalParam.getBiomass()/100;
                projectEmission = (((charcoalParam.getSize() * charcoalParam.getEmissionFactor() * charcoalBiomassFraction)
                        + charcoalParam.getAdjustedEmissionFactor())
                        * fuelConsumptionPerProjectDevice
                        * charcoalParam.getNetCalorificValue()
                        * mitigation.getUnitsInstalled()) / 1000.0;
                break;

            case FIRE_WOOD:
                FireWoodParameterResponseDto firewoodParam = fireWoodParameterService.getLatestActive();
                // Fuel consumption per project device type = Efficiency * (Per capita fuel consumption by a baseline device / Baseline efficiency)
                // Percentage fields are divided by 100
                // Simplify: (efficiency/100) * (fuelConsumption / (baselineEfficiency/100)) = (efficiency * fuelConsumption) / baselineEfficiency
                fuelConsumptionPerProjectDevice = ((firewoodParam.getEfficiency() / 100) * firewoodParam.getFuelConsumption()) / (mitigation.getEfficiency() / 100);
                // Project emissions = (((hh size * CO2 emission factor * Fraction of non-renewable biomass) + Adjusted Non CO2 emission factor) * Fuel consumption per project device type * Net calorific value * Number of cookstoves HH) / 1000
                double firewoodBiomassFraction = firewoodParam.getBiomass()/100;
                projectEmission = (((firewoodParam.getSize() * firewoodParam.getEmissionFactor() * firewoodBiomassFraction)
                        + firewoodParam.getAdjustedEmissionFactor())
                        * fuelConsumptionPerProjectDevice
                        * firewoodParam.getNetCalorificValue()
                        * mitigation.getUnitsInstalled()) / 1000.0;
                break;

            default:
                throw new RuntimeException("Unsupported stove type: " + stoveType);
        }

        mitigation.setFuelConsumption(fuelConsumptionPerProjectDevice);
        mitigation.setProjectEmission(projectEmission);
    }

    private StoveMitigationResponseDto mapEntityToResponseDto(StoveMitigation entity) {
        StoveMitigationResponseDto dto = mapEntityToResponseDtoBasic(entity);

        // Calculate totalProjectEmission (sum of all projectEmission for all records)
        double totalProjectEmission = repository.findAll().stream()
                .mapToDouble(m -> m.getProjectEmission() != null ? m.getProjectEmission() : 0.0)
                .sum();
        dto.setTotalProjectEmission(totalProjectEmission);

        // Calculate emissionReduction = BAU ENERGY (for the year) - totalProjectEmission
        Optional<BAU> bauOptional = bauService.getBAUByYearAndSector(entity.getYear(), ESector.ENERGY);
        if (bauOptional.isPresent()) {
            double bauValue = bauOptional.get().getValue();
            dto.setEmissionReduction(bauValue - totalProjectEmission);
        }

        return dto;
    }

    private StoveMitigationResponseDto mapEntityToResponseDtoBasic(StoveMitigation entity) {
        StoveMitigationResponseDto dto = new StoveMitigationResponseDto();
        dto.setId(entity.getId());
        dto.setStoveType(entity.getStoveType());
        dto.setYear(entity.getYear());
        dto.setUnitsInstalled(entity.getUnitsInstalled());
        dto.setEfficiency(entity.getEfficiency());
        dto.setFuelConsumption(entity.getFuelConsumption());
        dto.setProjectEmission(entity.getProjectEmission());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        // Map intervention - FORCE initialization within transaction to avoid lazy loading
        if (entity.getProjectIntervention() != null) {
            Hibernate.initialize(entity.getProjectIntervention());
            Intervention intervention = entity.getProjectIntervention();
            StoveMitigationResponseDto.InterventionInfo interventionInfo =
                    new StoveMitigationResponseDto.InterventionInfo(
                            intervention.getId(),
                            intervention.getName()
                    );
            dto.setProjectIntervention(interventionInfo);
        } else {
            dto.setProjectIntervention(null);
        }

        return dto;
    }

    @Override
    public byte[] generateExcelTemplate() {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Cookstove Mitigation");

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
            titleCell.setCellValue("Cookstove Mitigation Template");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));

            rowIdx++; // Blank row

            // Create header row
            Row headerRow = sheet.createRow(rowIdx++);
            headerRow.setHeightInPoints(22);
            String[] headers = {
                    "Year",
                    "Stove Type",
                    "Units Installed",
                    "Efficiency (%)",
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

            // Data validation for Stove Type column (Column B, index 1)
            String[] stoveTypeValues = {"FIRE_WOOD", "CHARCOAL", "LGP", "ELECTRIC"};
            CellRangeAddressList stoveTypeList = new CellRangeAddressList(3, 1000, 1, 1);
            DataValidationConstraint stoveTypeConstraint = validationHelper
                    .createExplicitListConstraint(stoveTypeValues);
            DataValidation stoveTypeValidation = validationHelper.createValidation(stoveTypeConstraint, stoveTypeList);
            stoveTypeValidation.setShowErrorBox(true);
            stoveTypeValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
            stoveTypeValidation.createErrorBox("Invalid Stove Type",
                    "Please select a valid stove type from the dropdown list (FIRE_WOOD, CHARCOAL, LGP, ELECTRIC).");
            stoveTypeValidation.setShowPromptBox(true);
            stoveTypeValidation.createPromptBox("Stove Type",
                    "Select a stove type from the dropdown list.");
            sheet.addValidationData(stoveTypeValidation);

            // Data validation for Project Intervention Name column (Column E, index 4)
            if (interventionNames.length > 0) {
                CellRangeAddressList interventionList = new CellRangeAddressList(3, 1000, 4, 4);
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
                    "FIRE_WOOD",
                    1000,
                    70.0,
                    interventions.isEmpty() ? "Example Intervention 1" : interventions.get(0).getName()
            };

            Object[] exampleData2 = {
                    2025,
                    "CHARCOAL",
                    1200,
                    75.0,
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
                } else if (i == 1 || i == 4) {
                    cell.setCellStyle(dataStyle);
                    cell.setCellValue((String) exampleData1[i]);
                } else if (i == 2) {
                    cell.setCellStyle(yearStyle);
                    cell.setCellValue(((Number) exampleData1[i]).intValue());
                } else {
                    cell.setCellStyle(numberStyle);
                    cell.setCellValue(((Number) exampleData1[i]).doubleValue());
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
                } else if (i == 1 || i == 4) {
                    cell.setCellStyle(alternateDataStyle);
                    cell.setCellValue((String) exampleData2[i]);
                } else if (i == 2) {
                    CellStyle altYearStyle = workbook.createCellStyle();
                    altYearStyle.cloneStyleFrom(alternateDataStyle);
                    altYearStyle.setAlignment(HorizontalAlignment.CENTER);
                    cell.setCellStyle(altYearStyle);
                    cell.setCellValue(((Number) exampleData2[i]).intValue());
                } else {
                    CellStyle altNumStyle = workbook.createCellStyle();
                    altNumStyle.cloneStyleFrom(numberStyle);
                    altNumStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                    altNumStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    cell.setCellStyle(altNumStyle);
                    cell.setCellValue(((Number) exampleData2[i]).doubleValue());
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
    @Transactional
    public Map<String, Object> createStoveMitigationFromExcel(MultipartFile file) {
        List<StoveMitigationResponseDto> savedRecords = new ArrayList<>();
        List<Map<String, Object>> skippedRows = new ArrayList<>();
        int totalProcessed = 0;

        try {
            List<StoveMitigationExcelDto> dtos = ExcelReader.readExcel(
                    file.getInputStream(),
                    StoveMitigationExcelDto.class,
                    ExcelType.COOKSTOVE_MITIGATION);

            for (int i = 0; i < dtos.size(); i++) {
                StoveMitigationExcelDto dto = dtos.get(i);
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
                if (dto.getStoveType() == null || dto.getStoveType().trim().isEmpty()) {
                    missingFields.add("Stove Type");
                }
                if (dto.getUnitsInstalled() <= 0) {
                    missingFields.add("Units Installed");
                }
                if (dto.getEfficiency() <= 0) {
                    missingFields.add("Efficiency (%)");
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

                // Validate and parse stove type
                EStoveType stoveType;
                try {
                    stoveType = EStoveType.valueOf(dto.getStoveType().trim().toUpperCase());
                } catch (IllegalArgumentException e) {
                    Map<String, Object> skipInfo = new HashMap<>();
                    skipInfo.put("row", excelRowNumber);
                    skipInfo.put("year", dto.getYear());
                    skipInfo.put("reason", String.format("Invalid stove type '%s'. Valid values are: FIRE_WOOD, CHARCOAL, LGP, ELECTRIC", dto.getStoveType()));
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
                CreateStoveMitigationDto createDto = new CreateStoveMitigationDto();
                createDto.setYear(dto.getYear());
                createDto.setStoveType(stoveType);
                createDto.setUnitsInstalled(dto.getUnitsInstalled());
                createDto.setEfficiency(dto.getEfficiency());
                createDto.setProjectInterventionId(interventionOpt.get().getId());

                // Try to create the record - catch specific errors and skip instead of failing
                try {
                    StoveMitigationResponseDto saved = createStoveMitigation(createDto);
                    savedRecords.add(saved);
                } catch (RuntimeException e) {
                    String errorMessage = e.getMessage();
                    if (errorMessage != null) {
                        // Check for parameter not found errors
                        if (errorMessage.contains("Parameter") || errorMessage.contains("active parameter") ||
                                errorMessage.contains("No active")) {
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
                if (message.contains("Sheet") && message.contains("not found")) {
                    throw new RuntimeException("Template format error: " + message, e);
                }
            }
            throw new RuntimeException("Error reading Excel file: " + e.getMessage(), e);
        }
    }
}

