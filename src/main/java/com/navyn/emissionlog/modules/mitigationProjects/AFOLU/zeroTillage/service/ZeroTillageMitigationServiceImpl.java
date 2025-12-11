package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.zeroTillage.service;

import com.navyn.emissionlog.Enums.ExcelType;
import com.navyn.emissionlog.Enums.Metrics.AreaUnits;
import com.navyn.emissionlog.Enums.Mitigation.ZeroTillageConstants;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.zeroTillage.dtos.ZeroTillageMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.zeroTillage.models.ZeroTillageMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.zeroTillage.repositories.ZeroTillageMitigationRepository;
import com.navyn.emissionlog.utils.ExcelReader;
import com.navyn.emissionlog.utils.Specifications.MitigationSpecifications;
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
import java.util.*;

@Service
@RequiredArgsConstructor
public class ZeroTillageMitigationServiceImpl implements ZeroTillageMitigationService {

    private final ZeroTillageMitigationRepository repository;

    @Override
    public ZeroTillageMitigation createZeroTillageMitigation(ZeroTillageMitigationDto dto) {
        ZeroTillageMitigation mitigation = new ZeroTillageMitigation();

        // Convert area to hectares (standard unit)
        double areaInHectares = dto.getAreaUnit().toHectares(dto.getAreaUnderZeroTillage());

        // Map input fields (store in standard units)
        mitigation.setYear(dto.getYear());
        mitigation.setAreaUnderZeroTillage(areaInHectares);

        // 1. Calculate Total Carbon Increase in Soil (Tonnes C)
        // Total carbon = Area × Carbon increase in soil
        double totalCarbon = areaInHectares *
                ZeroTillageConstants.CARBON_INCREASE_SOIL.getValue();
        mitigation.setTotalCarbonIncreaseInSoil(totalCarbon);

        // 2. Calculate Emissions Savings (Kilotonnes CO2e)
        // Emissions savings = Total carbon × C to CO2 conversion / 1000
        double emissionsSavings = (totalCarbon *
                ZeroTillageConstants.CONVERSION_C_TO_CO2.getValue()) / 1000.0;
        mitigation.setEmissionsSavings(emissionsSavings);

        // 3. Calculate Urea Applied (tonnes)
        // Urea applied = Area × Urea application rate
        double ureaApplied = areaInHectares *
                ZeroTillageConstants.UREA_APPLICATION_RATE.getValue();
        mitigation.setUreaApplied(ureaApplied);

        // 4. Calculate Emissions from Urea (Tonnes CO2)
        // Emissions from urea = Urea applied × Emission factor from urea
        double emissionsFromUrea = ureaApplied *
                ZeroTillageConstants.EMISSION_FACTOR_UREA.getValue();
        mitigation.setEmissionsFromUrea(emissionsFromUrea);

        // 5. Calculate GHG Emissions Savings (Kilotonnes CO2e) - NET
        // GHG savings = Emissions savings - (Emissions from urea / 1000)
        double ghgSavings = emissionsSavings - (emissionsFromUrea / 1000.0);
        mitigation.setGhgEmissionsSavings(ghgSavings);

        return repository.save(mitigation);
    }

    @Override
    public ZeroTillageMitigation updateZeroTillageMitigation(UUID id, ZeroTillageMitigationDto dto) {
        ZeroTillageMitigation mitigation = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zero Tillage Mitigation record not found with id: " + id));

        // Convert area to hectares (standard unit)
        double areaInHectares = dto.getAreaUnit().toHectares(dto.getAreaUnderZeroTillage());

        // Update input fields (store in standard units)
        mitigation.setYear(dto.getYear());
        mitigation.setAreaUnderZeroTillage(areaInHectares);

        // Recalculate all derived fields
        double totalCarbon = areaInHectares *
                ZeroTillageConstants.CARBON_INCREASE_SOIL.getValue();
        mitigation.setTotalCarbonIncreaseInSoil(totalCarbon);

        double emissionsSavings = (totalCarbon *
                ZeroTillageConstants.CONVERSION_C_TO_CO2.getValue()) / 1000.0;
        mitigation.setEmissionsSavings(emissionsSavings);

        double ureaApplied = areaInHectares *
                ZeroTillageConstants.UREA_APPLICATION_RATE.getValue();
        mitigation.setUreaApplied(ureaApplied);

        double emissionsFromUrea = ureaApplied *
                ZeroTillageConstants.EMISSION_FACTOR_UREA.getValue();
        mitigation.setEmissionsFromUrea(emissionsFromUrea);

        double ghgSavings = emissionsSavings - (emissionsFromUrea / 1000.0);
        mitigation.setGhgEmissionsSavings(ghgSavings);

        return repository.save(mitigation);
    }

    @Override
    public void deleteZeroTillageMitigation(UUID id) {
        ZeroTillageMitigation mitigation = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zero Tillage Mitigation record not found with id: " + id));
        repository.delete(mitigation);
    }

