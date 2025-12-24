package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.service;

import com.navyn.emissionlog.Enums.ExcelType;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.constants.AddingStrawConstants;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.dtos.AddingStrawMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.models.AddingStrawMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.repository.AddingStrawMitigationRepository;
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
public class AddingStrawMitigationServiceImpl implements AddingStrawMitigationService {
    
    private final AddingStrawMitigationRepository repository;
    
    @Override
    public AddingStrawMitigation createAddingStrawMitigation(AddingStrawMitigationDto dto) {
        AddingStrawMitigation mitigation = new AddingStrawMitigation();
        
        // Set user inputs
        mitigation.setYear(dto.getYear());
        mitigation.setNumberOfCows(dto.getNumberOfCows());
        
        // Calculations for CH4 Reduction (Adding Straw)
        // 1. CH4 emissions if no mitigation (tonnes CO2e) = CH4_EMISSIONS_PER_COW × numberOfCows
        Double ch4EmissionsStraw = AddingStrawConstants.CH4_EMISSIONS_PER_COW_ADDING_STRAW.getValue() * dto.getNumberOfCows();
        mitigation.setCh4EmissionsAddingStraw(ch4EmissionsStraw);
        
        // 2. CH4 reduction (30%) = ch4EmissionsStraw × CH4_REDUCTION_RATE_STRAW
        Double ch4ReductionStraw = ch4EmissionsStraw * AddingStrawConstants.CH4_REDUCTION_RATE_STRAW.getValue();
        mitigation.setCh4ReductionAddingStraw(ch4ReductionStraw);
        
        // 3. Mitigated CH4 emissions (ktCO2e/year) = ch4ReductionStraw / 1000
        Double mitigatedCh4Kilotonnes = ch4ReductionStraw / 1000.0;
        mitigation.setMitigatedCh4EmissionsKilotonnes(mitigatedCh4Kilotonnes);
        
        return repository.save(mitigation);
    }

    @Override
    public AddingStrawMitigation updateAddingStrawMitigation(UUID id, AddingStrawMitigationDto dto) {
        AddingStrawMitigation mitigation = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Adding Straw Mitigation record not found with id: " + id));

        mitigation.setYear(dto.getYear());
        mitigation.setNumberOfCows(dto.getNumberOfCows());

        Double ch4EmissionsStraw = AddingStrawConstants.CH4_EMISSIONS_PER_COW_ADDING_STRAW.getValue()
            * dto.getNumberOfCows();
        mitigation.setCh4EmissionsAddingStraw(ch4EmissionsStraw);

        Double ch4ReductionStraw = ch4EmissionsStraw
            * AddingStrawConstants.CH4_REDUCTION_RATE_STRAW.getValue();
        mitigation.setCh4ReductionAddingStraw(ch4ReductionStraw);

        Double mitigatedCh4Kilotonnes = ch4ReductionStraw / 1000.0;
        mitigation.setMitigatedCh4EmissionsKilotonnes(mitigatedCh4Kilotonnes);

        return repository.save(mitigation);
    }

    @Override
    public void deleteAddingStrawMitigation(UUID id) {
        AddingStrawMitigation mitigation = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Adding Straw Mitigation record not found with id: " + id));
        repository.delete(mitigation);
    }
    
    @Override
    public List<AddingStrawMitigation> getAllAddingStrawMitigation(Integer year) {
        Specification<AddingStrawMitigation> spec = Specification.where(hasYear(year));
        return repository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
    }

    @Override
    public byte[] generateExcelTemplate() {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Adding Straw Mitigation");

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
            titleCell.setCellValue("Adding Straw Mitigation Template");
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
    public Map<String, Object> createAddingStrawMitigationFromExcel(MultipartFile file) {
        List<AddingStrawMitigation> savedRecords = new ArrayList<>();
        List<Integer> skippedYears = new ArrayList<>();
        int totalProcessed = 0;

        try {
            List<AddingStrawMitigationDto> dtos = ExcelReader.readExcel(
                    file.getInputStream(),
                    AddingStrawMitigationDto.class,
                    ExcelType.ADDING_STRAW_MITIGATION);

            for (int i = 0; i < dtos.size(); i++) {
                AddingStrawMitigationDto dto = dtos.get(i);
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
                AddingStrawMitigation saved = createAddingStrawMitigation(dto);
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
