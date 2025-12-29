package com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.service;

import com.navyn.emissionlog.Enums.ExcelType;
import com.navyn.emissionlog.Enums.Metrics.MassPerYearUnit;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.enums.ESector;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.models.BAU;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.services.BAUService;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.dtos.EPRParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.dtos.EPRPlasticWasteMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.dtos.EPRPlasticWasteMitigationResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.models.EPRPlasticWasteMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.repository.EPRPlasticWasteMitigationRepository;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.navyn.emissionlog.utils.Specifications.MitigationSpecifications.hasYear;

@Service
@RequiredArgsConstructor
public class EPRPlasticWasteMitigationServiceImpl implements EPRPlasticWasteMitigationService {
    
    private final EPRPlasticWasteMitigationRepository repository;
    private final EPRParameterService parameterService;
    private final InterventionRepository interventionRepository;
    private final BAUService bauService;
    
    /**
     * Maps EPRPlasticWasteMitigation entity to Response DTO
     * This method loads intervention data within the transaction to avoid lazy loading issues
     */
    private EPRPlasticWasteMitigationResponseDto toResponseDto(EPRPlasticWasteMitigation mitigation) {
        EPRPlasticWasteMitigationResponseDto dto = new EPRPlasticWasteMitigationResponseDto();
        dto.setId(mitigation.getId());
        dto.setYear(mitigation.getYear());
        dto.setPlasticWasteTonnesPerYear(mitigation.getPlasticWasteTonnesPerYear());
        dto.setRecycledPlasticWithoutEPRTonnesPerYear(mitigation.getRecycledPlasticWithoutEPRTonnesPerYear());
        dto.setRecycledPlasticWithEPRTonnesPerYear(mitigation.getRecycledPlasticWithEPRTonnesPerYear());
        dto.setAdditionalRecyclingVsBAUTonnesPerYear(mitigation.getAdditionalRecyclingVsBAUTonnesPerYear());
        dto.setGhgReductionTonnes(mitigation.getGhgReductionTonnes());
        dto.setGhgReductionKilotonnes(mitigation.getGhgReductionKilotonnes());
        dto.setAdjustedBauEmissionMitigation(mitigation.getAdjustedBauEmissionMitigation());

        // Map intervention - FORCE initialization within transaction to avoid lazy loading
        if (mitigation.getProjectIntervention() != null) {
            // Force Hibernate to initialize the proxy while session is still open
            Hibernate.initialize(mitigation.getProjectIntervention());
            Intervention intervention = mitigation.getProjectIntervention();
            EPRPlasticWasteMitigationResponseDto.InterventionInfo interventionInfo =
                    new EPRPlasticWasteMitigationResponseDto.InterventionInfo(
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
    private EPRPlasticWasteMitigation createEPRPlasticWasteMitigationInternal(EPRPlasticWasteMitigationDto dto) {
        EPRPlasticWasteMitigation mitigation = new EPRPlasticWasteMitigation();
        
        // Get EPRParameter (latest active) - throws exception if none exists
        EPRParameterResponseDto paramDto;
        try {
            paramDto = parameterService.getLatestActive();
        } catch (RuntimeException e) {
            throw new RuntimeException(
                    "Cannot create EPR Plastic Waste Mitigation: No active EPR Parameter found. " +
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
        double plasticWasteInTonnesPerYear = dto.getPlasticWasteTonnesPerYearUnit().toTonnesPerYear(dto.getPlasticWasteTonnesPerYear());
        
        // Set user inputs (store in standard units)
        mitigation.setYear(dto.getYear());
        mitigation.setPlasticWasteTonnesPerYear(plasticWasteInTonnesPerYear);
        mitigation.setProjectIntervention(intervention);
        
        // Calculations
        // 1. Recycled Plastic (without EPR) (t/year) = Plastic Waste × Recycling Rate (without EPR)
        Double recycledPlasticWithoutEPR = plasticWasteInTonnesPerYear * paramDto.getRecyclingRateWithoutEPR();
        mitigation.setRecycledPlasticWithoutEPRTonnesPerYear(recycledPlasticWithoutEPR);
        
        // 2. Recycled Plastic (with EPR) (t/year) = Plastic Waste × Recycling Rate (with EPR)
        Double recycledPlasticWithEPR = plasticWasteInTonnesPerYear * paramDto.getRecyclingRateWithEPR();
        mitigation.setRecycledPlasticWithEPRTonnesPerYear(recycledPlasticWithEPR);
        
        // 3. Additional Recycling vs. BAU (t/year) = Recycled Plastic (with EPR) - Recycled Plastic (without EPR)
        Double additionalRecycling = recycledPlasticWithEPR - recycledPlasticWithoutEPR;
        mitigation.setAdditionalRecyclingVsBAUTonnesPerYear(additionalRecycling);
        
        // 4. GHG Reduction (tCO2eq) = Additional Recycling vs. BAU × Emission Factor
        Double ghgReductionTonnes = additionalRecycling * paramDto.getEmissionFactor();
        mitigation.setGhgReductionTonnes(ghgReductionTonnes);
        
        // 5. GHG Reduction (ktCO2eq) = GHG Reduction (tCO2eq) / 1000
        Double ghgReductionKilotonnes = ghgReductionTonnes / 1000.0;
        mitigation.setGhgReductionKilotonnes(ghgReductionKilotonnes);
        
        // 6. Adjusted BAU Emission Mitigation (ktCO2e) = BAU - GHG Reduction (ktCO2eq)
        Double adjustedBauEmissionMitigation = bau.getValue() - ghgReductionKilotonnes;
        mitigation.setAdjustedBauEmissionMitigation(adjustedBauEmissionMitigation);
        
        return repository.save(mitigation);
    }
    
    @Override
    @Transactional
    public EPRPlasticWasteMitigationResponseDto createEPRPlasticWasteMitigation(EPRPlasticWasteMitigationDto dto) {
        EPRPlasticWasteMitigation saved = createEPRPlasticWasteMitigationInternal(dto);
        return toResponseDto(saved);
    }
    
    @Override
    @Transactional
    public EPRPlasticWasteMitigationResponseDto updateEPRPlasticWasteMitigation(UUID id, EPRPlasticWasteMitigationDto dto) {
        EPRPlasticWasteMitigation mitigation = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("EPR Plastic Waste Mitigation record not found with id: " + id));
        
        // Get EPRParameter (latest active) - throws exception if none exists
        EPRParameterResponseDto paramDto;
        try {
            paramDto = parameterService.getLatestActive();
        } catch (RuntimeException e) {
            throw new RuntimeException(
                    "Cannot update EPR Plastic Waste Mitigation: No active EPR Parameter found. " +
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
        double plasticWasteInTonnesPerYear = dto.getPlasticWasteTonnesPerYearUnit().toTonnesPerYear(dto.getPlasticWasteTonnesPerYear());
        
        // Update user inputs (store in standard units)
        mitigation.setYear(dto.getYear());
        mitigation.setPlasticWasteTonnesPerYear(plasticWasteInTonnesPerYear);
        mitigation.setProjectIntervention(intervention);
        
        // Recalculate derived fields
        Double recycledPlasticWithoutEPR = plasticWasteInTonnesPerYear * paramDto.getRecyclingRateWithoutEPR();
        mitigation.setRecycledPlasticWithoutEPRTonnesPerYear(recycledPlasticWithoutEPR);
        
        Double recycledPlasticWithEPR = plasticWasteInTonnesPerYear * paramDto.getRecyclingRateWithEPR();
        mitigation.setRecycledPlasticWithEPRTonnesPerYear(recycledPlasticWithEPR);
        
        Double additionalRecycling = recycledPlasticWithEPR - recycledPlasticWithoutEPR;
        mitigation.setAdditionalRecyclingVsBAUTonnesPerYear(additionalRecycling);
        
        Double ghgReductionTonnes = additionalRecycling * paramDto.getEmissionFactor();
        mitigation.setGhgReductionTonnes(ghgReductionTonnes);
        
        Double ghgReductionKilotonnes = ghgReductionTonnes / 1000.0;
        mitigation.setGhgReductionKilotonnes(ghgReductionKilotonnes);
        
        Double adjustedBauEmissionMitigation = bau.getValue() - ghgReductionKilotonnes;
        mitigation.setAdjustedBauEmissionMitigation(adjustedBauEmissionMitigation);
        
        EPRPlasticWasteMitigation saved = repository.save(mitigation);
        return toResponseDto(saved);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<EPRPlasticWasteMitigationResponseDto> getAllEPRPlasticWasteMitigation(Integer year) {
        Specification<EPRPlasticWasteMitigation> spec = Specification.where(hasYear(year));
        List<EPRPlasticWasteMitigation> mitigations = repository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
        return mitigations.stream()
                .map(this::toResponseDto)
                .toList();
    }
    
    @Override
    public void deleteEPRPlasticWasteMitigation(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("EPR Plastic Waste Mitigation record not found with id: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public byte[] generateExcelTemplate() {
        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("EPR Plastic Waste Mitigation");

            // Create styles (reusing pattern from other templates)
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
            titleCell.setCellValue("EPR Plastic Waste Mitigation Template");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

            rowIdx++; // Blank row

            // Create header row
            Row headerRow = sheet.createRow(rowIdx++);
            headerRow.setHeightInPoints(22);
            String[] headers = {
                    "Year",
                    "Plastic Waste (t/year)",
                    "Plastic Waste Unit",
                    "Project Intervention Name"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Get enum values for dropdowns
            String[] massPerYearUnitValues = Arrays.stream(MassPerYearUnit.values())
                    .map(Enum::name)
                    .toArray(String[]::new);

            // Create data validation helper
            DataValidationHelper validationHelper = sheet.getDataValidationHelper();

            // Data validation for Plastic Waste Unit column (Column C, index 2)
            CellRangeAddressList plasticWasteUnitList = new CellRangeAddressList(3, 1000, 2, 2);
            DataValidationConstraint plasticWasteUnitConstraint = validationHelper
                    .createExplicitListConstraint(massPerYearUnitValues);
            DataValidation plasticWasteUnitValidation = validationHelper.createValidation(plasticWasteUnitConstraint, plasticWasteUnitList);
            plasticWasteUnitValidation.setShowErrorBox(true);
            plasticWasteUnitValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
            plasticWasteUnitValidation.createErrorBox("Invalid Unit", "Please select a valid unit from the dropdown list.");
            plasticWasteUnitValidation.setShowPromptBox(true);
            plasticWasteUnitValidation.createPromptBox("Plastic Waste Unit", "Select a unit from the dropdown list.");
            sheet.addValidationData(plasticWasteUnitValidation);

            // Create example data rows
            Object[] exampleData1 = {2024, 10000.0, "TONNES_PER_YEAR", "EPR Implementation"};
            Object[] exampleData2 = {2025, 10500.0, "TONNES_PER_YEAR", "EPR Implementation"};

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
    public Map<String, Object> createEPRPlasticWasteMitigationFromExcel(MultipartFile file) {
        List<EPRPlasticWasteMitigation> savedRecords = new ArrayList<>();
        List<Integer> skippedYears = new ArrayList<>();
        int totalProcessed = 0;

        try {
            List<EPRPlasticWasteMitigationDto> dtos = ExcelReader.readExcel(
                    file.getInputStream(),
                    EPRPlasticWasteMitigationDto.class,
                    ExcelType.EPR_PLASTIC_WASTE_MITIGATION);

            for (int i = 0; i < dtos.size(); i++) {
                EPRPlasticWasteMitigationDto dto = dtos.get(i);
                totalProcessed++;
                int actualRowNumber = i + 1 + 3;

                // Validate required fields
                List<String> missingFields = new ArrayList<>();
                if (dto.getYear() == null) missingFields.add("Year");
                if (dto.getPlasticWasteTonnesPerYear() == null) missingFields.add("Plastic Waste (t/year)");
                if (dto.getPlasticWasteTonnesPerYearUnit() == null) missingFields.add("Plastic Waste Unit");
                if (dto.getProjectInterventionId() == null && dto.getProjectInterventionName() == null) {
                    missingFields.add("Project Intervention");
                }

                if (!missingFields.isEmpty()) {
                    throw new RuntimeException(String.format(
                            "Row %d: Missing required fields: %s. Please fill in all required fields in your Excel file.",
                            actualRowNumber, String.join(", ", missingFields)));
                }

                // Handle intervention name from Excel (if provided instead of ID)
                if (dto.getProjectInterventionId() == null && dto.getProjectInterventionName() != null) {
                    Optional<Intervention> interventionOpt = interventionRepository.findByNameIgnoreCase(dto.getProjectInterventionName());
                    if (interventionOpt.isEmpty()) {
                        throw new RuntimeException(String.format(
                                "Row %d: Intervention with name '%s' not found. Please create the intervention first or use a valid intervention name.",
                                actualRowNumber, dto.getProjectInterventionName()));
                    }
                    dto.setProjectInterventionId(interventionOpt.get().getId());
                }

                // Check if year already exists
                if (repository.findByYear(dto.getYear()).isPresent()) {
                    skippedYears.add(dto.getYear());
                    continue;
                }

                // Create the record
                try {
                    EPRPlasticWasteMitigation saved = createEPRPlasticWasteMitigationInternal(dto);
                    savedRecords.add(saved);
                } catch (RuntimeException e) {
                    throw new RuntimeException(String.format("Row %d: %s", actualRowNumber, e.getMessage()));
                }
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

    // Helper methods for styles (to reduce code duplication)
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
