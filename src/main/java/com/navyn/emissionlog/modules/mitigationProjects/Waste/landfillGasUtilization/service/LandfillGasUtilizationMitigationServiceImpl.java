package com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.service;

import com.navyn.emissionlog.Enums.ExcelType;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.enums.ESector;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.models.BAU;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.services.BAUService;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.dtos.LandfillGasUtilizationMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.models.LandfillGasParameter;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.models.LandfillGasUtilizationMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.repository.LandfillGasParameterRepository;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.repository.LandfillGasUtilizationMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.intervention.Intervention;
import com.navyn.emissionlog.modules.mitigationProjects.intervention.repositories.InterventionRepository;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.navyn.emissionlog.utils.Specifications.MitigationSpecifications.hasYear;

@Service
@RequiredArgsConstructor
public class LandfillGasUtilizationMitigationServiceImpl implements LandfillGasUtilizationMitigationService {

    private final LandfillGasUtilizationMitigationRepository repository;
    private final LandfillGasParameterRepository parameterRepository;
    private final InterventionRepository interventionRepository;
    private final BAUService bauService;

    @Override
    public LandfillGasUtilizationMitigation createLandfillGasUtilizationMitigation(LandfillGasUtilizationMitigationDto dto) {
        LandfillGasUtilizationMitigation mitigation = new LandfillGasUtilizationMitigation();

        // Get LandfillGasParameter (latest)
        LandfillGasParameter parameter = parameterRepository.findFirstByOrderByCreatedAtDesc()
                .orElseThrow(() -> new RuntimeException("Landfill Gas Parameter not found. Please create parameters first."));

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
        mitigation.setCh4Captured(dto.getCh4Captured());
        mitigation.setProjectIntervention(intervention);

        // Calculate CH₄Destroyed = CH₄Captured * DestructionEfficiency(%)
        // Note: DestructionEfficiency is a percentage (0-100), so divide by 100
        Double ch4Destroyed = dto.getCh4Captured() * (parameter.getDestructionEfficiencyPercentage() / 100.0);
        mitigation.setCh4Destroyed(ch4Destroyed);

        // Calculate EquivalentCO₂eReduction = CH₄Destroyed * GlobalWarmingPotential(CH₄)
        Double equivalentCO2eReduction = ch4Destroyed * parameter.getGlobalWarmingPotentialCh4();
        mitigation.setEquivalentCO2eReduction(equivalentCO2eReduction);

        // Calculate MitigationScenarioGrand = BAU - EquivalentCO₂eReduction
        Double mitigationScenarioGrand = bau.getValue() - equivalentCO2eReduction;
        mitigation.setMitigationScenarioGrand(mitigationScenarioGrand);

        return repository.save(mitigation);
    }

    @Override
    public LandfillGasUtilizationMitigation updateLandfillGasUtilizationMitigation(UUID id, LandfillGasUtilizationMitigationDto dto) {
        LandfillGasUtilizationMitigation mitigation = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Landfill Gas Utilization Mitigation record not found with id: " + id));

        // Get LandfillGasParameter (latest)
        LandfillGasParameter parameter = parameterRepository.findFirstByOrderByCreatedAtDesc()
                .orElseThrow(() -> new RuntimeException("Landfill Gas Parameter not found. Please create parameters first."));

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
        mitigation.setCh4Captured(dto.getCh4Captured());
        mitigation.setProjectIntervention(intervention);

        // Recalculate derived fields
        // Calculate CH₄Destroyed = CH₄Captured * DestructionEfficiency(%)
        Double ch4Destroyed = dto.getCh4Captured() * (parameter.getDestructionEfficiencyPercentage() / 100.0);
        mitigation.setCh4Destroyed(ch4Destroyed);

        // Calculate EquivalentCO₂eReduction = CH₄Destroyed * GlobalWarmingPotential(CH₄)
        Double equivalentCO2eReduction = ch4Destroyed * parameter.getGlobalWarmingPotentialCh4();
        mitigation.setEquivalentCO2eReduction(equivalentCO2eReduction);

        // Calculate MitigationScenarioGrand = BAU - EquivalentCO₂eReduction
        Double mitigationScenarioGrand = bau.getValue() - equivalentCO2eReduction;
        mitigation.setMitigationScenarioGrand(mitigationScenarioGrand);

        return repository.save(mitigation);
    }

    @Override
    public List<LandfillGasUtilizationMitigation> getAllLandfillGasUtilizationMitigation(Integer year) {
        Specification<LandfillGasUtilizationMitigation> spec = Specification.where(hasYear(year));
        return repository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
    }

    @Override
    public void deleteLandfillGasUtilizationMitigation(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Landfill Gas Utilization Mitigation record not found with id: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public byte[] generateExcelTemplate() {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Landfill Gas Utilization");

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
            titleCell.setCellValue("Landfill Gas Utilization Mitigation Template");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));

            rowIdx++; // Blank row

            // Create header row
            Row headerRow = sheet.createRow(rowIdx++);
            headerRow.setHeightInPoints(22);
            String[] headers = {
                    "Year",
                    "CH₄ Captured",
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
                    2029,
                    100.0,
                    interventions.isEmpty() ? "Example Intervention" : interventions.get(0).getName()
            };

            Object[] exampleData2 = {
                    2030,
                    110.0,
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
                } else if (i == 1) { // CH₄ Captured (number)
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
                } else if (i == 1) { // CH₄ Captured (number)
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
    public Map<String, Object> createLandfillGasUtilizationMitigationFromExcel(MultipartFile file) {
        List<LandfillGasUtilizationMitigation> savedRecords = new ArrayList<>();
        List<Integer> skippedYears = new ArrayList<>();
        int totalProcessed = 0;

        try {
            List<LandfillGasUtilizationMitigationDto> dtos = ExcelReader.readExcel(
                    file.getInputStream(),
                    LandfillGasUtilizationMitigationDto.class,
                    ExcelType.LANDFILL_GAS_UTILIZATION_MITIGATION);

            for (int i = 0; i < dtos.size(); i++) {
                LandfillGasUtilizationMitigationDto dto = dtos.get(i);
                totalProcessed++;
                int actualRowNumber = i + 1 + 3; // +1 for 1-based, +3 for title(1) + blank(1) + header(1)

                // Validate required fields
                List<String> missingFields = new ArrayList<>();
                if (dto.getYear() == null) {
                    missingFields.add("Year");
                }
                if (dto.getCh4Captured() == null) {
                    missingFields.add("CH₄ Captured");
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
                LandfillGasUtilizationMitigation saved = createLandfillGasUtilizationMitigation(dto);
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
