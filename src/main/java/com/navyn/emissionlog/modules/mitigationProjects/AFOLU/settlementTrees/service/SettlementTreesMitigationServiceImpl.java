package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.settlementTrees.service;

import com.navyn.emissionlog.Enums.ExcelType;
import com.navyn.emissionlog.Enums.Metrics.VolumeUnits;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.settlementTrees.dtos.SettlementTreesMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.settlementTrees.dtos.SettlementTreesMitigationResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.settlementTrees.dtos.SettlementTreesParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.settlementTrees.models.SettlementTreesMitigation;
import org.hibernate.Hibernate;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.settlementTrees.repositories.SettlementTreesMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.enums.ESector;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.models.BAU;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.repositories.BAURepository;
import com.navyn.emissionlog.modules.mitigationProjects.intervention.Intervention;
import com.navyn.emissionlog.modules.mitigationProjects.intervention.repositories.InterventionRepository;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SettlementTreesMitigationServiceImpl implements SettlementTreesMitigationService {

        private final SettlementTreesMitigationRepository repository;
        private final SettlementTreesParameterService settlementTreesParameterService;
        private final BAURepository bauRepository;
        private final InterventionRepository interventionRepository;

        private static Double apply(SettlementTreesMitigation settlementTreesMitigation) {
                return settlementTreesMitigation.getNumberOfTreesPlanted()
                                + settlementTreesMitigation.getCumulativeNumberOfTrees();
        }

        /**
         * Maps SettlementTreesMitigation entity to Response DTO
         * This method loads intervention data within the transaction to avoid lazy
         * loading issues
         */
        private SettlementTreesMitigationResponseDto toResponseDto(SettlementTreesMitigation mitigation) {
                SettlementTreesMitigationResponseDto dto = new SettlementTreesMitigationResponseDto();
                dto.setId(mitigation.getId());
                dto.setYear(mitigation.getYear());
                dto.setCumulativeNumberOfTrees(mitigation.getCumulativeNumberOfTrees());
                dto.setNumberOfTreesPlanted(mitigation.getNumberOfTreesPlanted());
                dto.setAgbSingleTreeCurrentYear(mitigation.getAgbSingleTreeCurrentYear());
                dto.setAgbGrowth(mitigation.getAgbGrowth());
                dto.setAboveGroundBiomassGrowth(mitigation.getAboveGroundBiomassGrowth());
                dto.setTotalBiomass(mitigation.getTotalBiomass());
                dto.setBiomassCarbonIncrease(mitigation.getBiomassCarbonIncrease());
                dto.setMitigatedEmissionsKtCO2e(mitigation.getMitigatedEmissionsKtCO2e());
                dto.setAdjustmentMitigation(mitigation.getAdjustmentMitigation());
                dto.setCreatedAt(mitigation.getCreatedAt());
                dto.setUpdatedAt(mitigation.getUpdatedAt());

                // Map intervention - FORCE initialization within transaction to avoid lazy
                // loading
                if (mitigation.getIntervention() != null) {
                        Hibernate.initialize(mitigation.getIntervention());
                        Intervention intervention = mitigation.getIntervention();
                        SettlementTreesMitigationResponseDto.InterventionInfo interventionInfo = new SettlementTreesMitigationResponseDto.InterventionInfo(
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
        public SettlementTreesMitigationResponseDto createSettlementTreesMitigation(SettlementTreesMitigationDto dto) {
                // Fetch the latest active parameter - throws exception if none exists
                SettlementTreesParameterResponseDto param;
                try {
                        param = settlementTreesParameterService.getLatestActive();
                } catch (RuntimeException e) {
                        throw new RuntimeException(
                                        "Cannot create Settlement Trees Mitigation: No active Settlement Trees Parameter found. "
                                                        +
                                                        "Please create an active parameter first before creating mitigation records.",
                                        e);
                }

                SettlementTreesMitigation mitigation = new SettlementTreesMitigation();

                Optional<SettlementTreesMitigation> lastYearRecord = repository
                                .findTopByYearLessThanOrderByYearDesc(dto.getYear());
                Double cumulativeNumberOfTrees = lastYearRecord.map(SettlementTreesMitigationServiceImpl::apply)
                                .orElse(0.0);
                Double agbSingleTreePrevYear = lastYearRecord
                                .map(SettlementTreesMitigation::getAgbSingleTreeCurrentYear)
                                .orElse(0.0);

                // Convert AGB to cubic meters (standard unit)
                double agbCurrentYearInCubicMeters = dto.getAgbUnit().toCubicMeters(dto.getAgbSingleTreeCurrentYear());

                // Map input fields (store in standard units)
                mitigation.setYear(dto.getYear());
                mitigation.setCumulativeNumberOfTrees(cumulativeNumberOfTrees);
                mitigation.setNumberOfTreesPlanted(dto.getNumberOfTreesPlanted());
                mitigation.setAgbSingleTreeCurrentYear(agbCurrentYearInCubicMeters);

                // Get values from parameter
                double conversionM3ToTonnes = param.getConversationM3ToTonnes();
                double ratioBGBToAGB = param.getRatioOfBelowGroundBiomass();
                double carbonContent = param.getCarbonContent();
                double carbonToC02 = param.getCarbonToC02();

                // 1. Calculate AGB Growth (tonnes m3)
                double agbGrowth = (agbCurrentYearInCubicMeters - agbSingleTreePrevYear) * cumulativeNumberOfTrees;
                mitigation.setAgbGrowth(agbGrowth);

                // 2. Calculate Aboveground Biomass Growth (tonnes DM)
                double aboveGroundBiomassGrowth = conversionM3ToTonnes * agbGrowth;
                mitigation.setAboveGroundBiomassGrowth(aboveGroundBiomassGrowth);

                // 3. Calculate Total Biomass (tonnes DM/year) - includes belowground
                double totalBiomass = (aboveGroundBiomassGrowth * ratioBGBToAGB) + aboveGroundBiomassGrowth;
                mitigation.setTotalBiomass(totalBiomass);

                // 4. Calculate Biomass Carbon Increase (tonnes C/year)
                double biomassCarbonIncrease = totalBiomass * carbonContent;
                mitigation.setBiomassCarbonIncrease(biomassCarbonIncrease);

                // 5. Calculate Mitigated Emissions (Kt CO2e)
                double mitigatedEmissions = (biomassCarbonIncrease * carbonToC02) / 1000.0;
                mitigation.setMitigatedEmissionsKtCO2e(mitigatedEmissions);

                // 6. Calculate Adjustment Mitigation (Kilotonnes CO2)
                BAU bau = bauRepository.findByYearAndSector(mitigation.getYear(), ESector.AFOLU)
                                .orElseThrow(() -> new RuntimeException(
                                                String.format("BAU record for AFOLU sector and year %d not found. Please create a BAU record first.",
                                                                mitigation.getYear())));
                double adjustmentMitigation = bau.getValue() - mitigatedEmissions;
                mitigation.setAdjustmentMitigation(adjustmentMitigation);

                // Handle intervention if provided
                if (dto.getInterventionId() != null) {
                        Intervention intervention = interventionRepository.findById(dto.getInterventionId())
                                        .orElseThrow(() -> new RuntimeException(
                                                        "Intervention not found with id: " + dto.getInterventionId()));
                        mitigation.setIntervention(intervention);
                } else {
                        mitigation.setIntervention(null);
                }

                SettlementTreesMitigation saved = repository.save(mitigation);

                // CASCADE: Find and recalculate all subsequent years that depend on this new
                // record
                // This ensures cumulative calculations are correct when inserting a year in the
                // middle
                List<SettlementTreesMitigation> subsequentRecords = repository
                                .findByYearGreaterThanOrderByYearAsc(dto.getYear());

                for (SettlementTreesMitigation subsequent : subsequentRecords) {
                        recalculateExistingRecord(subsequent, param);
                        repository.save(subsequent);
                }

                return toResponseDto(saved);
        }

        @Override
        @Transactional
        public SettlementTreesMitigationResponseDto updateSettlementTreesMitigation(UUID id,
                        SettlementTreesMitigationDto dto) {
                // Fetch the latest active parameter - throws exception if none exists
                SettlementTreesParameterResponseDto param;
                try {
                        param = settlementTreesParameterService.getLatestActive();
                } catch (RuntimeException e) {
                        throw new RuntimeException(
                                        "Cannot update Settlement Trees Mitigation: No active Settlement Trees Parameter found. "
                                                        +
                                                        "Please create an active parameter first before updating mitigation records.",
                                        e);
                }

                SettlementTreesMitigation mitigation = repository.findById(id)
                                .orElseThrow(() -> new RuntimeException(
                                                "Settlement Trees Mitigation record not found with id: " + id));

                // Update the current record
                recalculateAndUpdateRecord(mitigation, dto, param);
                SettlementTreesMitigation updatedRecord = repository.save(mitigation);

                // CASCADE: Find and recalculate all subsequent years
                List<SettlementTreesMitigation> subsequentRecords = repository
                                .findByYearGreaterThanOrderByYearAsc(dto.getYear());

                for (SettlementTreesMitigation subsequent : subsequentRecords) {
                        recalculateExistingRecord(subsequent, param);
                        repository.save(subsequent);
                }

                return toResponseDto(updatedRecord);
        }

        @Override
        @Transactional
        public void deleteSettlementTreesMitigation(UUID id) {
                // Fetch the latest active parameter - throws exception if none exists
                SettlementTreesParameterResponseDto param;
                try {
                        param = settlementTreesParameterService.getLatestActive();
                } catch (RuntimeException e) {
                        throw new RuntimeException(
                                        "Cannot delete Settlement Trees Mitigation: No active Settlement Trees Parameter found. "
                                                        +
                                                        "Please create an active parameter first before deleting mitigation records.",
                                        e);
                }

                SettlementTreesMitigation mitigation = repository.findById(id)
                                .orElseThrow(() -> new RuntimeException(
                                                "Settlement Trees Mitigation record not found with id: " + id));

                Integer year = mitigation.getYear();
                repository.delete(mitigation);

                // Recalculate all subsequent years as cumulative fields depend on previous
                // records
                List<SettlementTreesMitigation> subsequentRecords = repository
                                .findByYearGreaterThanOrderByYearAsc(year);
                for (SettlementTreesMitigation subsequent : subsequentRecords) {
                        recalculateExistingRecord(subsequent, param);
                        repository.save(subsequent);
                }
        }

        /**
         * Recalculates an existing record based on its current year and stored input
         * values
         */
        private void recalculateExistingRecord(SettlementTreesMitigation mitigation,
                        SettlementTreesParameterResponseDto param) {
                Optional<SettlementTreesMitigation> lastYearRecord = repository
                                .findTopByYearLessThanOrderByYearDesc(mitigation.getYear());
                Double cumulativeNumberOfTrees = lastYearRecord.map(SettlementTreesMitigationServiceImpl::apply)
                                .orElse(0.0);
                Double agbSingleTreePrevYear = lastYearRecord
                                .map(SettlementTreesMitigation::getAgbSingleTreeCurrentYear)
                                .orElse(0.0);

                mitigation.setCumulativeNumberOfTrees(cumulativeNumberOfTrees);

                // Get values from parameter
                double conversionM3ToTonnes = param.getConversationM3ToTonnes();
                double ratioBGBToAGB = param.getRatioOfBelowGroundBiomass();
                double carbonContent = param.getCarbonContent();
                double carbonToC02 = param.getCarbonToC02();

                // Recalculate all derived fields using existing AGB value (matching create
                // method logic)
                double agbCurrentYearInCubicMeters = mitigation.getAgbSingleTreeCurrentYear();

                // 1. Calculate AGB Growth (tonnes m3)
                double agbGrowth = (agbCurrentYearInCubicMeters - agbSingleTreePrevYear) * cumulativeNumberOfTrees;
                mitigation.setAgbGrowth(agbGrowth);

                // 2. Calculate Aboveground Biomass Growth (tonnes DM)
                double aboveGroundBiomassGrowth = conversionM3ToTonnes * agbGrowth;
                mitigation.setAboveGroundBiomassGrowth(aboveGroundBiomassGrowth);

                // 3. Calculate Total Biomass (tonnes DM/year) - includes belowground
                double totalBiomass = (aboveGroundBiomassGrowth * ratioBGBToAGB) + aboveGroundBiomassGrowth;
                mitigation.setTotalBiomass(totalBiomass);

                // 4. Calculate Biomass Carbon Increase (tonnes C/year)
                double biomassCarbonIncrease = totalBiomass * carbonContent;
                mitigation.setBiomassCarbonIncrease(biomassCarbonIncrease);

                // 5. Calculate Mitigated Emissions (Kt CO2e)
                double mitigatedEmissions = (biomassCarbonIncrease * carbonToC02) / 1000.0;
                mitigation.setMitigatedEmissionsKtCO2e(mitigatedEmissions);

                // 6. Calculate Adjustment Mitigation (Kilotonnes CO2)
                BAU bau = bauRepository.findByYearAndSector(mitigation.getYear(), ESector.AFOLU)
                                .orElseThrow(() -> new RuntimeException(
                                                String.format("BAU record for AFOLU sector and year %d not found. Please create a BAU record first.",
                                                                mitigation.getYear())));
                double adjustmentMitigation = bau.getValue() - mitigatedEmissions;
                mitigation.setAdjustmentMitigation(adjustmentMitigation);
        }

        /**
         * Recalculates a record with new DTO values
         */
        private void recalculateAndUpdateRecord(SettlementTreesMitigation mitigation,
                        SettlementTreesMitigationDto dto, SettlementTreesParameterResponseDto param) {
                Optional<SettlementTreesMitigation> lastYearRecord = repository
                                .findTopByYearLessThanOrderByYearDesc(dto.getYear());
                Double cumulativeNumberOfTrees = lastYearRecord.map(SettlementTreesMitigationServiceImpl::apply)
                                .orElse(0.0);
                Double agbSingleTreePrevYear = lastYearRecord
                                .map(SettlementTreesMitigation::getAgbSingleTreeCurrentYear)
                                .orElse(0.0);

                // Convert AGB to cubic meters (standard unit)
                double agbCurrentYearInCubicMeters = dto.getAgbUnit().toCubicMeters(dto.getAgbSingleTreeCurrentYear());

                // Update input fields (store in standard units)
                mitigation.setYear(dto.getYear());
                mitigation.setCumulativeNumberOfTrees(cumulativeNumberOfTrees);
                mitigation.setNumberOfTreesPlanted(dto.getNumberOfTreesPlanted());
                mitigation.setAgbSingleTreeCurrentYear(agbCurrentYearInCubicMeters);

                // Get values from parameter
                double conversionM3ToTonnes = param.getConversationM3ToTonnes();
                double ratioBGBToAGB = param.getRatioOfBelowGroundBiomass();
                double carbonContent = param.getCarbonContent();
                double carbonToC02 = param.getCarbonToC02();

                // Recalculate derived fields using parameter values (matching create method
                // logic)
                // 1. Calculate AGB Growth (tonnes m3)
                double agbGrowth = (agbCurrentYearInCubicMeters - agbSingleTreePrevYear) * cumulativeNumberOfTrees;
                mitigation.setAgbGrowth(agbGrowth);

                // 2. Calculate Aboveground Biomass Growth (tonnes DM)
                double aboveGroundBiomassGrowth = conversionM3ToTonnes * agbGrowth;
                mitigation.setAboveGroundBiomassGrowth(aboveGroundBiomassGrowth);

                // 3. Calculate Total Biomass (tonnes DM/year) - includes belowground
                double totalBiomass = (aboveGroundBiomassGrowth * ratioBGBToAGB) + aboveGroundBiomassGrowth;
                mitigation.setTotalBiomass(totalBiomass);

                // 4. Calculate Biomass Carbon Increase (tonnes C/year)
                double biomassCarbonIncrease = totalBiomass * carbonContent;
                mitigation.setBiomassCarbonIncrease(biomassCarbonIncrease);

                // 5. Calculate Mitigated Emissions (Kt CO2e)
                double mitigatedEmissions = (biomassCarbonIncrease * carbonToC02) / 1000.0;
                mitigation.setMitigatedEmissionsKtCO2e(mitigatedEmissions);

                // 6. Calculate Adjustment Mitigation (Kilotonnes CO2)
                BAU bau = bauRepository.findByYearAndSector(mitigation.getYear(), ESector.AFOLU)
                                .orElseThrow(() -> new RuntimeException(
                                                String.format("BAU record for AFOLU sector and year %d not found. Please create a BAU record first.",
                                                                mitigation.getYear())));
                double adjustmentMitigation = bau.getValue() - mitigatedEmissions;
                mitigation.setAdjustmentMitigation(adjustmentMitigation);

                // Handle intervention if provided
                if (dto.getInterventionId() != null) {
                        Intervention intervention = interventionRepository.findById(dto.getInterventionId())
                                        .orElseThrow(() -> new RuntimeException(
                                                        "Intervention not found with id: " + dto.getInterventionId()));
                        mitigation.setIntervention(intervention);
                } else {
                        mitigation.setIntervention(null);
                }
        }

        @Override
        @Transactional(readOnly = true)
        public List<SettlementTreesMitigationResponseDto> getAllSettlementTreesMitigation(Integer year) {
                Specification<SettlementTreesMitigation> spec = Specification
                                .<SettlementTreesMitigation>where(MitigationSpecifications.hasYear(year));
                List<SettlementTreesMitigation> mitigations = repository.findAll(spec,
                                Sort.by(Sort.Direction.ASC, "year"));
                return mitigations.stream()
                                .map(this::toResponseDto)
                                .collect(java.util.stream.Collectors.toList());
        }

        @Override
        @Transactional(readOnly = true)
        public Optional<SettlementTreesMitigationResponseDto> getByYear(Integer year) {
                return repository.findByYear(year)
                                .map(this::toResponseDto);
        }

        @Override
        public byte[] generateExcelTemplate() {
                try (Workbook workbook = new XSSFWorkbook();
                                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

                        Sheet sheet = workbook.createSheet("Settlement Trees Mitigation");

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
                        titleCell.setCellValue("Settlement Trees Mitigation Template");
                        titleCell.setCellStyle(titleStyle);
                        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

                        rowIdx++; // Blank row

                        // Create header row
                        Row headerRow = sheet.createRow(rowIdx++);
                        headerRow.setHeightInPoints(22);
                        String[] headers = {
                                        "Year",
                                        "Number of Trees Planted",
                                        "AGB Single Tree Current Year",
                                        "AGB Unit"
                        };

                        for (int i = 0; i < headers.length; i++) {
                                Cell cell = headerRow.createCell(i);
                                cell.setCellValue(headers[i]);
                                cell.setCellStyle(headerStyle);
                        }

                        // Get enum values for dropdowns
                        String[] agbUnitValues = Arrays.stream(VolumeUnits.values())
                                        .map(Enum::name)
                                        .toArray(String[]::new);

                        // Create data validation helper
                        DataValidationHelper validationHelper = sheet.getDataValidationHelper();

                        // Data validation for AGB Unit column (Column D, index 3)
                        CellRangeAddressList agbUnitList = new CellRangeAddressList(3, 1000, 3, 3);
                        DataValidationConstraint agbUnitConstraint = validationHelper
                                        .createExplicitListConstraint(agbUnitValues);
                        DataValidation agbUnitValidation = validationHelper.createValidation(agbUnitConstraint,
                                        agbUnitList);
                        agbUnitValidation.setShowErrorBox(true);
                        agbUnitValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
                        agbUnitValidation.createErrorBox("Invalid AGB Unit",
                                        "Please select a valid AGB unit from the dropdown list.");
                        agbUnitValidation.setShowPromptBox(true);
                        agbUnitValidation.createPromptBox("AGB Unit", "Select an AGB unit from the dropdown list.");
                        sheet.addValidationData(agbUnitValidation);

                        // Create example data rows
                        Object[] exampleData1 = {
                                        2024,
                                        1000.0,
                                        0.5,
                                        "CUBIC_METER"
                        };

                        Object[] exampleData2 = {
                                        2025,
                                        1500.0,
                                        0.75,
                                        "CUBIC_METER"
                        };

                        // First example row
                        Row exampleRow1 = sheet.createRow(rowIdx++);
                        exampleRow1.setHeightInPoints(18);
                        for (int i = 0; i < exampleData1.length; i++) {
                                Cell cell = exampleRow1.createCell(i);
                                if (i == 0) { // Year
                                        cell.setCellStyle(yearStyle);
                                        cell.setCellValue(((Number) exampleData1[i]).intValue());
                                } else if (i == 1 || i == 2) { // Numbers
                                        cell.setCellStyle(numberStyle);
                                        cell.setCellValue(((Number) exampleData1[i]).doubleValue());
                                } else { // Unit (string)
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
                                } else if (i == 1 || i == 2) { // Numbers
                                        CellStyle altNumStyle = workbook.createCellStyle();
                                        altNumStyle.cloneStyleFrom(numberStyle);
                                        altNumStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                                        altNumStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                                        cell.setCellStyle(altNumStyle);
                                        cell.setCellValue(((Number) exampleData2[i]).doubleValue());
                                } else { // Unit (string)
                                        cell.setCellStyle(alternateDataStyle);
                                        cell.setCellValue((String) exampleData2[i]);
                                }
                        }

                        // Auto-size columns with wider limits (bigger columns)
                        for (int i = 0; i < headers.length; i++) {
                                sheet.autoSizeColumn(i);
                                int currentWidth = sheet.getColumnWidth(i);
                                int minWidth = 5000; // Increased from 3000 - wider minimum
                                int maxWidth = 30000; // Increased from 20000 - wider maximum
                                if (currentWidth < minWidth) {
                                        sheet.setColumnWidth(i, minWidth);
                                } else if (currentWidth > maxWidth) {
                                        sheet.setColumnWidth(i, maxWidth);
                                } else {
                                        // Make columns even wider - add 30% more width
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
        public Map<String, Object> createSettlementTreesMitigationFromExcel(MultipartFile file) {
                List<SettlementTreesMitigationResponseDto> savedRecords = new ArrayList<>();
                List<Integer> skippedYears = new ArrayList<>();
                List<Map<String, Object>> skippedParameterNotFound = new ArrayList<>();
                List<Map<String, Object>> skippedInterventionNotFound = new ArrayList<>();
                List<Map<String, Object>> skippedMissingFields = new ArrayList<>();
                List<Map<String, Object>> skippedBAUNotFound = new ArrayList<>();
                int totalProcessed = 0;

                try {
                        List<SettlementTreesMitigationDto> dtos = ExcelReader.readExcel(
                                        file.getInputStream(),
                                        SettlementTreesMitigationDto.class,
                                        ExcelType.SETTLEMENT_TREES_MITIGATION);

                        // Create a list of DTOs with their original row numbers for error reporting
                        List<Map.Entry<SettlementTreesMitigationDto, Integer>> dtoWithRowNumbers = new ArrayList<>();
                        for (int i = 0; i < dtos.size(); i++) {
                                dtoWithRowNumbers.add(new AbstractMap.SimpleEntry<>(dtos.get(i), i));
                        }

                        // Sort by year (ascending) to ensure cumulative calculations are correct
                        // This ensures that when processing new records, they are processed in chronological order
                        // Skipped years (already exist) will still work correctly because they query the database
                        dtoWithRowNumbers.sort((entry1, entry2) -> {
                                Integer year1 = entry1.getKey().getYear();
                                Integer year2 = entry2.getKey().getYear();
                                // Handle null years - put them at the end
                                if (year1 == null && year2 == null)
                                        return 0;
                                if (year1 == null)
                                        return 1;
                                if (year2 == null)
                                        return -1;
                                return year1.compareTo(year2);
                        });

                        for (Map.Entry<SettlementTreesMitigationDto, Integer> entry : dtoWithRowNumbers) {
                                SettlementTreesMitigationDto dto = entry.getKey();
                                int originalIndex = entry.getValue();
                                totalProcessed++;
                                int rowNumber = originalIndex + 1; // Excel row number (1-based, accounting for header
                                                                   // row)
                                int excelRowNumber = rowNumber + 2; // +2 for header row and 0-based index

                                // Validate required fields
                                List<String> missingFields = new ArrayList<>();
                                if (dto.getYear() == null) {
                                        missingFields.add("Year");
                                }
                                if (dto.getNumberOfTreesPlanted() == null) {
                                        missingFields.add("Number of Trees Planted");
                                }
                                if (dto.getAgbSingleTreeCurrentYear() == null) {
                                        missingFields.add("AGB Single Tree Current Year");
                                }
                                if (dto.getAgbUnit() == null) {
                                        missingFields.add("AGB Unit");
                                }

                                if (!missingFields.isEmpty()) {
                                        Map<String, Object> skipInfo = new HashMap<>();
                                        skipInfo.put("row", excelRowNumber);
                                        skipInfo.put("year", dto.getYear() != null ? dto.getYear() : "N/A");
                                        skipInfo.put("reason",
                                                        "Missing required fields: " + String.join(", ", missingFields));
                                        skippedMissingFields.add(skipInfo);
                                        continue; // Skip this row
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
                                                continue; // Skip this row
                                        }
                                }
                                // Clear the temporary interventionName field
                                dto.setInterventionName(null);

                                // Check if year already exists
                                if (repository.findByYear(dto.getYear()).isPresent()) {
                                        skippedYears.add(dto.getYear());
                                        continue; // Skip this row
                                }

                                // Try to create the record - catch specific errors and skip instead of failing
                                try {
                                        SettlementTreesMitigationResponseDto saved = createSettlementTreesMitigation(dto);
                                        savedRecords.add(saved);
                                } catch (RuntimeException e) {
                                        String errorMessage = e.getMessage();
                                        if (errorMessage != null) {
                                                // Check for Settlement Trees Parameter not found error
                                                if (errorMessage.contains("Settlement Trees Parameter") ||
                                                                errorMessage.contains("active parameter") ||
                                                                errorMessage.contains("No active Settlement Trees Parameter")) {
                                                        Map<String, Object> skipInfo = new HashMap<>();
                                                        skipInfo.put("row", excelRowNumber);
                                                        skipInfo.put("year", dto.getYear());
                                                        skipInfo.put("reason", errorMessage);
                                                        skippedParameterNotFound.add(skipInfo);
                                                        continue; // Skip this row
                                                }
                                                // Check for BAU not found error - skip in Excel upload, but single form upload will throw error
                                                if (errorMessage.contains("BAU record") && errorMessage.contains("not found")) {
                                                        Map<String, Object> skipInfo = new HashMap<>();
                                                        skipInfo.put("row", excelRowNumber);
                                                        skipInfo.put("year", dto.getYear());
                                                        skipInfo.put("reason", errorMessage);
                                                        skippedBAUNotFound.add(skipInfo);
                                                        continue; // Skip this row
                                                }
                                        }
                                        // If it's a different error, re-throw it (e.g., file format issues)
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
                        // Re-throw IOException with user-friendly message
                        String message = e.getMessage();
                        if (message != null) {
                                throw new RuntimeException(message, e);
                        } else {
                                throw new RuntimeException(
                                                "Incorrect template. Please download the correct template and try again.",
                                                e);
                        }
                } catch (NullPointerException e) {
                        // Handle null pointer exceptions with clear message
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
