package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.service;

import com.navyn.emissionlog.Enums.ExcelType;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.constants.ManureCoveringConstants;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.dtos.ManureCoveringMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.models.ManureCoveringMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.repository.ManureCoveringMitigationRepository;
import com.navyn.emissionlog.utils.ExcelReader;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

import static com.navyn.emissionlog.utils.Specifications.MitigationSpecifications.hasYear;

@Service
@RequiredArgsConstructor
public class ManureCoveringMitigationServiceImpl implements ManureCoveringMitigationService {
    
    private final ManureCoveringMitigationRepository repository;
    
    @Override
    public ManureCoveringMitigation createManureCoveringMitigation(ManureCoveringMitigationDto dto) {
        ManureCoveringMitigation mitigation = new ManureCoveringMitigation();
        
        // Set user inputs
        mitigation.setYear(dto.getYear());
        mitigation.setNumberOfCows(dto.getNumberOfCows());
        
        // Calculations for N2O Reduction (Compaction and Manure Covering)
        // 1. N2O emissions (tonnes CO2e/year) = N2O_EMISSIONS_PER_COW × numberOfCows
        Double n2oEmissions = ManureCoveringConstants.N2O_EMISSIONS_PER_COW.getValue() * dto.getNumberOfCows();
        mitigation.setN2oEmissions(n2oEmissions);
        
        // 2. N2O reduction (30%) = n2oEmissions × N2O_REDUCTION_RATE
        Double n2oReduction = n2oEmissions * ManureCoveringConstants.N2O_REDUCTION_RATE.getValue();
        mitigation.setN2oReduction(n2oReduction);
        
        // 3. Mitigated N2O emissions (ktCO2e/year) = n2oReduction / 1000
        Double mitigatedN2oKilotonnes = n2oReduction / 1000.0;
        mitigation.setMitigatedN2oEmissionsKilotonnes(mitigatedN2oKilotonnes);
        
        return repository.save(mitigation);
    }

    @Override
    public ManureCoveringMitigation updateManureCoveringMitigation(UUID id, ManureCoveringMitigationDto dto) {
        ManureCoveringMitigation mitigation = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Manure Covering Mitigation record not found with id: " + id));

        mitigation.setYear(dto.getYear());
        mitigation.setNumberOfCows(dto.getNumberOfCows());

        Double n2oEmissions = ManureCoveringConstants.N2O_EMISSIONS_PER_COW.getValue()
            * dto.getNumberOfCows();
        mitigation.setN2oEmissions(n2oEmissions);

        Double n2oReduction = n2oEmissions * ManureCoveringConstants.N2O_REDUCTION_RATE.getValue();
        mitigation.setN2oReduction(n2oReduction);

        Double mitigatedN2oKilotonnes = n2oReduction / 1000.0;
        mitigation.setMitigatedN2oEmissionsKilotonnes(mitigatedN2oKilotonnes);

        return repository.save(mitigation);
    }

    @Override
    public void deleteManureCoveringMitigation(UUID id) {
        ManureCoveringMitigation mitigation = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Manure Covering Mitigation record not found with id: " + id));
        repository.delete(mitigation);
    }
    
    @Override
    public List<ManureCoveringMitigation> getAllManureCoveringMitigation(Integer year) {
        Specification<ManureCoveringMitigation> spec = Specification.where(hasYear(year));
        return repository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
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

            // Title row
            Row titleRow = sheet.createRow(rowIdx++);
            titleRow.setHeightInPoints(30);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Manure Covering Mitigation Template");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));

            rowIdx++; // Blank row

            // Create header row
            Row headerRow = sheet.createRow(rowIdx++);
            headerRow.setHeightInPoints(22);
            String[] headers = {"Year", "Number of Cows"};

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Create example data rows
            Object[] exampleData1 = {2024, 100};
            Object[] exampleData2 = {2025, 150};

            // First example row
            Row exampleRow1 = sheet.createRow(rowIdx++);
            exampleRow1.setHeightInPoints(18);
            for (int i = 0; i < exampleData1.length; i++) {
                Cell cell = exampleRow1.createCell(i);
                if (i == 0) { // Year
                    cell.setCellStyle(yearStyle);
                    cell.setCellValue(((Number) exampleData1[i]).intValue());
                } else { // Number of Cows
                    cell.setCellStyle(numberStyle);
                    cell.setCellValue(((Number) exampleData1[i]).intValue());
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
                } else { // Number of Cows
                    CellStyle altNumStyle = workbook.createCellStyle();
                    altNumStyle.cloneStyleFrom(numberStyle);
                    altNumStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                    altNumStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    cell.setCellStyle(altNumStyle);
                    cell.setCellValue(((Number) exampleData2[i]).intValue());
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
    public Map<String, Object> createManureCoveringMitigationFromExcel(MultipartFile file) {
        List<ManureCoveringMitigation> savedRecords = new ArrayList<>();
        List<Integer> skippedYears = new ArrayList<>();
        int totalProcessed = 0;

        try {
            List<ManureCoveringMitigationDto> dtos = ExcelReader.readExcel(
                    file.getInputStream(),
                    ManureCoveringMitigationDto.class,
                    ExcelType.MANURE_COVERING_MITIGATION);

            for (int i = 0; i < dtos.size(); i++) {
                ManureCoveringMitigationDto dto = dtos.get(i);
                totalProcessed++;

                // Validate required fields
                List<String> missingFields = new ArrayList<>();
                if (dto.getYear() == null) {
                    missingFields.add("Year");
                }
                if (dto.getNumberOfCows() == null) {
                    missingFields.add("Number of Cows");
                }

                if (!missingFields.isEmpty()) {
                    throw new RuntimeException(String.format(
                            "Missing required fields: %s. Please fill in all required fields in your Excel file.",
                            String.join(", ", missingFields)));
                }

                // Check if year already exists
                if (repository.findByYear(dto.getYear()).isPresent()) {
                    skippedYears.add(dto.getYear());
                    continue; // Skip this row
                }

                // Create the record
                ManureCoveringMitigation saved = createManureCoveringMitigation(dto);
                savedRecords.add(saved);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("saved", savedRecords);
            result.put("savedCount", savedRecords.size());
            result.put("skippedCount", skippedYears.size());
            result.put("skippedYears", skippedYears);
            result.put("totalProcessed", totalProcessed);

            return result;
        } catch (IOException e) {
            String message = e.getMessage();
            if (message != null) {
                throw new RuntimeException(message, e);
            } else {
                throw new RuntimeException("Incorrect template. Please download the correct template and try again.", e);
            }
        }
    }
}
