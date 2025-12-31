package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.service;

import com.navyn.emissionlog.Enums.ExcelType;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.dtos.ManureCoveringMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.dtos.ManureCoveringMitigationResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.dtos.ManureCoveringParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.models.ManureCoveringMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.repository.ManureCoveringMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.service.ManureCoveringParameterService;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.enums.ESector;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.models.BAU;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.repositories.BAURepository;
import com.navyn.emissionlog.modules.mitigationProjects.intervention.Intervention;
import com.navyn.emissionlog.modules.mitigationProjects.intervention.repositories.InterventionRepository;
import com.navyn.emissionlog.utils.ExcelReader;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

import static com.navyn.emissionlog.utils.Specifications.MitigationSpecifications.hasYear;

@Service
@RequiredArgsConstructor
public class ManureCoveringMitigationServiceImpl implements ManureCoveringMitigationService {
    
    private final ManureCoveringMitigationRepository repository;
    private final ManureCoveringParameterService manureCoveringParameterService;
    private final BAURepository bauRepository;
    private final InterventionRepository interventionRepository;

    /**
     * Maps ManureCoveringMitigation entity to Response DTO
     * This method loads intervention data within the transaction to avoid lazy loading issues
     */
    private ManureCoveringMitigationResponseDto toResponseDto(ManureCoveringMitigation mitigation) {
        ManureCoveringMitigationResponseDto dto = new ManureCoveringMitigationResponseDto();
        dto.setId(mitigation.getId());
        dto.setYear(mitigation.getYear());
        dto.setNumberOfCows(mitigation.getNumberOfCows());
        dto.setN2oEmissions(mitigation.getN2oEmissions());
        dto.setN2oReduction(mitigation.getN2oReduction());
        dto.setMitigatedN2oEmissionsKilotonnes(mitigation.getMitigatedN2oEmissionsKilotonnes());
        dto.setAdjustmentMitigation(mitigation.getAdjustmentMitigation());
        dto.setCreatedAt(mitigation.getCreatedAt());
        dto.setUpdatedAt(mitigation.getUpdatedAt());

        // Map intervention - FORCE initialization within transaction to avoid lazy loading
        if (mitigation.getIntervention() != null) {
            Hibernate.initialize(mitigation.getIntervention());
            Intervention intervention = mitigation.getIntervention();
            ManureCoveringMitigationResponseDto.InterventionInfo interventionInfo = new ManureCoveringMitigationResponseDto.InterventionInfo(
                    intervention.getId(),
                    intervention.getName());
            dto.setIntervention(interventionInfo);
        } else {
            dto.setIntervention(null);
        }

        return dto;
    }
    
    @Override
    @Transactional
    public ManureCoveringMitigationResponseDto createManureCoveringMitigation(ManureCoveringMitigationDto dto) {
        // Fetch the latest active parameter - throws exception if none exists
        ManureCoveringParameterResponseDto param;
        try {
            param = manureCoveringParameterService.getLatestActive();
        } catch (RuntimeException e) {
            throw new RuntimeException(
                    "Cannot create Manure Covering Mitigation: No active Manure Covering Parameter found. " +
                            "Please create an active parameter first before creating mitigation records.",
                    e);
        }

        ManureCoveringMitigation mitigation = new ManureCoveringMitigation();
        
        // Set user inputs
        mitigation.setYear(dto.getYear());
        mitigation.setNumberOfCows(dto.getNumberOfCows());
        
        // Get values from parameter
        double emissionPerCow = param.getEmissionPerCow();
        double reduction = param.getReduction();
        
        // Calculations for N2O Reduction (Compaction and Manure Covering)
        // 1. N2O emissions (tonnes CO2e/year) = emissionPerCow × numberOfCows
        double n2oEmissions = emissionPerCow * dto.getNumberOfCows();
        mitigation.setN2oEmissions(n2oEmissions);
        
        // 2. N2O reduction = n2oEmissions × reduction
        double n2oReduction = n2oEmissions * reduction;
        mitigation.setN2oReduction(n2oReduction);
        
        // 3. Mitigated N2O emissions (ktCO2e/year) = n2oReduction / 1000
        Double mitigatedN2oKilotonnes = n2oReduction / 1000.0;
        mitigation.setMitigatedN2oEmissionsKilotonnes(mitigatedN2oKilotonnes);
        
        // 4. Calculate Adjustment Mitigation = BAU.value - mitigatedEmissions
        BAU bau = bauRepository.findByYearAndSector(mitigation.getYear(), ESector.AFOLU)
                .orElseThrow(() -> new RuntimeException(
                        String.format("BAU record for AFOLU sector and year %d not found. Please create a BAU record first.",
                                mitigation.getYear())));
        double adjustmentMitigation = bau.getValue() - mitigatedN2oKilotonnes;
        mitigation.setAdjustmentMitigation(adjustmentMitigation);
        
        // Handle intervention
        if (dto.getInterventionId() != null) {
            Intervention intervention = interventionRepository.findById(dto.getInterventionId())
                    .orElseThrow(() -> new RuntimeException(
                            "Intervention not found with id: " + dto.getInterventionId()));
            mitigation.setIntervention(intervention);
        } else {
            mitigation.setIntervention(null);
        }
        
        ManureCoveringMitigation saved = repository.save(mitigation);
        return toResponseDto(saved);
    }

