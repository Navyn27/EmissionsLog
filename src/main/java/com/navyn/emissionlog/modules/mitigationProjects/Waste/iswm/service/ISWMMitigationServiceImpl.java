package com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.service;

import com.navyn.emissionlog.Enums.ExcelType;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.enums.ESector;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.models.BAU;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.services.BAUService;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.dtos.ISWMMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.dtos.ISWMMitigationResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.dtos.ISWMParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.models.ISWMMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.repository.ISWMMitigationRepository;
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
public class ISWMMitigationServiceImpl implements ISWMMitigationService {
    
    private final ISWMMitigationRepository repository;
    private final ISWMParameterService parameterService;
    private final InterventionRepository interventionRepository;
    private final BAUService bauService;
    
    @Override
    @Transactional
    public ISWMMitigationResponseDto createISWMMitigation(ISWMMitigationDto dto) {
        ISWMMitigation saved = createISWMMitigationInternal(dto);
        return toResponseDto(saved);
    }

    /**
     * Internal method for Excel processing that returns entity
     */
    private ISWMMitigation createISWMMitigationInternal(ISWMMitigationDto dto) {
        ISWMMitigation mitigation = new ISWMMitigation();
        
        // Get ISWMParameter (latest active)
        ISWMParameterResponseDto paramDto = parameterService.getLatestActive();
        
        // Get Intervention
        Intervention intervention = interventionRepository.findById(dto.getProjectInterventionId())
                .orElseThrow(() -> new RuntimeException("Intervention not found with id: " + dto.getProjectInterventionId()));
        
        // Get BAU for Waste sector and same year
        Optional<BAU> bauOptional = bauService.getBAUByYearAndSector(dto.getYear(), ESector.WASTE);
        if (bauOptional.isEmpty()) {
            throw new RuntimeException("BAU record not found for year " + dto.getYear() + " and sector WASTE. Please create BAU record first.");
        }
        BAU bau = bauOptional.get();
        
        // Set user inputs
        mitigation.setYear(dto.getYear());
        mitigation.setWasteProcessed(dto.getWasteProcessed());
        mitigation.setProjectIntervention(intervention);
        
        // Calculations
        // DOFDiverted = wasteProcessed * %DegradableOrganicFraction
        Double dofDiverted = dto.getWasteProcessed() * (paramDto.getDegradableOrganicFraction() / 100.0);
        mitigation.setDofDiverted(dofDiverted);
        
        // AvoidedLandfill = wasteProcessed * LandfillAvoidance
        Double avoidedLandfill = dto.getWasteProcessed() * paramDto.getLandfillAvoidance();
        mitigation.setAvoidedLandfill(avoidedLandfill);
        
        // CompostingEmissions = DOFDiverted * CompostingEF
        Double compostingEmissions = dofDiverted * paramDto.getCompostingEF();
        mitigation.setCompostingEmissions(compostingEmissions);
        
        // NetAnnualReduction = (AvoidedLandfill - CompostingEmissions) / 1000 (ktCO2eq)
        Double netAnnualReduction = (avoidedLandfill - compostingEmissions) / 1000.0;
        mitigation.setNetAnnualReduction(netAnnualReduction);
        
        // MitigationScenarioEmission = BAU (ktCO2e) - NetAnnualReduction (ktCO2eq)
        Double mitigationScenarioEmission = bau.getValue() - netAnnualReduction;
        mitigation.setMitigationScenarioEmission(mitigationScenarioEmission);
        
        return repository.save(mitigation);
    }
    
    @Override
    @Transactional
    public ISWMMitigationResponseDto updateISWMMitigation(UUID id, ISWMMitigationDto dto) {
        ISWMMitigation mitigation = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("ISWM Mitigation record not found with id: " + id));
        
        // Get ISWMParameter (latest active)
        ISWMParameterResponseDto paramDto = parameterService.getLatestActive();
        
        // Get Intervention
        Intervention intervention = interventionRepository.findById(dto.getProjectInterventionId())
                .orElseThrow(() -> new RuntimeException("Intervention not found with id: " + dto.getProjectInterventionId()));
        
        // Get BAU for Waste sector and same year
        Optional<BAU> bauOptional = bauService.getBAUByYearAndSector(dto.getYear(), ESector.WASTE);
        if (bauOptional.isEmpty()) {
            throw new RuntimeException("BAU record not found for year " + dto.getYear() + " and sector WASTE. Please create BAU record first.");
        }
        BAU bau = bauOptional.get();
        
        // Update user inputs
        mitigation.setYear(dto.getYear());
        mitigation.setWasteProcessed(dto.getWasteProcessed());
        mitigation.setProjectIntervention(intervention);
        
        // Recalculate all derived fields
        // DOFDiverted = wasteProcessed * %DegradableOrganicFraction
        Double dofDiverted = dto.getWasteProcessed() * (paramDto.getDegradableOrganicFraction() / 100.0);
        mitigation.setDofDiverted(dofDiverted);
        
        // AvoidedLandfill = wasteProcessed * LandfillAvoidance
        Double avoidedLandfill = dto.getWasteProcessed() * paramDto.getLandfillAvoidance();
        mitigation.setAvoidedLandfill(avoidedLandfill);
        