    @Override
    public List<ZeroTillageMitigation> getAllZeroTillageMitigation(Integer year) {
        Specification<ZeroTillageMitigation> spec = Specification
                .<ZeroTillageMitigation>where(MitigationSpecifications.hasYear(year));
        return repository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
    }

    @Override
    public Optional<ZeroTillageMitigation> getByYear(Integer year) {
        return repository.findByYear(year);
    }

    @Override
    public byte[] generateExcelTemplate() {
        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Zero Tillage Mitigation");

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
            titleCell.setCellValue("Zero Tillage Mitigation Template");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));

            rowIdx++; // Blank row

            // Create header row
            Row headerRow = sheet.createRow(rowIdx++);
            headerRow.setHeightInPoints(22);
            String[] headers = {
                    "Year",
                    "Area Under Zero Tillage",
                    "Area Unit"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Get AreaUnits enum values for dropdown
            String[] areaUnitValues = Arrays.stream(AreaUnits.values())
                    .map(Enum::name)
                    .toArray(String[]::new);

            // Create data validation for Area Unit column (Column C, index 2)
            DataValidationHelper validationHelper = sheet.getDataValidationHelper();
            CellRangeAddressList addressList = new CellRangeAddressList(
                    3, // Start row (first data row after headers)
                    1000, // End row (sufficient for most use cases)
                    2, 2 // Column C (Area Unit column, 0-indexed)
            );

            DataValidationConstraint constraint = validationHelper.createExplicitListConstraint(areaUnitValues);
            DataValidation validation = validationHelper.createValidation(constraint, addressList);
            validation.setShowErrorBox(true);
            validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
            validation.createErrorBox("Invalid Area Unit", "Please select a valid area unit from the dropdown list.");
            validation.setShowPromptBox(true);
            validation.createPromptBox("Area Unit", "Select an area unit from the dropdown list.");
            sheet.addValidationData(validation);

            // Create example data rows
            Object[] exampleData1 = {
                    2024,
                    100.5,
                    "HECTARES"
            };

            Object[] exampleData2 = {
                    2025,
                    150.75,
                    "ACRES"
            };

            // First example row
            Row exampleRow1 = sheet.createRow(rowIdx++);
            exampleRow1.setHeightInPoints(18);
            for (int i = 0; i < exampleData1.length; i++) {
                Cell cell = exampleRow1.createCell(i);
                if (i == 0) { // Year
                    cell.setCellStyle(yearStyle);
                    cell.setCellValue(((Number) exampleData1[i]).intValue());
                } else if (i == 1) { // Area
                    cell.setCellStyle(numberStyle);
                    cell.setCellValue(((Number) exampleData1[i]).doubleValue());
                } else { // Area Unit
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
                } else if (i == 1) { // Area
                    CellStyle altNumStyle = workbook.createCellStyle();
                    altNumStyle.cloneStyleFrom(numberStyle);
                    altNumStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                    altNumStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    cell.setCellStyle(altNumStyle);
                    cell.setCellValue(((Number) exampleData2[i]).doubleValue());
                } else { // Area Unit
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
    public Map<String, Object> createZeroTillageMitigationFromExcel(MultipartFile file) {
        List<ZeroTillageMitigation> savedRecords = new ArrayList<>();
        List<Integer> skippedYears = new ArrayList<>();
        int totalProcessed = 0;

        try {
            List<ZeroTillageMitigationDto> dtos = ExcelReader.readExcel(
                    file.getInputStream(),
                    ZeroTillageMitigationDto.class,
                    ExcelType.ZERO_TILLAGE_MITIGATION);

            for (int i = 0; i < dtos.size(); i++) {
                ZeroTillageMitigationDto dto = dtos.get(i);
                totalProcessed++;
                int rowNumber = i + 1; // Excel row number (1-based, accounting for header row)

                // Validate required fields
                List<String> missingFields = new ArrayList<>();
                if (dto.getYear() == null) {
                    missingFields.add("Year");
                }
                if (dto.getAreaUnderZeroTillage() == null) {
                    missingFields.add("Area Under Zero Tillage");
                }
                if (dto.getAreaUnit() == null) {
                    missingFields.add("Area Unit");
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
                ZeroTillageMitigation saved = createZeroTillageMitigation(dto);
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
            // Re-throw IOException with user-friendly message
            String message = e.getMessage();
            if (message != null) {
                throw new RuntimeException(message, e);
            } else {
                throw new RuntimeException("Incorrect template. Please download the correct template and try again.",
                        e);
            }
        } catch (NullPointerException e) {
            // Handle null pointer exceptions with clear message
            throw new RuntimeException(
                    "Missing required fields. Please fill in all required fields in your Excel file.", e);
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if (errorMsg != null) {
                throw new RuntimeException(errorMsg, e);
            }
            throw new RuntimeException("Error processing Excel file. Please check your file and try again.", e);
        }
    }
}