    @Override
    @Transactional
    public ManureCoveringMitigationResponseDto updateManureCoveringMitigation(UUID id, ManureCoveringMitigationDto dto) {
        ManureCoveringMitigation mitigation = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Manure Covering Mitigation record not found with id: " + id));

        // Fetch the latest active parameter
        ManureCoveringParameterResponseDto param = manureCoveringParameterService.getLatestActive();

        mitigation.setYear(dto.getYear());
        mitigation.setNumberOfCows(dto.getNumberOfCows());

        // Get values from parameter
        double emissionPerCow = param.getEmissionPerCow();
        double reduction = param.getReduction();

        // Recalculate all derived fields
        double n2oEmissions = emissionPerCow * dto.getNumberOfCows();
        mitigation.setN2oEmissions(n2oEmissions);

        double n2oReduction = n2oEmissions * reduction;
        mitigation.setN2oReduction(n2oReduction);

        Double mitigatedN2oKilotonnes = n2oReduction / 1000.0;
        mitigation.setMitigatedN2oEmissionsKilotonnes(mitigatedN2oKilotonnes);

        // Recalculate Adjustment Mitigation
        BAU bau = bauRepository.findByYearAndSector(mitigation.getYear(), ESector.AFOLU)
                .orElseThrow(() -> new RuntimeException(
                        String.format("BAU record for AFOLU sector and year %d not found. Please create a BAU record first.",
                                mitigation.getYear())));
        double adjustmentMitigation = bau.getValue() - mitigatedN2oKilotonnes;
        mitigation.setAdjustmentMitigation(adjustmentMitigation);

        // Handle intervention
        if (dto.getInterventionId() != null) {
            Intervention intervention = interventionRepository.findById(dto.getInterventionId())
                    .orElseThrow(() -> new RuntimeException(
                            "Intervention not found with id: " + dto.getInterventionId()));
            mitigation.setIntervention(intervention);
        } else {
            mitigation.setIntervention(null);
        }

        ManureCoveringMitigation updated = repository.save(mitigation);
        return toResponseDto(updated);
    }