        // CompostingEmissions = DOFDiverted * CompostingEF
        Double compostingEmissions = dofDiverted * paramDto.getCompostingEF();
        mitigation.setCompostingEmissions(compostingEmissions);
        
        // NetAnnualReduction = (AvoidedLandfill - CompostingEmissions) / 1000 (ktCO2eq)
        Double netAnnualReduction = (avoidedLandfill - compostingEmissions) / 1000.0;
        mitigation.setNetAnnualReduction(netAnnualReduction);
        
        // MitigationScenarioEmission = BAU (ktCO2e) - NetAnnualReduction (ktCO2eq)
        Double mitigationScenarioEmission = bau.getValue() - netAnnualReduction;
        mitigation.setMitigationScenarioEmission(mitigationScenarioEmission);
        
        ISWMMitigation saved = repository.save(mitigation);
        return toResponseDto(saved);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ISWMMitigationResponseDto> getAllISWMMitigation(Integer year) {
        Specification<ISWMMitigation> spec = Specification.where(hasYear(year));
        return repository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"))
                .stream()
                .map(this::toResponseDto)
                .toList();
    }
    
    @Override
    public void deleteISWMMitigation(UUID id) {
        ISWMMitigation mitigation = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("ISWM Mitigation record not found with id: " + id));
        repository.delete(mitigation);
    }

    @Override
    public byte[] generateExcelTemplate() {
        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("ISWM Mitigation");

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
            titleCell.setCellValue("ISWM Mitigation Template");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));

            rowIdx++; // Blank row

            // Create header row
            Row headerRow = sheet.createRow(rowIdx++);
            headerRow.setHeightInPoints(22);
            String[] headers = {
                    "Year",
                    "Waste Processed",
                    "Project Intervention Name"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Get all intervention names for dropdown
            List<Intervention> interventions = interventionRepository.findAll();
            String[] interventionNames = interventions.stream()
                    .map(Intervention::getName)
                    .toArray(String[]::new);

            // Create data validation helper
            DataValidationHelper validationHelper = sheet.getDataValidationHelper();

            // Data validation for Project Intervention Name column (Column C, index 2)
            if (interventionNames.length > 0) {
                CellRangeAddressList interventionNameList = new CellRangeAddressList(3, 1000, 2, 2);
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
                    interventions.isEmpty() ? "Example Intervention" : interventions.get(0).getName()
            };

            Object[] exampleData2 = {
                    2025,
                    1100.0,
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
                } else if (i == 1) { // Waste Processed (number)
                    cell.setCellStyle(numberStyle);
                    cell.setCellValue(((Number) exampleData1[i]).doubleValue());
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
                } else if (i == 1) { // Waste Processed (number)
                    CellStyle altNumStyle = workbook.createCellStyle();
                    altNumStyle.cloneStyleFrom(numberStyle);
                    altNumStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                    altNumStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    cell.setCellStyle(altNumStyle);
                    cell.setCellValue(((Number) exampleData2[i]).doubleValue());
                } else { // Intervention Name (string)
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
    public Map<String, Object> createISWMMitigationFromExcel(MultipartFile file) {
        List<ISWMMitigation> savedRecords = new ArrayList<>();
        List<Integer> skippedYears = new ArrayList<>();
        int totalProcessed = 0;

        try {
            List<ISWMMitigationDto> dtos = ExcelReader.readExcel(
                    file.getInputStream(),
                    ISWMMitigationDto.class,
                    ExcelType.ISWM_MITIGATION);

            for (int i = 0; i < dtos.size(); i++) {
                ISWMMitigationDto dto = dtos.get(i);
                totalProcessed++;
                int actualRowNumber = i + 1 + 3;

                // Validate required fields
                List<String> missingFields = new ArrayList<>();
                if (dto.getYear() == null) missingFields.add("Year");
                if (dto.getWasteProcessed() == null) missingFields.add("Waste Processed");
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
                    continue;
                }

                // Create the record
                ISWMMitigation saved = createISWMMitigationInternal(dto);
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

    /**
     * Maps entity to response DTO
     */
    private ISWMMitigationResponseDto toResponseDto(ISWMMitigation mitigation) {
        ISWMMitigationResponseDto dto = new ISWMMitigationResponseDto();
        dto.setId(mitigation.getId());
        dto.setYear(mitigation.getYear());
        dto.setWasteProcessed(mitigation.getWasteProcessed());
        dto.setDofDiverted(mitigation.getDofDiverted());
        dto.setAvoidedLandfill(mitigation.getAvoidedLandfill());
        dto.setCompostingEmissions(mitigation.getCompostingEmissions());
        dto.setNetAnnualReduction(mitigation.getNetAnnualReduction());
        dto.setMitigationScenarioEmission(mitigation.getMitigationScenarioEmission());

        // Map intervention - FORCE initialization within transaction to avoid lazy loading
        if (mitigation.getProjectIntervention() != null) {
            // Force Hibernate to initialize the proxy while session is still open
            Hibernate.initialize(mitigation.getProjectIntervention());
            Intervention intervention = mitigation.getProjectIntervention();
            ISWMMitigationResponseDto.InterventionInfo interventionInfo =
                    new ISWMMitigationResponseDto.InterventionInfo(
                            intervention.getId(),
                            intervention.getName()
                    );
            dto.setProjectIntervention(interventionInfo);
        } else {
            dto.setProjectIntervention(null);
        }

        return dto;
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
