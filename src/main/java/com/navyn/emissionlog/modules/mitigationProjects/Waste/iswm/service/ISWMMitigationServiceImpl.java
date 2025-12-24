package com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.service;

import com.navyn.emissionlog.Enums.ExcelType;
import com.navyn.emissionlog.Enums.Metrics.EmissionsUnit;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.dtos.ISWMMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.models.ISWMMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.repository.ISWMMitigationRepository;
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
public class ISWMMitigationServiceImpl implements ISWMMitigationService {
    
    private final ISWMMitigationRepository repository;
    
    @Override
    public ISWMMitigation createISWMMitigation(ISWMMitigationDto dto) {
        ISWMMitigation mitigation = new ISWMMitigation();
        
        // Convert BAU Emission to standard units (tCO₂e)
        double bauEmissionInTonnes = dto.getBauEmissionUnit().toKiloTonnesCO2e(dto.getBauEmission());
        
        // Set user inputs
        mitigation.setYear(dto.getYear());
        mitigation.setWasteProcessed(dto.getWasteProcessed());
        mitigation.setDegradableOrganicFraction(dto.getDegradableOrganicFraction());
        mitigation.setLandfillAvoidance(dto.getLandfillAvoidance());
        mitigation.setCompostingEF(dto.getCompostingEF());
        mitigation.setBauEmission(bauEmissionInTonnes);
        
        // Calculations
        // DOFDiverted = wasteProcessed * %DegradableOrganicFraction
        Double dofDiverted = dto.getWasteProcessed() * (dto.getDegradableOrganicFraction() / 100.0);
        mitigation.setDofDiverted(dofDiverted);
        
        // AvoidedLandfill = wasteProcessed * LandfillAvoidance
        Double avoidedLandfill = dto.getWasteProcessed() * dto.getLandfillAvoidance();
        mitigation.setAvoidedLandfill(avoidedLandfill);
        
        // CompostingEmissions = DOFDiverted * CompostingEF
        Double compostingEmissions = dofDiverted * dto.getCompostingEF();
        mitigation.setCompostingEmissions(compostingEmissions);
        
        // NetAnnualReduction = (AvoidedLandfill - CompostingEmissions) / 1000
        Double netAnnualReduction = (avoidedLandfill - compostingEmissions) / 1000.0;
        mitigation.setNetAnnualReduction(netAnnualReduction);
        
        // MitigationScenarioEmission = BauEmission - NetAnnualReduction
        Double mitigationScenarioEmission = bauEmissionInTonnes - netAnnualReduction;
        mitigation.setMitigationScenarioEmission(mitigationScenarioEmission);
        
        return repository.save(mitigation);
    }
    
    @Override
    public ISWMMitigation updateISWMMitigation(UUID id, ISWMMitigationDto dto) {
        ISWMMitigation mitigation = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("ISWM Mitigation record not found with id: " + id));
        
        // Convert BAU Emission to standard units (tCO₂e)
        double bauEmissionInTonnes = dto.getBauEmissionUnit().toKiloTonnesCO2e(dto.getBauEmission());
        
        // Update user inputs
        mitigation.setYear(dto.getYear());
        mitigation.setWasteProcessed(dto.getWasteProcessed());
        mitigation.setDegradableOrganicFraction(dto.getDegradableOrganicFraction());
        mitigation.setLandfillAvoidance(dto.getLandfillAvoidance());
        mitigation.setCompostingEF(dto.getCompostingEF());
        mitigation.setBauEmission(bauEmissionInTonnes);
        
        // Recalculate all derived fields
        // DOFDiverted = wasteProcessed * %DegradableOrganicFraction
        Double dofDiverted = dto.getWasteProcessed() * (dto.getDegradableOrganicFraction() / 100.0);
        mitigation.setDofDiverted(dofDiverted);
        
        // AvoidedLandfill = wasteProcessed * LandfillAvoidance
        Double avoidedLandfill = dto.getWasteProcessed() * dto.getLandfillAvoidance();
        mitigation.setAvoidedLandfill(avoidedLandfill);
        
        // CompostingEmissions = DOFDiverted * CompostingEF
        Double compostingEmissions = dofDiverted * dto.getCompostingEF();
        mitigation.setCompostingEmissions(compostingEmissions);
        