    @Override
    public void deleteManureCoveringMitigation(UUID id) {
        ManureCoveringMitigation mitigation = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Manure Covering Mitigation record not found with id: " + id));
        repository.delete(mitigation);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ManureCoveringMitigationResponseDto> getAllManureCoveringMitigation(Integer year) {
        Specification<ManureCoveringMitigation> spec = Specification.where(hasYear(year));
        return repository.findAll(spec, Sort.by(Sort.Direction.ASC, "year"))
                .stream()
                .map(this::toResponseDto)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public byte[] generateExcelTemplate() {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Manure Covering Mitigation");

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

            // Create year style (centered number)
            XSSFCellStyle yearStyle = (XSSFCellStyle) workbook.createCellStyle();
            yearStyle.cloneStyleFrom(dataStyle);
            yearStyle.setAlignment(HorizontalAlignment.CENTER);

            // Create number style
            XSSFCellStyle numberStyle = (XSSFCellStyle) workbook.createCellStyle();
            numberStyle.cloneStyleFrom(dataStyle);
            Font numFont = workbook.createFont();
            numFont.setFontName("Calibri");
            numFont.setFontHeightInPoints((short) 10);
            numberStyle.setFont(numFont);
            DataFormat dataFormat = workbook.createDataFormat();
            numberStyle.setDataFormat(dataFormat.getFormat("#,##0"));
            numberStyle.setAlignment(HorizontalAlignment.RIGHT);
            numberStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            int rowIdx = 0;

            // Get all interventions for dropdown
            List<Intervention> allInterventions = interventionRepository.findAll();
            String[] interventionNames = allInterventions.stream()
                    .map(Intervention::getName)
                    .sorted()
                    .toArray(String[]::new);

            // Title row
            Row titleRow = sheet.createRow(rowIdx++);
            titleRow.setHeightInPoints(30);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Manure Covering Mitigation Template");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));

            rowIdx++; // Blank row

            // Create header row
            Row headerRow = sheet.createRow(rowIdx++);
            headerRow.setHeightInPoints(22);
            String[] headers = {"Year", "Number of Cows", "Intervention Name"};

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Create data validation helper
            DataValidationHelper validationHelper = sheet.getDataValidationHelper();

            // Data validation for Intervention Name column (Column C, index 2)
            if (interventionNames.length > 0) {
                CellRangeAddressList interventionList = new CellRangeAddressList(3, 1000, 2, 2);
                DataValidationConstraint interventionConstraint = validationHelper
                        .createExplicitListConstraint(interventionNames);
                DataValidation interventionValidation = validationHelper.createValidation(interventionConstraint,
                        interventionList);
                interventionValidation.setShowErrorBox(true);
                interventionValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
                interventionValidation.createErrorBox("Invalid Intervention",
                        "Please select a valid intervention from the dropdown list.");
                interventionValidation.setShowPromptBox(true);
                interventionValidation.createPromptBox("Intervention Name",
                        "Select an intervention from the dropdown list.");
                sheet.addValidationData(interventionValidation);
            }

            // Create example data rows
            Object[] exampleData1 = {
                    2024,
                    100,
                    interventionNames.length > 0 ? interventionNames[0] : ""
            };
            Object[] exampleData2 = {
                    2025,
                    150,
                    ""
            };

            // First example row
            Row exampleRow1 = sheet.createRow(rowIdx++);
            exampleRow1.setHeightInPoints(18);
            for (int i = 0; i < exampleData1.length; i++) {
                Cell cell = exampleRow1.createCell(i);
                if (i == 0) { // Year
                    cell.setCellStyle(yearStyle);
                    cell.setCellValue(((Number) exampleData1[i]).intValue());
                } else if (i == 1) { // Number of Cows
                    cell.setCellStyle(numberStyle);
                    cell.setCellValue(((Number) exampleData1[i]).intValue());
                } else { // Intervention Name (string)
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
                } else if (i == 1) { // Number of Cows
                    CellStyle altNumStyle = workbook.createCellStyle();
                    altNumStyle.cloneStyleFrom(numberStyle);
                    altNumStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                    altNumStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    cell.setCellStyle(altNumStyle);
                    cell.setCellValue(((Number) exampleData2[i]).intValue());
                } else { // Intervention Name (string)
                    CellStyle altInterventionStyle = workbook.createCellStyle();
                    altInterventionStyle.cloneStyleFrom(alternateDataStyle);
                    cell.setCellStyle(altInterventionStyle);
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
    @Transactional
    public Map<String, Object> createManureCoveringMitigationFromExcel(MultipartFile file) {
        List<ManureCoveringMitigationResponseDto> savedRecords = new ArrayList<>();
        List<Integer> skippedYears = new ArrayList<>();
        List<Map<String, Object>> skippedParameterNotFound = new ArrayList<>();
        List<Map<String, Object>> skippedInterventionNotFound = new ArrayList<>();
        List<Map<String, Object>> skippedMissingFields = new ArrayList<>();
        List<Map<String, Object>> skippedBAUNotFound = new ArrayList<>();
        int totalProcessed = 0;

        try {
            List<ManureCoveringMitigationDto> dtos = ExcelReader.readExcel(
                    file.getInputStream(),
                    ManureCoveringMitigationDto.class,
                    ExcelType.MANURE_COVERING_MITIGATION);

            // Create a list of DTOs with their original row numbers for error reporting
            List<Map.Entry<ManureCoveringMitigationDto, Integer>> dtoWithRowNumbers = new ArrayList<>();
            for (int i = 0; i < dtos.size(); i++) {
                dtoWithRowNumbers.add(new AbstractMap.SimpleEntry<>(dtos.get(i), i));
            }

            // Sort by year (ascending) to ensure proper ordering
            dtoWithRowNumbers.sort((entry1, entry2) -> {
                Integer year1 = entry1.getKey().getYear();
                Integer year2 = entry2.getKey().getYear();
                if (year1 == null && year2 == null)
                    return 0;
                if (year1 == null)
                    return 1;
                if (year2 == null)
                    return -1;
                return year1.compareTo(year2);
            });

            for (Map.Entry<ManureCoveringMitigationDto, Integer> entry : dtoWithRowNumbers) {
                ManureCoveringMitigationDto dto = entry.getKey();
                int originalIndex = entry.getValue();
                totalProcessed++;
                int rowNumber = originalIndex + 1;
                int excelRowNumber = rowNumber + 2; // +2 for header row and 0-based index

                // Validate required fields
                List<String> missingFields = new ArrayList<>();
                if (dto.getYear() == null) {
                    missingFields.add("Year");
                }
                if (dto.getNumberOfCows() == null) {
                    missingFields.add("Number of Cows");
                }

                if (!missingFields.isEmpty()) {
                    Map<String, Object> skipInfo = new HashMap<>();
                    skipInfo.put("row", excelRowNumber);
                    skipInfo.put("year", dto.getYear() != null ? dto.getYear() : "N/A");
                    skipInfo.put("reason",
                            "Missing required fields: " + String.join(", ", missingFields));
                    skippedMissingFields.add(skipInfo);
                    continue;
                }

                // Handle intervention name from Excel - convert to interventionId
                if (dto.getInterventionName() != null && !dto.getInterventionName().trim().isEmpty()) {
                    String interventionName = dto.getInterventionName().trim();
                    Optional<Intervention> intervention = interventionRepository
                            .findByNameIgnoreCase(interventionName);
                    if (intervention.isPresent()) {
                        dto.setInterventionId(intervention.get().getId());
                    } else {
                        Map<String, Object> skipInfo = new HashMap<>();
                        skipInfo.put("row", excelRowNumber);
                        skipInfo.put("year", dto.getYear());
                        skipInfo.put("reason",
                                String.format("Intervention '%s' not found", interventionName));
                        skippedInterventionNotFound.add(skipInfo);
                        continue;
                    }
                }
                // Clear the temporary interventionName field
                dto.setInterventionName(null);

                // Check if year already exists
                if (repository.findByYear(dto.getYear()).isPresent()) {
                    skippedYears.add(dto.getYear());
                    continue;
                }

                // Try to create the record - catch specific errors and skip instead of failing
                try {
                    ManureCoveringMitigationResponseDto saved = createManureCoveringMitigation(dto);
                    savedRecords.add(saved);
                } catch (RuntimeException e) {
                    String errorMessage = e.getMessage();
                    if (errorMessage != null) {
                        // Check for Parameter not found error
                        if (errorMessage.contains("Manure Covering Parameter") ||
                                errorMessage.contains("active parameter") ||
                                errorMessage.contains("No active Manure Covering Parameter")) {
                            Map<String, Object> skipInfo = new HashMap<>();
                            skipInfo.put("row", excelRowNumber);
                            skipInfo.put("year", dto.getYear());
                            skipInfo.put("reason", errorMessage);
                            skippedParameterNotFound.add(skipInfo);
                            continue;
                        }
                        // Check for BAU not found error
                        if (errorMessage.contains("BAU record") && errorMessage.contains("not found")) {
                            Map<String, Object> skipInfo = new HashMap<>();
                            skipInfo.put("row", excelRowNumber);
                            skipInfo.put("year", dto.getYear());
                            skipInfo.put("reason", errorMessage);
                            skippedBAUNotFound.add(skipInfo);
                            continue;
                        }
                    }
                    // If it's a different error, re-throw it
                    throw e;
                }
            }

            // Calculate total skipped count
            int totalSkipped = skippedYears.size() + skippedParameterNotFound.size() +
                    skippedInterventionNotFound.size() + skippedMissingFields.size() +
                    skippedBAUNotFound.size();

            Map<String, Object> result = new HashMap<>();
            result.put("saved", savedRecords);
            result.put("savedCount", savedRecords.size());
            result.put("skippedCount", totalSkipped);
            result.put("skippedYears", skippedYears);
            result.put("skippedParameterNotFound", skippedParameterNotFound);
            result.put("skippedInterventionNotFound", skippedInterventionNotFound);
            result.put("skippedMissingFields", skippedMissingFields);
            result.put("skippedBAUNotFound", skippedBAUNotFound);
            result.put("totalProcessed", totalProcessed);

            return result;
        } catch (IOException e) {
            String message = e.getMessage();
            if (message != null) {
                throw new RuntimeException(message, e);
            } else {
                throw new RuntimeException(
                        "Incorrect template. Please download the correct template and try again.",
                        e);
            }
        } catch (NullPointerException e) {
            throw new RuntimeException(
                    "Missing required fields. Please fill in all required fields in your Excel file.",
                    e);
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if (errorMsg != null) {
                throw new RuntimeException(errorMsg, e);
            }
            throw new RuntimeException("Error processing Excel file. Please check your file and try again.",
                    e);
        }
    }
}
