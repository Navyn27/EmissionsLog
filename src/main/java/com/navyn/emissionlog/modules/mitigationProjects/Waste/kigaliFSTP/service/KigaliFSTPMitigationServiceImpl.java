package com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.service;

import com.navyn.emissionlog.Enums.ExcelType;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.enums.ESector;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.models.BAU;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.services.BAUService;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.dtos.KigaliFSTPParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.dtos.KigaliFSTPMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.dtos.KigaliFSTPMitigationResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.models.KigaliFSTPMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.repository.KigaliFSTPMitigationRepository;
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
public class KigaliFSTPMitigationServiceImpl implements KigaliFSTPMitigationService {
    
    private final KigaliFSTPMitigationRepository repository;
    private final KigaliFSTPParameterService parameterService;
    private final InterventionRepository interventionRepository;
    private final BAUService bauService;
    
    /**
     * Maps KigaliFSTPMitigation entity to Response DTO
     * This method loads intervention data within the transaction to avoid lazy loading issues
     */
    private KigaliFSTPMitigationResponseDto toResponseDto(KigaliFSTPMitigation mitigation) {
        KigaliFSTPMitigationResponseDto dto = new KigaliFSTPMitigationResponseDto();
        dto.setId(mitigation.getId());
        dto.setYear(mitigation.getYear());
        dto.setAnnualSludgeTreated(mitigation.getAnnualSludgeTreated());
        dto.setMethanePotential(mitigation.getMethanePotential());
        dto.setCo2ePerM3Sludge(mitigation.getCo2ePerM3Sludge());
        dto.setAnnualEmissionsReductionTonnes(mitigation.getAnnualEmissionsReductionTonnes());
        dto.setAnnualEmissionsReductionKilotonnes(mitigation.getAnnualEmissionsReductionKilotonnes());
        dto.setAdjustedBauEmissionMitigation(mitigation.getAdjustedBauEmissionMitigation());
        dto.setCreatedAt(mitigation.getCreatedAt());
        dto.setUpdatedAt(mitigation.getUpdatedAt());

        // Map intervention - FORCE initialization within transaction to avoid lazy loading
        if (mitigation.getProjectIntervention() != null) {
            // Force Hibernate to initialize the proxy while session is still open
            Hibernate.initialize(mitigation.getProjectIntervention());
            com.navyn.emissionlog.modules.mitigationProjects.intervention.Intervention intervention = mitigation.getProjectIntervention();
            KigaliFSTPMitigationResponseDto.InterventionInfo interventionInfo =
                    new KigaliFSTPMitigationResponseDto.InterventionInfo(
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
    private KigaliFSTPMitigation createKigaliFSTPMitigationInternal(KigaliFSTPMitigationDto dto) {
        KigaliFSTPMitigation mitigation = new KigaliFSTPMitigation();
        
        // Get KigaliFSTPParameter (latest active)
        KigaliFSTPParameterResponseDto paramDto = parameterService.getLatestActive();
        
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
        mitigation.setAnnualSludgeTreated(dto.getAnnualSludgeTreated());
        mitigation.setProjectIntervention(intervention);
        
        // Calculations
        // 1. MethanePotential (kg CH4 per m³) = MethaneEmissionFactor (kg CH4 per kg COD) × COD_Concentration (kg COD per m³)
        Double methanePotential = paramDto.getMethaneEmissionFactor() * paramDto.getCodConcentration();
        mitigation.setMethanePotential(methanePotential);
        
        // 2. CO₂e per m³ sludge (kg CO2e per m³) = MethanePotential × CH₄ GWP (100-yr) (kg CO2e per kg CH4)
        Double co2ePerM3Sludge = methanePotential * paramDto.getCh4Gwp100Year();
        mitigation.setCo2ePerM3Sludge(co2ePerM3Sludge);
        
        // 3. Annual Emissions Reduction (tCO₂e) = annualSludgeTreated (m³) × CO₂e per m³ sludge (kg CO₂e per m³) / 1000
        Double annualEmissionsReductionTonnes = (dto.getAnnualSludgeTreated() * co2ePerM3Sludge) / 1000;
        mitigation.setAnnualEmissionsReductionTonnes(annualEmissionsReductionTonnes);
        
        // 4. Annual Emissions Reduction (ktCO₂e) = Annual Emissions Reduction (tCO₂e) / 1000
        Double annualEmissionsReductionKilotonnes = annualEmissionsReductionTonnes / 1000;
        mitigation.setAnnualEmissionsReductionKilotonnes(annualEmissionsReductionKilotonnes);
        
        // 5. Adjusted BAU Emission Mitigation (ktCO₂e) = BAU (ktCO₂e) - Annual Emissions Reduction (ktCO₂e)
        Double adjustedBauEmissionMitigation = bau.getValue() - annualEmissionsReductionKilotonnes;
        mitigation.setAdjustedBauEmissionMitigation(adjustedBauEmissionMitigation);
        
        return repository.save(mitigation);
    }
    
    @Override
    @Transactional
    public KigaliFSTPMitigationResponseDto createKigaliFSTPMitigation(KigaliFSTPMitigationDto dto) {
        KigaliFSTPMitigation saved = createKigaliFSTPMitigationInternal(dto);
        return toResponseDto(saved);
    }
    
    @Override
    @Transactional
    public KigaliFSTPMitigationResponseDto updateKigaliFSTPMitigation(UUID id, KigaliFSTPMitigationDto dto) {
        KigaliFSTPMitigation mitigation = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Kigali FSTP Mitigation record not found with id: " + id));
        
        // Get KigaliFSTPParameter (latest active)
        KigaliFSTPParameterResponseDto paramDto = parameterService.getLatestActive();
        
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
        mitigation.setAnnualSludgeTreated(dto.getAnnualSludgeTreated());
        mitigation.setProjectIntervention(intervention);
        
        // Recalculate derived fields
        // 1. MethanePotential
        Double methanePotential = paramDto.getMethaneEmissionFactor() * paramDto.getCodConcentration();
        mitigation.setMethanePotential(methanePotential);
        
        // 2. CO₂e per m³ sludge
        Double co2ePerM3Sludge = methanePotential * paramDto.getCh4Gwp100Year();
        mitigation.setCo2ePerM3Sludge(co2ePerM3Sludge);
        
        // 3. Annual Emissions Reduction (tCO₂e)
        Double annualEmissionsReductionTonnes = (dto.getAnnualSludgeTreated() * co2ePerM3Sludge) / 1000;
        mitigation.setAnnualEmissionsReductionTonnes(annualEmissionsReductionTonnes);
        
        // 4. Annual Emissions Reduction (ktCO₂e)
        Double annualEmissionsReductionKilotonnes = annualEmissionsReductionTonnes / 1000;
        mitigation.setAnnualEmissionsReductionKilotonnes(annualEmissionsReductionKilotonnes);
        
        // 5. Adjusted BAU Emission Mitigation
        Double adjustedBauEmissionMitigation = bau.getValue() - annualEmissionsReductionKilotonnes;
        mitigation.setAdjustedBauEmissionMitigation(adjustedBauEmissionMitigation);
        
        KigaliFSTPMitigation saved = repository.save(mitigation);
        return toResponseDto(saved);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<KigaliFSTPMitigationResponseDto> getAllKigaliFSTPMitigation(Integer year) {
        Specification<KigaliFSTPMitigation> spec = Specification.where(hasYear(year));
        List<KigaliFSTPMitigation> mitigations = repository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
        return mitigations.stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public void deleteKigaliFSTPMitigation(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Kigali FSTP Mitigation record not found with id: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public byte[] generateExcelTemplate() {
        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Kigali FSTP Mitigation");

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
            titleCell.setCellValue("Kigali FSTP Mitigation Template");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));

            rowIdx++; // Blank row

            // Create header row
            Row headerRow = sheet.createRow(rowIdx++);
            headerRow.setHeightInPoints(22);
            String[] headers = {
                    "Year",
                    "Annual Sludge Treated (m³/year)",
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

            // Data validation for Project Intervention Name column (Column C, index 2)
            if (interventionNames.length > 0) {
                CellRangeAddressList interventionList = new CellRangeAddressList(3, 1000, 2, 2);
                DataValidationConstraint interventionConstraint = validationHelper
                        .createExplicitListConstraint(interventionNames);
                DataValidation interventionValidation = validationHelper.createValidation(interventionConstraint, interventionList);
                interventionValidation.setShowErrorBox(true);
                interventionValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
                interventionValidation.createErrorBox("Invalid Intervention", "Please select a valid intervention from the dropdown list.");
                interventionValidation.setShowPromptBox(true);
                interventionValidation.createPromptBox("Project Intervention", "Select an intervention from the dropdown list.");
                sheet.addValidationData(interventionValidation);
            }

            // Create example data rows
            Object[] exampleData1 = {2024, 10000.0, interventions.isEmpty() ? "Example Intervention 1" : interventions.get(0).getName()};
            Object[] exampleData2 = {2025, 12000.0, interventions.size() > 1 ? interventions.get(1).getName() : (interventions.isEmpty() ? "Example Intervention 2" : interventions.get(0).getName())};

            // First example row
            Row exampleRow1 = sheet.createRow(rowIdx++);
            exampleRow1.setHeightInPoints(18);
            for (int i = 0; i < exampleData1.length; i++) {
                Cell cell = exampleRow1.createCell(i);
                if (i == 0) {
                    cell.setCellStyle(yearStyle);
                    cell.setCellValue(((Number) exampleData1[i]).intValue());
                } else if (i == 1) {
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
                } else if (i == 1) {
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
    @Transactional
    public Map<String, Object> createKigaliFSTPMitigationFromExcel(MultipartFile file) {
        List<KigaliFSTPMitigation> savedRecords = new ArrayList<>();
        int totalProcessed = 0;

        try {
            List<KigaliFSTPMitigationDto> dtos = ExcelReader.readExcel(
                    file.getInputStream(),
                    KigaliFSTPMitigationDto.class,
                    ExcelType.KIGALI_FSTP_MITIGATION);

            for (int i = 0; i < dtos.size(); i++) {
                KigaliFSTPMitigationDto dto = dtos.get(i);
                totalProcessed++;
                int actualRowNumber = i + 1 + 3;

                // Validate required fields
                List<String> missingFields = new ArrayList<>();
                if (dto.getYear() == null) missingFields.add("Year");
                if (dto.getAnnualSludgeTreated() == null) missingFields.add("Annual Sludge Treated");
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

                // Create the record (duplicate years are now allowed)
                KigaliFSTPMitigation saved = createKigaliFSTPMitigationInternal(dto);
                savedRecords.add(saved);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("savedCount", savedRecords.size());
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
