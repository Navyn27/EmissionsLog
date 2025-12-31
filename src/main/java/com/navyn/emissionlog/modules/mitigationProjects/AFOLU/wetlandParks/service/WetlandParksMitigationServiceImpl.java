package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.service;

import com.navyn.emissionlog.Enums.ExcelType;
import com.navyn.emissionlog.Enums.Metrics.VolumePerAreaUnit;
import com.navyn.emissionlog.Enums.Mitigation.WetlandTreeCategory;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.dtos.WetlandParksMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.dtos.WetlandParksMitigationResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.dtos.WetlandParksParameterResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.models.WetlandParksMitigation;
import org.hibernate.Hibernate;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.repositories.WetlandParksMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.service.WetlandParksParameterService;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WetlandParksMitigationServiceImpl implements WetlandParksMitigationService {

    private final WetlandParksMitigationRepository repository;
    private final WetlandParksParameterService wetlandParksParameterService;
    private final BAURepository bauRepository;
    private final InterventionRepository interventionRepository;

    /**
     * Maps WetlandParksMitigation entity to Response DTO
     * This method loads intervention data within the transaction to avoid lazy loading issues
     */
    private WetlandParksMitigationResponseDto toResponseDto(WetlandParksMitigation mitigation) {
        WetlandParksMitigationResponseDto dto = new WetlandParksMitigationResponseDto();
        dto.setId(mitigation.getId());
        dto.setYear(mitigation.getYear());
        dto.setTreeCategory(mitigation.getTreeCategory());
        dto.setAreaPlanted(mitigation.getAreaPlanted());
        dto.setAbovegroundBiomassAGB(mitigation.getAbovegroundBiomassAGB());
        dto.setCumulativeArea(mitigation.getCumulativeArea());
        dto.setPreviousYearAGB(mitigation.getPreviousYearAGB());
        dto.setAgbGrowth(mitigation.getAgbGrowth());
        dto.setAbovegroundBiomassGrowth(mitigation.getAbovegroundBiomassGrowth());
        dto.setTotalBiomass(mitigation.getTotalBiomass());
        dto.setBiomassCarbonIncrease(mitigation.getBiomassCarbonIncrease());
        dto.setMitigatedEmissionsKtCO2e(mitigation.getMitigatedEmissionsKtCO2e());
        dto.setAdjustmentMitigation(mitigation.getAdjustmentMitigation());
        dto.setCreatedAt(mitigation.getCreatedAt());
        dto.setUpdatedAt(mitigation.getUpdatedAt());

        // Map intervention - FORCE initialization within transaction to avoid lazy loading
        if (mitigation.getIntervention() != null) {
            Hibernate.initialize(mitigation.getIntervention());
            Intervention intervention = mitigation.getIntervention();
            WetlandParksMitigationResponseDto.InterventionInfo interventionInfo = new WetlandParksMitigationResponseDto.InterventionInfo(
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
    public WetlandParksMitigationResponseDto createWetlandParksMitigation(WetlandParksMitigationDto dto) {
        // Fetch the latest active parameter - throws exception if none exists
        WetlandParksParameterResponseDto param;
        try {
            param = wetlandParksParameterService.getLatestActive();
        } catch (RuntimeException e) {
            throw new RuntimeException(
                    "Cannot create Wetland Parks Mitigation: No active Wetland Parks Parameter found. "
                            +
                            "Please create an active parameter first before creating mitigation records.",
                    e);
        }

        WetlandParksMitigation mitigation = new WetlandParksMitigation();

        Optional<WetlandParksMitigation> lastYearRecord = repository
                .findTopByYearLessThanAndTreeCategoryOrderByYearDesc(dto.getYear(), dto.getTreeCategory());
        Double cumulativeArea = lastYearRecord
                .map(wetlandParksMitigation -> wetlandParksMitigation.getAreaPlanted()
                        + wetlandParksMitigation.getCumulativeArea())
                .orElse(0.0);

        // Fetch previous year's AGB from DB (for same treeCategory)
        Double previousYearAGB = lastYearRecord
                .map(WetlandParksMitigation::getAbovegroundBiomassAGB)
                .orElse(0.0);

        // Convert units to standard values
        double areaPlantedInHectares = dto.getAreaPlanted();
        double agbInCubicMeterPerHA = dto.getAgbUnit().toCubicMeterPerHA(dto.getAbovegroundBiomassAGB());

        // Map input fields (store in standard units)
        mitigation.setYear(dto.getYear());
        mitigation.setTreeCategory(dto.getTreeCategory());
        mitigation.setCumulativeArea(cumulativeArea);
        mitigation.setAreaPlanted(areaPlantedInHectares);
        mitigation.setAbovegroundBiomassAGB(agbInCubicMeterPerHA);
        mitigation.setPreviousYearAGB(previousYearAGB);

        // Get values from parameter
        double conversionM3ToTonnesDM = param.getConversionM3ToTonnesDM();
        double ratioBGBToAGB = param.getRatioOfBelowGroundBiomass();
        double carbonContentDryWood = param.getCarbonContentDryWood();
        double carbonToC02 = param.getCarbonToC02();

        // 1. Calculate AGB Growth (m³)
        // AGB growth = (AGB in current year - AGB in previous year) × Cumulative Area
        double agbGrowth = (agbInCubicMeterPerHA - previousYearAGB) * cumulativeArea;
        mitigation.setAgbGrowth(agbGrowth);

        // 2. Calculate Aboveground Biomass Growth (tonnes DM)
        // Aboveground Biomass Growth = AGB growth × Conversion m3 to tonnes DM
        double abovegroundBiomassGrowth = agbGrowth * conversionM3ToTonnesDM;
        mitigation.setAbovegroundBiomassGrowth(abovegroundBiomassGrowth);

        // 3. Calculate Total Biomass (tonnes DM/year) - includes belowground
        // Total biomass = Aboveground Biomass Growth + (Aboveground Biomass Growth × Ratio BGB to AGB)
        double totalBiomass = abovegroundBiomassGrowth + (abovegroundBiomassGrowth * ratioBGBToAGB);
        mitigation.setTotalBiomass(totalBiomass);

        // 4. Calculate Biomass Carbon Increase (tonnes C/year)
        // Biomass carbon increase = Total biomass × Carbon content in dry wood
        double biomassCarbonIncrease = totalBiomass * carbonContentDryWood;
        mitigation.setBiomassCarbonIncrease(biomassCarbonIncrease);

        // 5. Calculate Mitigated Emissions (Kt CO2e)
        // Mitigated emissions = (Biomass carbon increase × Conversion C to CO2) / 1000
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

        WetlandParksMitigation saved = repository.save(mitigation);

        // CASCADE: Find and recalculate all subsequent years for the same tree category
        List<WetlandParksMitigation> subsequentRecords = repository
                .findByYearGreaterThanAndTreeCategoryOrderByYearAsc(dto.getYear(), dto.getTreeCategory());

        for (WetlandParksMitigation subsequent : subsequentRecords) {
            recalculateExistingRecord(subsequent, param);
            repository.save(subsequent);
        }

        return toResponseDto(saved);
    }

    @Override
    @Transactional
    public WetlandParksMitigationResponseDto updateWetlandParksMitigation(UUID id, WetlandParksMitigationDto dto) {
        // Fetch the latest active parameter - throws exception if none exists
        WetlandParksParameterResponseDto param;
        try {
            param = wetlandParksParameterService.getLatestActive();
        } catch (RuntimeException e) {
            throw new RuntimeException(
                    "Cannot update Wetland Parks Mitigation: No active Wetland Parks Parameter found. "
                            +
                            "Please create an active parameter first before updating mitigation records.",
                    e);
        }

        WetlandParksMitigation mitigation = repository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Wetland Parks Mitigation record not found with id: " + id));

        // Update the current record
        recalculateAndUpdateRecord(mitigation, dto, param);
        WetlandParksMitigation updatedRecord = repository.save(mitigation);

        // CASCADE: Find and recalculate all subsequent years for the same tree category
        List<WetlandParksMitigation> subsequentRecords = repository
                .findByYearGreaterThanAndTreeCategoryOrderByYearAsc(dto.getYear(), dto.getTreeCategory());

        for (WetlandParksMitigation subsequent : subsequentRecords) {
            recalculateExistingRecord(subsequent, param);
            repository.save(subsequent);
        }

        return toResponseDto(updatedRecord);
    }

    @Override
    @Transactional
    public void deleteWetlandParksMitigation(UUID id) {
        // Fetch the latest active parameter - throws exception if none exists
        WetlandParksParameterResponseDto param;
        try {
            param = wetlandParksParameterService.getLatestActive();
        } catch (RuntimeException e) {
            throw new RuntimeException(
                    "Cannot delete Wetland Parks Mitigation: No active Wetland Parks Parameter found. "
                            +
                            "Please create an active parameter first before deleting mitigation records.",
                    e);
        }

        WetlandParksMitigation mitigation = repository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Wetland Parks Mitigation record not found with id: " + id));

        Integer year = mitigation.getYear();
        WetlandTreeCategory category = mitigation.getTreeCategory();
        repository.delete(mitigation);

        // Recalculate all subsequent years for this tree category because cumulative fields depend on previous records
        List<WetlandParksMitigation> subsequentRecords = repository
                .findByYearGreaterThanAndTreeCategoryOrderByYearAsc(year, category);
        for (WetlandParksMitigation subsequent : subsequentRecords) {
            recalculateExistingRecord(subsequent, param);
            repository.save(subsequent);
        }
    }

    /**
     * Recalculates an existing record based on its current year and stored input values
     */
    private void recalculateExistingRecord(WetlandParksMitigation mitigation, WetlandParksParameterResponseDto param) {
        Optional<WetlandParksMitigation> lastYearRecord = repository
                .findTopByYearLessThanAndTreeCategoryOrderByYearDesc(mitigation.getYear(), mitigation.getTreeCategory());
        Double cumulativeArea = lastYearRecord
                .map(wetlandParksMitigation -> wetlandParksMitigation.getAreaPlanted()
                        + wetlandParksMitigation.getCumulativeArea())
                .orElse(0.0);

        mitigation.setCumulativeArea(cumulativeArea);

        // Fetch previous year's AGB from DB
        Double previousYearAGB = lastYearRecord
                .map(WetlandParksMitigation::getAbovegroundBiomassAGB)
                .orElse(0.0);
        mitigation.setPreviousYearAGB(previousYearAGB);

        // Get values from parameter
        double conversionM3ToTonnesDM = param.getConversionM3ToTonnesDM();
        double ratioBGBToAGB = param.getRatioOfBelowGroundBiomass();
        double carbonContentDryWood = param.getCarbonContentDryWood();
        double carbonToC02 = param.getCarbonToC02();

        // Recalculate all derived fields using existing AGB value
        double agbInCubicMeterPerHA = mitigation.getAbovegroundBiomassAGB();

        // 1. Calculate AGB Growth (m³)
        double agbGrowth = (agbInCubicMeterPerHA - previousYearAGB) * cumulativeArea;
        mitigation.setAgbGrowth(agbGrowth);

        // 2. Calculate Aboveground Biomass Growth (tonnes DM)
        double abovegroundBiomassGrowth = agbGrowth * conversionM3ToTonnesDM;
        mitigation.setAbovegroundBiomassGrowth(abovegroundBiomassGrowth);

        // 3. Calculate Total Biomass (tonnes DM/year) - includes belowground
        double totalBiomass = abovegroundBiomassGrowth + (abovegroundBiomassGrowth * ratioBGBToAGB);
        mitigation.setTotalBiomass(totalBiomass);

        double biomassCarbonIncrease = totalBiomass * carbonContentDryWood;
        mitigation.setBiomassCarbonIncrease(biomassCarbonIncrease);

        double mitigatedEmissions = (biomassCarbonIncrease * carbonToC02) / 1000.0;
        mitigation.setMitigatedEmissionsKtCO2e(mitigatedEmissions);

        // Calculate Adjustment Mitigation (Kilotonnes CO2)
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
    private void recalculateAndUpdateRecord(WetlandParksMitigation mitigation, WetlandParksMitigationDto dto,
            WetlandParksParameterResponseDto param) {
        Optional<WetlandParksMitigation> lastYearRecord = repository
                .findTopByYearLessThanAndTreeCategoryOrderByYearDesc(dto.getYear(), dto.getTreeCategory());
        Double cumulativeArea = lastYearRecord
                .map(wetlandParksMitigation -> wetlandParksMitigation.getAreaPlanted()
                        + wetlandParksMitigation.getCumulativeArea())
                .orElse(0.0);

        // Convert units to standard values
        double areaPlantedInHectares = dto.getAreaPlanted();
        double agbInCubicMeterPerHA = dto.getAgbUnit().toCubicMeterPerHA(dto.getAbovegroundBiomassAGB());

        // Fetch previous year's AGB from DB
        Double previousYearAGB = lastYearRecord
                .map(WetlandParksMitigation::getAbovegroundBiomassAGB)
                .orElse(0.0);

        // Update input fields (store in standard units)
        mitigation.setYear(dto.getYear());
        mitigation.setTreeCategory(dto.getTreeCategory());
        mitigation.setCumulativeArea(cumulativeArea);
        mitigation.setAreaPlanted(areaPlantedInHectares);
        mitigation.setAbovegroundBiomassAGB(agbInCubicMeterPerHA);
        mitigation.setPreviousYearAGB(previousYearAGB);

        // Get values from parameter
        double conversionM3ToTonnesDM = param.getConversionM3ToTonnesDM();
        double ratioBGBToAGB = param.getRatioOfBelowGroundBiomass();
        double carbonContentDryWood = param.getCarbonContentDryWood();
        double carbonToC02 = param.getCarbonToC02();

        // Recalculate derived fields
        // 1. Calculate AGB Growth (m³)
        double agbGrowth = (agbInCubicMeterPerHA - previousYearAGB) * cumulativeArea;
        mitigation.setAgbGrowth(agbGrowth);

        // 2. Calculate Aboveground Biomass Growth (tonnes DM)
        double abovegroundBiomassGrowth = agbGrowth * conversionM3ToTonnesDM;
        mitigation.setAbovegroundBiomassGrowth(abovegroundBiomassGrowth);

        // 3. Calculate Total Biomass (tonnes DM/year) - includes belowground
        double totalBiomass = abovegroundBiomassGrowth + (abovegroundBiomassGrowth * ratioBGBToAGB);
        mitigation.setTotalBiomass(totalBiomass);

        double biomassCarbonIncrease = totalBiomass * carbonContentDryWood;
        mitigation.setBiomassCarbonIncrease(biomassCarbonIncrease);

        double mitigatedEmissions = (biomassCarbonIncrease * carbonToC02) / 1000.0;
        mitigation.setMitigatedEmissionsKtCO2e(mitigatedEmissions);

        // Calculate Adjustment Mitigation (Kilotonnes CO2)
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
    public List<WetlandParksMitigationResponseDto> getAllWetlandParksMitigation(
            Integer year, WetlandTreeCategory category) {
        Specification<WetlandParksMitigation> spec = Specification
                .<WetlandParksMitigation>where(MitigationSpecifications.hasYear(year))
                .and(MitigationSpecifications.hasWetlandTreeCategory(category));
        List<WetlandParksMitigation> mitigations = repository.findAll(spec,
                Sort.by(Sort.Direction.ASC, "year")
                        .and(Sort.by(Sort.Direction.ASC, "treeCategory")));
        return mitigations.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WetlandParksMitigationResponseDto> getByYearAndCategory(
            Integer year, WetlandTreeCategory category) {
        return repository.findByYearAndTreeCategory(year, category)
                .map(this::toResponseDto);
    }

    @Override
    public byte[] generateExcelTemplate() {
        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Wetland Parks Mitigation");

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

            // Get all interventions for dropdown
            List<Intervention> allInterventions = interventionRepository.findAll();
            String[] interventionNames = allInterventions.stream()
                    .map(Intervention::getName)
                    .sorted()
                    .toArray(String[]::new);

            // Title row
            Row titleRow = sheet.createRow(rowIdx++);
            titleRow.setHeightInPoints(30);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Wetland Parks Mitigation Template");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));

            rowIdx++; // Blank row

            // Create header row
            Row headerRow = sheet.createRow(rowIdx++);
            headerRow.setHeightInPoints(22);
            String[] headers = {
                    "Year",
                    "Tree Category",
                    "Area Planted",
                    "AGB Current Year",
                    "AGB Unit",
                    "Intervention Name"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Get enum values for dropdowns
            String[] treeCategoryValues = Arrays.stream(WetlandTreeCategory.values())
                    .map(Enum::name)
                    .toArray(String[]::new);
            String[] agbUnitValues = Arrays.stream(VolumePerAreaUnit.values())
                    .map(Enum::name)
                    .toArray(String[]::new);

            // Create data validation helper
            DataValidationHelper validationHelper = sheet.getDataValidationHelper();

            // Data validation for Tree Category column (Column B, index 1)
            CellRangeAddressList treeCategoryList = new CellRangeAddressList(3, 1000, 1, 1);
            DataValidationConstraint treeCategoryConstraint = validationHelper
                    .createExplicitListConstraint(treeCategoryValues);
            DataValidation treeCategoryValidation = validationHelper.createValidation(
                    treeCategoryConstraint,
                    treeCategoryList);
            treeCategoryValidation.setShowErrorBox(true);
            treeCategoryValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
            treeCategoryValidation.createErrorBox("Invalid Tree Category",
                    "Please select a valid tree category from the dropdown list.");
            treeCategoryValidation.setShowPromptBox(true);
            treeCategoryValidation.createPromptBox("Tree Category",
                    "Select a tree category from the dropdown list.");
            sheet.addValidationData(treeCategoryValidation);

            // Data validation for AGB Unit column (Column E, index 4)
            CellRangeAddressList agbUnitList = new CellRangeAddressList(3, 1000, 4, 4);
            DataValidationConstraint agbUnitConstraint = validationHelper
                    .createExplicitListConstraint(agbUnitValues);
            DataValidation agbUnitValidation = validationHelper.createValidation(agbUnitConstraint, agbUnitList);
            agbUnitValidation.setShowErrorBox(true);
            agbUnitValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
            agbUnitValidation.createErrorBox("Invalid AGB Unit",
                    "Please select a valid AGB unit from the dropdown list.");
            agbUnitValidation.setShowPromptBox(true);
            agbUnitValidation.createPromptBox("AGB Unit", "Select an AGB unit from the dropdown list.");
            sheet.addValidationData(agbUnitValidation);

            // Data validation for Intervention Name column (Column F, index 5)
            if (interventionNames.length > 0) {
                CellRangeAddressList interventionList = new CellRangeAddressList(3, 1000, 5, 5);
                DataValidationConstraint interventionConstraint = validationHelper
                        .createExplicitListConstraint(interventionNames);
                DataValidation interventionValidation = validationHelper.createValidation(interventionConstraint,
                        interventionList);
                interventionValidation.setShowErrorBox(true);
                interventionValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
                interventionValidation.createErrorBox("Invalid Intervention",
                        "Please select a valid intervention from the dropdown list.");
                interventionValidation.setShowPromptBox(true);
                interventionValidation.createPromptBox("Intervention Name",
                        "Select an intervention from the dropdown list.");
                sheet.addValidationData(interventionValidation);
            }

            // Create example data rows
            Object[] exampleData1 = {
                    2024,
                    "BAMBOO_SPP",
                    50.5,
                    25.3,
                    "CUBIC_METER_PER_HA",
                    interventionNames.length > 0 ? interventionNames[0] : ""
            };

            Object[] exampleData2 = {
                    2025,
                    "ACACIA_SPP",
                    75.25,
                    30.5,
                    "CUBIC_METER_PER_HA",
                    ""
            };

            // First example row
            Row exampleRow1 = sheet.createRow(rowIdx++);
            exampleRow1.setHeightInPoints(18);
            for (int i = 0; i < exampleData1.length; i++) {
                Cell cell = exampleRow1.createCell(i);
                if (i == 0) { // Year
                    cell.setCellStyle(yearStyle);
                    cell.setCellValue(((Number) exampleData1[i]).intValue());
                } else if (i == 1 || i == 5) { // Tree Category or Intervention Name (strings)
                    cell.setCellStyle(dataStyle);
                    cell.setCellValue((String) exampleData1[i]);
                } else if (i == 2 || i == 3) { // Numbers
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
                } else if (i == 1 || i == 5) { // Tree Category or Intervention Name (strings)
                    cell.setCellStyle(alternateDataStyle);
                    cell.setCellValue((String) exampleData2[i]);
                } else if (i == 2 || i == 3) { // Numbers
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
                int minWidth = 5000;
                int maxWidth = 30000;
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
    public Map<String, Object> createWetlandParksMitigationFromExcel(MultipartFile file) {
        List<WetlandParksMitigationResponseDto> savedRecords = new ArrayList<>();
        List<String> skippedYearsAndCategories = new ArrayList<>();
        List<Map<String, Object>> skippedParameterNotFound = new ArrayList<>();
        List<Map<String, Object>> skippedInterventionNotFound = new ArrayList<>();
        List<Map<String, Object>> skippedMissingFields = new ArrayList<>();
        List<Map<String, Object>> skippedBAUNotFound = new ArrayList<>();
        int totalProcessed = 0;

        try {
            List<WetlandParksMitigationDto> dtos = ExcelReader.readExcel(
                    file.getInputStream(),
                    WetlandParksMitigationDto.class,
                    ExcelType.WETLAND_PARKS_MITIGATION);

            // Create a list of DTOs with their original row numbers for error reporting
            List<Map.Entry<WetlandParksMitigationDto, Integer>> dtoWithRowNumbers = new ArrayList<>();
            for (int i = 0; i < dtos.size(); i++) {
                dtoWithRowNumbers.add(new AbstractMap.SimpleEntry<>(dtos.get(i), i));
            }

            // Sort by year (ascending) to ensure cumulative calculations are correct
            dtoWithRowNumbers.sort((entry1, entry2) -> {
                Integer year1 = entry1.getKey().getYear();
                Integer year2 = entry2.getKey().getYear();
                if (year1 == null && year2 == null)
                    return 0;
                if (year1 == null)
                    return 1;
                if (year2 == null)
                    return -1;
                return year1.compareTo(year2);
            });

            for (Map.Entry<WetlandParksMitigationDto, Integer> entry : dtoWithRowNumbers) {
                WetlandParksMitigationDto dto = entry.getKey();
                int originalIndex = entry.getValue();
                totalProcessed++;
                int rowNumber = originalIndex + 1;
                int excelRowNumber = rowNumber + 2; // +2 for header row and 0-based index

                // Validate required fields
                List<String> missingFields = new ArrayList<>();
                if (dto.getYear() == null) {
                    missingFields.add("Year");
                }
                if (dto.getTreeCategory() == null) {
                    missingFields.add("Tree Category");
                }
                if (dto.getAreaPlanted() == null) {
                    missingFields.add("Area Planted");
                }
                if (dto.getAbovegroundBiomassAGB() == null) {
                    missingFields.add("Aboveground Biomass AGB");
                }
                if (dto.getAgbUnit() == null) {
                    missingFields.add("AGB Unit");
                }

                if (!missingFields.isEmpty()) {
                    Map<String, Object> skipInfo = new HashMap<>();
                    skipInfo.put("row", excelRowNumber);
                    skipInfo.put("year", dto.getYear() != null ? dto.getYear() : "N/A");
                    skipInfo.put("treeCategory", dto.getTreeCategory() != null ? dto.getTreeCategory().name() : "N/A");
                    skipInfo.put("reason",
                            "Missing required fields: " + String.join(", ", missingFields));
                    skippedMissingFields.add(skipInfo);
                    continue;
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
                        skipInfo.put("treeCategory", dto.getTreeCategory().name());
                        skipInfo.put("reason",
                                String.format("Intervention '%s' not found", interventionName));
                        skippedInterventionNotFound.add(skipInfo);
                        continue;
                    }
                }
                // Clear the temporary interventionName field
                dto.setInterventionName(null);

                // Check if year + treeCategory combination already exists (composite uniqueness)
                if (repository.findByYearAndTreeCategory(dto.getYear(), dto.getTreeCategory()).isPresent()) {
                    skippedYearsAndCategories.add(String.format("Year %d, Tree Category %s", dto.getYear(), dto.getTreeCategory()));
                    continue;
                }

                // Try to create the record - catch specific errors and skip instead of failing
                try {
                    WetlandParksMitigationResponseDto saved = createWetlandParksMitigation(dto);
                    savedRecords.add(saved);
                } catch (RuntimeException e) {
                    String errorMessage = e.getMessage();
                    if (errorMessage != null) {
                        // Check for Parameter not found error
                        if (errorMessage.contains("Wetland Parks Parameter") ||
                                errorMessage.contains("active parameter") ||
                                errorMessage.contains("No active Wetland Parks Parameter")) {
                            Map<String, Object> skipInfo = new HashMap<>();
                            skipInfo.put("row", excelRowNumber);
                            skipInfo.put("year", dto.getYear());
                            skipInfo.put("treeCategory", dto.getTreeCategory().name());
                            skipInfo.put("reason", errorMessage);
                            skippedParameterNotFound.add(skipInfo);
                            continue;
                        }
                        // Check for BAU not found error
                        if (errorMessage.contains("BAU record") && errorMessage.contains("not found")) {
                            Map<String, Object> skipInfo = new HashMap<>();
                            skipInfo.put("row", excelRowNumber);
                            skipInfo.put("year", dto.getYear());
                            skipInfo.put("treeCategory", dto.getTreeCategory().name());
                            skipInfo.put("reason", errorMessage);
                            skippedBAUNotFound.add(skipInfo);
                            continue;
                        }
                    }
                    // If it's a different error, re-throw it
                    throw e;
                }
            }

            // Calculate total skipped count
            int totalSkipped = skippedYearsAndCategories.size() + skippedParameterNotFound.size() +
                    skippedInterventionNotFound.size() + skippedMissingFields.size() +
                    skippedBAUNotFound.size();

            Map<String, Object> result = new HashMap<>();
            result.put("saved", savedRecords);
            result.put("savedCount", savedRecords.size());
            result.put("skippedCount", totalSkipped);
            result.put("skippedYearsAndCategories", skippedYearsAndCategories);
            result.put("skippedParameterNotFound", skippedParameterNotFound);
            result.put("skippedInterventionNotFound", skippedInterventionNotFound);
            result.put("skippedMissingFields", skippedMissingFields);
            result.put("skippedBAUNotFound", skippedBAUNotFound);
            result.put("totalProcessed", totalProcessed);

            return result;
        } catch (IOException e) {
            String message = e.getMessage();
            if (message != null) {
                throw new RuntimeException(message, e);
            } else {
                throw new RuntimeException(
                        "Incorrect template. Please download the correct template and try again.",
                        e);
            }
        } catch (NullPointerException e) {
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