        // NetAnnualReduction = (AvoidedLandfill - CompostingEmissions) / 1000
        Double netAnnualReduction = (avoidedLandfill - compostingEmissions) / 1000.0;
        mitigation.setNetAnnualReduction(netAnnualReduction);
        
        // MitigationScenarioEmission = BauEmission - NetAnnualReduction
        Double mitigationScenarioEmission = bauEmissionInTonnes - netAnnualReduction;
        mitigation.setMitigationScenarioEmission(mitigationScenarioEmission);
        
        return repository.save(mitigation);
    }
    
    @Override
    public List<ISWMMitigation> getAllISWMMitigation(Integer year) {
        Specification<ISWMMitigation> spec = Specification.where(hasYear(year));
        return repository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
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
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

            rowIdx++; // Blank row

            // Create header row
            Row headerRow = sheet.createRow(rowIdx++);
            headerRow.setHeightInPoints(22);
            String[] headers = {
                    "Year",
                    "Waste Processed",
                    "Degradable Organic Fraction",
                    "Landfill Avoidance",
                    "Composting Emission Factor",
                    "BAU Emission",
                    "BAU Emission Unit"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Get enum values for dropdowns
            String[] emissionsUnitValues = Arrays.stream(EmissionsUnit.values())
                    .map(Enum::name)
                    .toArray(String[]::new);

            // Create data validation helper
            DataValidationHelper validationHelper = sheet.getDataValidationHelper();

            // Data validation for BAU Emission Unit column (Column G, index 6)
            CellRangeAddressList bauUnitList = new CellRangeAddressList(3, 1000, 6, 6);
            DataValidationConstraint bauUnitConstraint = validationHelper
                    .createExplicitListConstraint(emissionsUnitValues);
            DataValidation bauUnitValidation = validationHelper.createValidation(bauUnitConstraint, bauUnitList);
            bauUnitValidation.setShowErrorBox(true);
            bauUnitValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
            bauUnitValidation.createErrorBox("Invalid Unit", "Please select a valid unit from the dropdown list.");
            bauUnitValidation.setShowPromptBox(true);
            bauUnitValidation.createPromptBox("BAU Emission Unit", "Select a unit from the dropdown list.");
            sheet.addValidationData(bauUnitValidation);

            // Create example data rows
            Object[] exampleData1 = {2024, 1000.0, 50.0, 500.0, 50.0, 100.0, "TONNES_CO2E"};
            Object[] exampleData2 = {2025, 1100.0, 52.0, 550.0, 52.0, 110.0, "TONNES_CO2E"};

            // First example row
            Row exampleRow1 = sheet.createRow(rowIdx++);
            exampleRow1.setHeightInPoints(18);
            for (int i = 0; i < exampleData1.length; i++) {
                Cell cell = exampleRow1.createCell(i);
                if (i == 0) {
                    cell.setCellStyle(yearStyle);
                    cell.setCellValue(((Number) exampleData1[i]).intValue());
                } else if (i == 6) {
                    cell.setCellStyle(dataStyle);
                    cell.setCellValue((String) exampleData1[i]);
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
                } else if (i == 6) {
                    cell.setCellStyle(alternateDataStyle);
                    cell.setCellValue((String) exampleData2[i]);
                } else {
                    CellStyle altNumStyle = workbook.createCellStyle();
                    altNumStyle.cloneStyleFrom(numberStyle);
                    altNumStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                    altNumStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    cell.setCellStyle(altNumStyle);
                    cell.setCellValue(((Number) exampleData2[i]).doubleValue());
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
                if (dto.getDegradableOrganicFraction() == null) missingFields.add("Degradable Organic Fraction");
                if (dto.getLandfillAvoidance() == null) missingFields.add("Landfill Avoidance");
                if (dto.getCompostingEF() == null) missingFields.add("Composting Emission Factor");
                if (dto.getBauEmission() == null) missingFields.add("BAU Emission");
                if (dto.getBauEmissionUnit() == null) missingFields.add("BAU Emission Unit");

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
                ISWMMitigation saved = createISWMMitigation(dto);
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
