package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.zeroTillage.service;

import com.navyn.emissionlog.Enums.ExcelType;
import com.navyn.emissionlog.Enums.Metrics.AreaUnits;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.zeroTillage.dtos.ZeroTillageMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.zeroTillage.dtos.ZeroTillageMitigationResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.zeroTillage.dtos.ZeroTillageParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.zeroTillage.models.ZeroTillageMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.zeroTillage.repositories.ZeroTillageMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.enums.ESector;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.models.BAU;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.repositories.BAURepository;
import com.navyn.emissionlog.modules.mitigationProjects.intervention.Intervention;
import com.navyn.emissionlog.modules.mitigationProjects.intervention.repositories.InterventionRepository;
import com.navyn.emissionlog.utils.ExcelReader;
import com.navyn.emissionlog.utils.Specifications.MitigationSpecifications;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
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
import java.util.*;

@Service
@RequiredArgsConstructor
public class ZeroTillageMitigationServiceImpl implements ZeroTillageMitigationService {

    private final ZeroTillageMitigationRepository repository;
    private final InterventionRepository interventionRepository;
    private final BAURepository bauRepository;
    private final ZeroTillageParameterService zeroTillageParameterService;

    /**
     * Maps ZeroTillageMitigation entity to Response DTO
     * This method loads intervention data within the transaction to avoid lazy loading issues
     */
    private ZeroTillageMitigationResponseDto toResponseDto(ZeroTillageMitigation mitigation) {
        ZeroTillageMitigationResponseDto dto = new ZeroTillageMitigationResponseDto();
        dto.setId(mitigation.getId());
        dto.setYear(mitigation.getYear());
        dto.setAreaUnderZeroTillage(mitigation.getAreaUnderZeroTillage());
        dto.setTotalCarbonIncreaseInSoil(mitigation.getTotalCarbonIncreaseInSoil());
        dto.setEmissionsSavings(mitigation.getEmissionsSavings());
        dto.setUreaApplied(mitigation.getUreaApplied());
        dto.setEmissionsFromUrea(mitigation.getEmissionsFromUrea());
        dto.setGhgEmissionsSavings(mitigation.getGhgEmissionsSavings());
        dto.setAdjustmentMitigation(mitigation.getAdjustmentMitigation());
        dto.setCreatedAt(mitigation.getCreatedAt());
        dto.setUpdatedAt(mitigation.getUpdatedAt());

        // Map intervention - FORCE initialization within transaction to avoid lazy loading
        if (mitigation.getIntervention() != null) {
            // Force Hibernate to initialize the proxy while session is still open
            Hibernate.initialize(mitigation.getIntervention());
            Intervention intervention = mitigation.getIntervention();
            ZeroTillageMitigationResponseDto.InterventionInfo interventionInfo =
                    new ZeroTillageMitigationResponseDto.InterventionInfo(
                            intervention.getId(),
                            intervention.getName()
                    );
            dto.setIntervention(interventionInfo);
        } else {
            dto.setIntervention(null);
        }

        return dto;
    }

    @Override
    @Transactional
    public ZeroTillageMitigationResponseDto createZeroTillageMitigation(ZeroTillageMitigationDto dto) {
        // Fetch latest active parameter - throws exception if none exists
        ZeroTillageParameterResponseDto param;
        try {
            param = zeroTillageParameterService.getLatestActive();
        } catch (RuntimeException e) {
            throw new RuntimeException(
                    "Cannot create Zero Tillage Mitigation: No active Zero Tillage Parameter found. " +
                            "Please create an active parameter first before creating mitigation records.",
                    e
            );
        }

        ZeroTillageMitigation mitigation = new ZeroTillageMitigation();
        // Convert area to hectares (standard unit)
        double areaInHectares = dto.getAreaUnit().toHectares(dto.getAreaUnderZeroTillage());

        // Map input fields (store in standard units)
        mitigation.setYear(dto.getYear());
        mitigation.setAreaUnderZeroTillage(areaInHectares);

        // 1. Calculate Total Carbon Increase in Soil (Tonnes C)
        // Total carbon = Area × Carbon increase in soil (from parameter)
        double totalCarbon = areaInHectares * param.getCarbonIncreaseInSoil();
        mitigation.setTotalCarbonIncreaseInSoil(totalCarbon);

        // 2. Calculate Emissions Savings (Kilotonnes CO2e)
        // Emissions savings = Total carbon × C to CO2 conversion (from parameter) / 1000
        double emissionsSavings = (totalCarbon * param.getCarbonToC02()) / 1000.0;
        mitigation.setEmissionsSavings(emissionsSavings);

        mitigation.setUreaApplied(dto.getUreaApplied());

        // 4. Calculate Emissions from Urea (Tonnes CO2)
        // Emissions from urea = Urea applied × Emission factor from urea (from parameter)
        double emissionsFromUrea = dto.getUreaApplied() * param.getEmissionFactorFromUrea();
        mitigation.setEmissionsFromUrea(emissionsFromUrea);

        // 5. Calculate GHG Emissions Savings (Kilotonnes CO2e) - NET
        // GHG savings = Emissions savings - (Emissions from urea / 1000)
        double ghgSavings = emissionsSavings - (emissionsFromUrea / 1000.0);
        mitigation.setGhgEmissionsSavings(ghgSavings);

        // 6. Calculate Adjustment Mitigation (Kilotonnes CO2)
        // Adjustment Mitigation = BAU.value - ghgEmissionsSavings
        // Find BAU record for AFOLU sector and same year
        BAU bau = bauRepository.findByYearAndSector(mitigation.getYear(), ESector.AFOLU)
                .orElseThrow(() -> new RuntimeException(
                        String.format("BAU record for AFOLU sector and year %d not found. Please create a BAU record first.",
                                mitigation.getYear())
                ));
        double adjustmentMitigation = bau.getValue() - ghgSavings;
        mitigation.setAdjustmentMitigation(adjustmentMitigation);

        // Handle intervention if provided
        if (dto.getInterventionId() != null) {
            Intervention intervention = interventionRepository.findById(dto.getInterventionId())
                    .orElseThrow(() -> new RuntimeException("Intervention not found with id: " + dto.getInterventionId()));
            mitigation.setIntervention(intervention);
        } else {
            mitigation.setIntervention(null);
        }

        ZeroTillageMitigation saved = repository.save(mitigation);
        return toResponseDto(saved);
    }

    @Override
    @Transactional
    public ZeroTillageMitigationResponseDto updateZeroTillageMitigation(UUID id, ZeroTillageMitigationDto dto) {
        // Fetch latest active parameter - throws exception if none exists
        ZeroTillageParameterResponseDto param;
        try {
            param = zeroTillageParameterService.getLatestActive();
        } catch (RuntimeException e) {
            throw new RuntimeException(
                    "Cannot update Zero Tillage Mitigation: No active Zero Tillage Parameter found. " +
                            "Please create an active parameter first before updating mitigation records.",
                    e
            );
        }

        ZeroTillageMitigation mitigation = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zero Tillage Mitigation record not found with id: " + id));

        // Convert area to hectares (standard unit)
        double areaInHectares = dto.getAreaUnit().toHectares(dto.getAreaUnderZeroTillage());

        // Update input fields (store in standard units)
        mitigation.setYear(dto.getYear());
        mitigation.setAreaUnderZeroTillage(areaInHectares);

        // Recalculate all derived fields using parameter values
        // 1. Calculate Total Carbon Increase in Soil (Tonnes C)
        // Total carbon = Area × Carbon increase in soil (from parameter)
        double totalCarbon = areaInHectares * param.getCarbonIncreaseInSoil();
        mitigation.setTotalCarbonIncreaseInSoil(totalCarbon);

        // 2. Calculate Emissions Savings (Kilotonnes CO2e)
        // Emissions savings = Total carbon × C to CO2 conversion (from parameter) / 1000
        double emissionsSavings = (totalCarbon * param.getCarbonToC02()) / 1000.0;
        mitigation.setEmissionsSavings(emissionsSavings);

        mitigation.setUreaApplied(dto.getUreaApplied());

        // 4. Calculate Emissions from Urea (Tonnes CO2)
        // Emissions from urea = Urea applied × Emission factor from urea (from parameter)
        double emissionsFromUrea = dto.getUreaApplied() * param.getEmissionFactorFromUrea();
        mitigation.setEmissionsFromUrea(emissionsFromUrea);

        // 5. Calculate GHG Emissions Savings (Kilotonnes CO2e) - NET
        // GHG savings = Emissions savings - (Emissions from urea / 1000)
        double ghgSavings = emissionsSavings - (emissionsFromUrea / 1000.0);
        mitigation.setGhgEmissionsSavings(ghgSavings);

        // 6. Calculate Adjustment Mitigation (Kilotons CO2)
        // Adjustment Mitigation = BAU.value - ghgEmissionsSavings
        // Find BAU record for AFOLU sector and same year
        BAU bau = bauRepository.findByYearAndSector(mitigation.getYear(), ESector.AFOLU)
                .orElseThrow(() -> new RuntimeException(
                        String.format("BAU record for AFOLU sector and year %d not found. Please create a BAU record first.",
                                mitigation.getYear())
                ));
        double adjustmentMitigation = bau.getValue() - ghgSavings;
        mitigation.setAdjustmentMitigation(adjustmentMitigation);

        // Handle intervention if provided
        if (dto.getInterventionId() != null) {
            Intervention intervention = interventionRepository.findById(dto.getInterventionId())
                    .orElseThrow(() -> new RuntimeException("Intervention not found with id: " + dto.getInterventionId()));
            mitigation.setIntervention(intervention);
        } else {
            mitigation.setIntervention(null);
        }

        ZeroTillageMitigation saved = repository.save(mitigation);
        return toResponseDto(saved);
    }

    @Override
    public void deleteZeroTillageMitigation(UUID id) {
        ZeroTillageMitigation mitigation = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zero Tillage Mitigation record not found with id: " + id));
        repository.delete(mitigation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ZeroTillageMitigationResponseDto> getAllZeroTillageMitigation(Integer year) {
        Specification<ZeroTillageMitigation> spec = Specification
                .<ZeroTillageMitigation>where(MitigationSpecifications.hasYear(year));
        // Use EntityGraph (via findAll override) to eagerly fetch interventions to avoid N+1 queries and lazy loading issues
        List<ZeroTillageMitigation> mitigations = repository.findAll(spec, Sort.by(Sort.Direction.DESC, "year"));
        // Map each entity to DTO within the transaction to avoid lazy loading issues
        return mitigations.stream()
                .map(this::toResponseDto)
                .toList();
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
        List<ZeroTillageMitigationResponseDto> savedRecords = new ArrayList<>();
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
                ZeroTillageMitigationResponseDto saved = createZeroTillageMitigation(dto);
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
