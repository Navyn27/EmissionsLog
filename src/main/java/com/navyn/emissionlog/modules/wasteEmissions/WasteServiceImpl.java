package com.navyn.emissionlog.modules.wasteEmissions;

import com.navyn.emissionlog.Enums.ExcelType;
import com.navyn.emissionlog.Enums.GWP;
import com.navyn.emissionlog.Enums.Scopes;
import com.navyn.emissionlog.Enums.Waste.SolidWasteType;
import com.navyn.emissionlog.Enums.Waste.WasteType;
import com.navyn.emissionlog.modules.eicvReports.EICVReport;
import com.navyn.emissionlog.modules.populationRecords.PopulationRecords;
import com.navyn.emissionlog.modules.regions.Region;
import com.navyn.emissionlog.modules.eicvReports.EICVReportRepository;
import com.navyn.emissionlog.modules.populationRecords.PopulationRecordsRepository;
import com.navyn.emissionlog.modules.regions.RegionRepository;
import com.navyn.emissionlog.utils.DashboardData;
import com.navyn.emissionlog.utils.ExcelReader;
import com.navyn.emissionlog.modules.wasteEmissions.models.*;
import com.navyn.emissionlog.modules.wasteEmissions.dtos.*;
import com.navyn.emissionlog.utils.Specifications.WasteSpecifications;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static java.util.stream.Collectors.groupingBy;

import static com.navyn.emissionlog.Enums.Waste.WasteType.SOLID_WASTE;

@Service
@RequiredArgsConstructor
public class WasteServiceImpl implements WasteService {

    private final PopulationRecordsRepository populationRecordRepository;
    private final RegionRepository regionRepository;
    private final WasteDataRepository wasteDataRepository;
    private final EICVReportRepository eicvReportRepository;

    @Override
    public WasteDataAbstract createIndustrialWasteWaterData(IndustrialWasteDto wasteData) {
        IndustrialWasteWaterData industrialWasteWaterData = new IndustrialWasteWaterData();
        industrialWasteWaterData.setWasteType(WasteType.INDUSTRIAL_WASTE_WATER);
        industrialWasteWaterData.setSugarProductionAmount(wasteData.getSugarProductionAmount());
        industrialWasteWaterData.setBeerProductionAmount(wasteData.getBeerProductionAmount());
        industrialWasteWaterData.setDairyProductionAmount(wasteData.getDairyProductionAmount());
        industrialWasteWaterData.setMeatAndPoultryProductionAmount(wasteData.getMeatAndPoultryProductionAmount());
        PopulationRecords populationRecords = populationRecordRepository.findById(wasteData.getPopulationRecords()).orElseThrow(() -> new RuntimeException("Population record not found"));

        industrialWasteWaterData.setPopulationRecords(populationRecords);
        industrialWasteWaterData.setScope(wasteData.getScope());
        industrialWasteWaterData.setRegion(regionRepository.findById(wasteData.getRegion()).orElseThrow(() -> new RuntimeException("Region not found")));

        // Handle activityYear: Use DTO's activityYear if year matches
        // populationRecords.year,
        // otherwise use populationRecords.year
        int populationYear = populationRecords.getYear();
        int dtoYear = wasteData.getActivityYear() != null ? wasteData.getActivityYear().getYear() : -1;

        LocalDateTime finalActivityYear;
        if (dtoYear == populationYear) {
            // Years match - use DTO's activityYear (respects user's date choice, handles
            // timezone)
            finalActivityYear = wasteData.getActivityYear();
        } else {
            // Years don't match - use populationRecords.year as source of truth
            finalActivityYear = LocalDateTime.of(populationYear, 12, 31, 23, 59, 59);
        }
        industrialWasteWaterData.setActivityYear(finalActivityYear);

        // Calculate emissions based on the production amounts
        industrialWasteWaterData.setFossilCO2Emissions(0.0);
        industrialWasteWaterData.setBioCO2Emissions(0.0);
        industrialWasteWaterData.setN2OEmissions(industrialWasteWaterData.calculateN2OEmissions());
        industrialWasteWaterData.setCH4Emissions(industrialWasteWaterData.calculateCH4Emissions());
        return wasteDataRepository.save(industrialWasteWaterData);
    }

    @Override
    public WasteDataAbstract createSolidWasteData(SolidWasteDto wasteData) {

        SolidWasteData latestSolidWasteRecord = (SolidWasteData) wasteDataRepository.findFirstByWasteTypeOrderByCreatedAtDesc(SOLID_WASTE);

        SolidWasteData solidWasteData = new SolidWasteData();
        solidWasteData.setWasteType(SOLID_WASTE);
        solidWasteData.setRegion(regionRepository.findById(wasteData.getRegion()).orElseThrow(() -> new RuntimeException("Region not found")));
        solidWasteData.setSolidWasteType(wasteData.getSolidWasteType());
        solidWasteData.setActivityYear(wasteData.getActivityYear());
        solidWasteData.setScope(wasteData.getScope());
        solidWasteData.setAmountDeposited(wasteData.getAmountDeposited());
        solidWasteData.setMethaneRecovery(wasteData.getMethaneRecovery());
        solidWasteData.setCH4Emissions(solidWasteData.calculateCH4Emissions(latestSolidWasteRecord == null ? 0.0 : latestSolidWasteRecord.getDDOCmAccumulated()));
        return wasteDataRepository.save(solidWasteData);
    }

    @Transactional
    @Override
    public WasteDataAbstract createWasteWaterData(WasteWaterDto wasteData) {
        WasteWaterData wasteWaterData = new WasteWaterData();
        wasteWaterData.setWasteType(WasteType.WASTE_WATER);

        // Step 1: Fetch all three entities first
        PopulationRecords populationRecords = populationRecordRepository.findById(wasteData.getPopulationRecords()).orElseThrow(() -> new RuntimeException("Population record not found"));

        Optional<EICVReport> providedEicvReport = eicvReportRepository.findById(wasteData.getEicvReport());
        if (providedEicvReport.isEmpty()) {
            throw new RuntimeException("EICV Report not found with id: " + wasteData.getEicvReport());
        }

        // Step 2: Extract years from all three sources
        int populationYear = populationRecords.getYear();
        int dtoYear = wasteData.getActivityYear() != null ? wasteData.getActivityYear().getYear() : -1;
        int providedEicvYear = providedEicvReport.get().getYear();

        // Step 3: Determine source of truth (populationRecords.year is authoritative)
        // Handle activityYear: Use DTO's activityYear if year matches
        // populationRecords.year
        LocalDateTime finalActivityYear;
        if (dtoYear == populationYear) {
            // Years match - use DTO's activityYear (respects user's date choice, handles
            // timezone)
            finalActivityYear = wasteData.getActivityYear();
        } else {
            // Years don't match - use populationRecords.year as source of truth
            finalActivityYear = LocalDateTime.of(populationYear, 12, 31, 23, 59, 59);
        }

        // Step 4: Validate and auto-correct EICVReport year
        EICVReport finalEicvReport;
        if (providedEicvYear == populationYear) {
            // EICVReport year matches - use provided EICVReport
            finalEicvReport = providedEicvReport.get();
        } else {
            // EICVReport year doesn't match - auto-find correct one
            finalEicvReport = findEICVReport(populationYear);
            if (finalEicvReport == null) {
                throw new RuntimeException("No EICV Report found for year " + populationYear + ". Population record year is " + populationYear + ", but provided EICV Report year is " + providedEicvYear + ". Please ensure EICV Report year matches population record year.");
            }
        }

        // Step 5: Set all validated values
        wasteWaterData.setPopulationRecords(populationRecords);
        wasteWaterData.setRegion(regionRepository.findById(wasteData.getRegion()).orElseThrow(() -> new RuntimeException("Region not found")));
        wasteWaterData.setScope(wasteData.getScope());
        wasteWaterData.setActivityYear(finalActivityYear);
        wasteWaterData.setEICVReport(finalEicvReport);

        // Reset BioCO2Emissions (waste water doesn't emit Bio CO2)
        wasteWaterData.setBioCO2Emissions(0.0);

        // Step 6: Calculate emissions
        wasteWaterData.setCH4Emissions(wasteWaterData.calculateCH4Emissions());
        wasteWaterData.setN2OEmissions(wasteWaterData.calculateN2OEmissions());
        return wasteDataRepository.save(wasteWaterData);
    }

    @Transactional
    @Override
    public WasteDataAbstract createBioTreatedWasteWaterData(GeneralWasteByPopulationDto wasteData) {
        BiologicallyTreatedWasteData biologicallyTreatedWasteData = new BiologicallyTreatedWasteData();
        biologicallyTreatedWasteData.setWasteType(WasteType.BIOLOGICALLY_TREATED_WASTE);

        // Get population record first to ensure year consistency
        PopulationRecords populationRecords = populationRecordRepository.findById(wasteData.getPopulationRecords()).orElseThrow(() -> new RuntimeException("Population record not found"));

        biologicallyTreatedWasteData.setPopulationRecords(populationRecords);
        biologicallyTreatedWasteData.setRegion(regionRepository.findById(wasteData.getRegion()).orElseThrow(() -> new RuntimeException("Region not found")));
        biologicallyTreatedWasteData.setScope(wasteData.getScope());

        // Handle activityYear: Use DTO's activityYear if year matches
        // populationRecords.year,
        // otherwise use populationRecords.year (calculations depend on
        // populationRecords.year)
        int populationYear = populationRecords.getYear();
        int dtoYear = wasteData.getActivityYear() != null ? wasteData.getActivityYear().getYear() : -1;

        LocalDateTime finalActivityYear;
        if (dtoYear == populationYear) {
            // Years match - use DTO's activityYear (respects user's date choice, handles
            // timezone)
            finalActivityYear = wasteData.getActivityYear();
        } else {
            // Years don't match - use populationRecords.year as source of truth
            // (calculations depend on populationRecords.year for CWFraction)
            finalActivityYear = LocalDateTime.of(populationYear, 12, 31, 23, 59, 59);
        }
        biologicallyTreatedWasteData.setActivityYear(finalActivityYear);
        biologicallyTreatedWasteData.setCH4Emissions(biologicallyTreatedWasteData.calculateCH4Emissions());
        biologicallyTreatedWasteData.setN2OEmissions(biologicallyTreatedWasteData.calculateN2OEmissions());
        return wasteDataRepository.save(biologicallyTreatedWasteData);
    }

    @Transactional
    @Override
    public WasteDataAbstract createBurntWasteData(GeneralWasteByPopulationDto wasteData) {
        BurningWasteData burningWasteData = new BurningWasteData();
        burningWasteData.setWasteType(WasteType.BURNT_WASTE);

        // Get population record first to ensure year consistency
        PopulationRecords populationRecords = populationRecordRepository.findById(wasteData.getPopulationRecords()).orElseThrow(() -> new RuntimeException("Population record not found"));

        burningWasteData.setPopulationRecords(populationRecords);
        burningWasteData.setRegion(regionRepository.findById(wasteData.getRegion()).orElseThrow(() -> new RuntimeException("Region not found")));
        burningWasteData.setScope(wasteData.getScope());

        // Handle activityYear: Use DTO's activityYear if year matches
        // populationRecords.year,
        // otherwise use populationRecords.year (calculations depend on
        // populationRecords.year)
        int populationYear = populationRecords.getYear();
        int dtoYear = wasteData.getActivityYear() != null ? wasteData.getActivityYear().getYear() : -1;

        LocalDateTime finalActivityYear;
        if (dtoYear == populationYear) {
            // Years match - use DTO's activityYear (respects user's date choice, handles
            // timezone)
            finalActivityYear = wasteData.getActivityYear();
        } else {
            // Years don't match - use populationRecords.year as source of truth
            finalActivityYear = LocalDateTime.of(populationYear, 12, 31, 23, 59, 59);
        }
        burningWasteData.setActivityYear(finalActivityYear);

        // Reset BioCO2Emissions (burnt waste emits Fossil CO2, not Bio CO2)
        burningWasteData.setBioCO2Emissions(0.0);

        // Calculate emissions
        burningWasteData.setCH4Emissions(burningWasteData.calculateCH4Emissions());
        burningWasteData.setN2OEmissions(burningWasteData.calculateN2OEmissions());
        burningWasteData.setFossilCO2Emissions(burningWasteData.calculateCO2Emissions());
        return wasteDataRepository.save(burningWasteData);
    }

    @Transactional
    @Override
    public WasteDataAbstract createIncinerationWasteData(GeneralWasteByPopulationDto wasteData) {
        IncinerationWasteData incinerationWasteData = new IncinerationWasteData();

        incinerationWasteData.setWasteType(WasteType.INCINERATED_WASTE);

        // Get population record first to ensure year consistency
        PopulationRecords populationRecords = populationRecordRepository.findById(wasteData.getPopulationRecords()).orElseThrow(() -> new RuntimeException("Population record not found"));

        incinerationWasteData.setPopulationRecords(populationRecords);
        incinerationWasteData.setRegion(regionRepository.findById(wasteData.getRegion()).orElseThrow(() -> new RuntimeException("Region not found")));
        incinerationWasteData.setScope(wasteData.getScope());

        // Handle activityYear: Use DTO's activityYear if year matches
        // populationRecords.year,
        // otherwise use populationRecords.year
        int populationYear = populationRecords.getYear();
        int dtoYear = wasteData.getActivityYear() != null ? wasteData.getActivityYear().getYear() : -1;

        LocalDateTime finalActivityYear;
        if (dtoYear == populationYear) {
            // Years match - use DTO's activityYear (respects user's date choice, handles
            // timezone)
            finalActivityYear = wasteData.getActivityYear();
        } else {
            // Years don't match - use populationRecords.year as source of truth
            finalActivityYear = LocalDateTime.of(populationYear, 12, 31, 23, 59, 59);
        }
        incinerationWasteData.setActivityYear(finalActivityYear);

        // Reset BioCO2Emissions (incineration emits Fossil CO2, not Bio CO2)
        incinerationWasteData.setBioCO2Emissions(0.0);

        incinerationWasteData.setCH4Emissions(incinerationWasteData.calculateCH4Emissions());
        incinerationWasteData.setN2OEmissions(incinerationWasteData.calculateN2OEmissions());
        incinerationWasteData.setFossilCO2Emissions(incinerationWasteData.calculateCO2Emissions());
        return wasteDataRepository.save(incinerationWasteData);
    }

    @Override
    public List<WasteDataAbstract> getAllWasteData() {
        List<WasteDataAbstract> result = wasteDataRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));

        // Ensure proper sorting with null safety (nulls last)
        result.sort((a, b) -> {
            if (a.getCreatedAt() == null && b.getCreatedAt() == null) return 0;
            if (a.getCreatedAt() == null) return 1; // nulls go to end
            if (b.getCreatedAt() == null) return -1; // nulls go to end
            return b.getCreatedAt().compareTo(a.getCreatedAt()); // descending
        });

        return result;
    }

    @Override
    public List<WasteDataAbstract> getWasteData(WasteType wasteType, Integer year, UUID regionId) {

        Specification<WasteDataAbstract> spec = Specification.where(WasteSpecifications.hasWasteType(wasteType)).and(WasteSpecifications.hasRegion(regionId)).and(WasteSpecifications.hasYear(year));

        List<WasteDataAbstract> result = wasteDataRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "createdAt"));

        // Ensure proper sorting with null safety (nulls last)
        result.sort((a, b) -> {
            if (a.getCreatedAt() == null && b.getCreatedAt() == null) return 0;
            if (a.getCreatedAt() == null) return 1; // nulls go to end
            if (b.getCreatedAt() == null) return -1; // nulls go to end
            return b.getCreatedAt().compareTo(a.getCreatedAt()); // descending
        });

        return result;
    }

    // populate population affiliated waste data
    @Override
    public List<WasteDataAbstract> populatePopulationAffiliatedWasteData() {

        // default region
        Region defaultRegion = regionRepository.findAll().stream().findFirst().orElseThrow(() -> new RuntimeException("No regions found in database"));

        List<WasteDataAbstract> wasteData = new ArrayList<>();
        List<PopulationRecords> populationRecords = populationRecordRepository.findAll();
        for (PopulationRecords populationRecord : populationRecords) {
            if (populationRecord.getYear() > 2022) {
                break;
            }

            // create waste water Dto
            WasteWaterDto wasteWaterDto = new WasteWaterDto();
            wasteWaterDto.setEicvReport(findEICVReport(populationRecord.getYear()).getId());
            wasteWaterDto.setPopulationRecords(populationRecord.getId());
            wasteWaterDto.setActivityYear(LocalDateTime.of(populationRecord.getYear(), 12, 31, 23, 59));
            wasteWaterDto.setScope(Scopes.SCOPE_1);
            wasteWaterDto.setRegion(defaultRegion.getId());

            // createDto
            GeneralWasteByPopulationDto generalWasteByPopulationDto = new GeneralWasteByPopulationDto();
            generalWasteByPopulationDto.setPopulationRecords(populationRecord.getId());
            generalWasteByPopulationDto.setActivityYear(LocalDateTime.of(populationRecord.getYear(), 12, 31, 23, 59));
            generalWasteByPopulationDto.setScope(Scopes.SCOPE_1);
            generalWasteByPopulationDto.setRegion(defaultRegion.getId());

            // create waste data
            wasteData.add(createWasteWaterData(wasteWaterDto));
            wasteData.add(createBioTreatedWasteWaterData(generalWasteByPopulationDto));
            wasteData.add(createBurntWasteData(generalWasteByPopulationDto));
            wasteData.add(createIncinerationWasteData(generalWasteByPopulationDto));
        }
        return wasteData;
    }

    @Override
    public List<WasteDataAbstract> populateIndustrialWasteData(MultipartFile file) throws IOException {
        List<IndustrialWasteExcelDto> industrialWasteExcelDtos = ExcelReader.readExcel(file.getInputStream(), IndustrialWasteExcelDto.class, ExcelType.INDUSTRIAL_WASTE_STARTER_DATA);
        List<WasteDataAbstract> savedRecords = new ArrayList<>();
        PopulationRecords populationRecords = null;
        for (IndustrialWasteExcelDto industrialWasteExcelDto : industrialWasteExcelDtos) {
            Region defaultRegion = regionRepository.findAll().stream().findFirst().orElseThrow(() -> new RuntimeException("No regions found in database"));
            populationRecords = populationRecordRepository.findByYear(industrialWasteExcelDto.getYear().intValue());
            if (populationRecords == null) {
                throw new RuntimeException("No population records found for the specified year " + industrialWasteExcelDto.getYear());
            }
            // createDto
            IndustrialWasteDto industrialWasteDto = new IndustrialWasteDto();
            industrialWasteDto.setSugarProductionAmount(industrialWasteExcelDto.getSugarProductionAmount());
            industrialWasteDto.setBeerProductionAmount(industrialWasteExcelDto.getBeerProductionAmount());
            industrialWasteDto.setPopulationRecords(populationRecords.getId());
            industrialWasteDto.setDairyProductionAmount(industrialWasteExcelDto.getDairyProductionAmount());
            industrialWasteDto.setMeatAndPoultryProductionAmount(industrialWasteExcelDto.getMeatAndPoultryProductionAmount());
            industrialWasteDto.setActivityYear(LocalDateTime.of(industrialWasteExcelDto.getYear().intValue(), 12, 31, 23, 59));
            industrialWasteDto.setScope(Scopes.SCOPE_1);
            industrialWasteDto.setRegion(defaultRegion.getId());
            // create waste data
            savedRecords.add(createIndustrialWasteWaterData(industrialWasteDto));
        }
        return savedRecords;
    }

    @Override
    public List<WasteDataAbstract> populateSolidWasteData(MultipartFile file) throws IOException {
        List<SolidWasteExcelDto> solidWasteExcelDtos = ExcelReader.readExcel(file.getInputStream(), SolidWasteExcelDto.class, ExcelType.SOLID_WASTE_STARTER_DATA);
        List<WasteDataAbstract> savedRecords = new ArrayList<>();
        Region defaultRegion = regionRepository.findAll().stream().findFirst().orElseThrow(() -> new RuntimeException("No regions found in database"));
        for (SolidWasteExcelDto solidWasteExcelDto : solidWasteExcelDtos) {
            // createDto
            SolidWasteDto solidWasteDto = new SolidWasteDto();
            solidWasteDto.setActivityYear(LocalDateTime.of(solidWasteExcelDto.getYear().intValue(), 12, 31, 23, 59));
            solidWasteDto.setScope(Scopes.SCOPE_1);
            solidWasteDto.setRegion(defaultRegion.getId());
            solidWasteDto.setMethaneRecovery(solidWasteExcelDto.getMethaneRecovery());
            // create waste data
            handleMultipleSolidWasteTypes(solidWasteExcelDto, savedRecords, solidWasteDto);
        }
        return savedRecords;
    }

    @Override
    public List<SolidWasteData> getSolidWasteData(SolidWasteType solidWasteType, Integer year, UUID regionId) {
        // Build specification for filtering
        Specification<WasteDataAbstract> spec = Specification.where(WasteSpecifications.hasWasteType(WasteType.SOLID_WASTE)).and(WasteSpecifications.hasRegion(regionId)).and(WasteSpecifications.hasYear(year));

        // Get all solid waste data matching the filters, sorted by createdAt
        List<WasteDataAbstract> wasteDataList = wasteDataRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "createdAt"));

        // Cast to SolidWasteData, filter by solidWasteType if provided, and return
        List<SolidWasteData> result = wasteDataList.stream().filter(w -> w instanceof SolidWasteData).map(w -> (SolidWasteData) w).filter(s -> solidWasteType == null || s.getSolidWasteType() == solidWasteType).collect(java.util.stream.Collectors.toList());

        // Ensure final result is sorted by createdAt (descending - latest first)
        result.sort((a, b) -> {
            if (a.getCreatedAt() == null && b.getCreatedAt() == null) return 0;
            if (a.getCreatedAt() == null) return 1;
            if (b.getCreatedAt() == null) return -1;
            return b.getCreatedAt().compareTo(a.getCreatedAt());
        });

        return result;
    }

    private void handleMultipleSolidWasteTypes(SolidWasteExcelDto solidWasteExcelDto, List<WasteDataAbstract> savedRecords, SolidWasteDto solidWasteDto) {
        // food
        solidWasteDto.setAmountDeposited(solidWasteExcelDto.getFoodDepositedAmount());
        solidWasteDto.setSolidWasteType(SolidWasteType.FOOD);
        savedRecords.add(createSolidWasteData(solidWasteDto));

        // paper
        solidWasteDto.setAmountDeposited(solidWasteExcelDto.getPaperDepositedAmount());
        solidWasteDto.setSolidWasteType(SolidWasteType.PAPER);
        savedRecords.add(createSolidWasteData(solidWasteDto));

        // sludge
        solidWasteDto.setAmountDeposited(solidWasteExcelDto.getSludgeDepositedAmount());
        solidWasteDto.setSolidWasteType(SolidWasteType.SLUDGE);
        savedRecords.add(createSolidWasteData(solidWasteDto));

        // msw
        solidWasteDto.setAmountDeposited(solidWasteExcelDto.getMswDepositedAmount());
        solidWasteDto.setSolidWasteType(SolidWasteType.MSW);
        savedRecords.add(createSolidWasteData(solidWasteDto));

        // industry
        solidWasteDto.setAmountDeposited(solidWasteExcelDto.getIndustryDepositedAmount());
        solidWasteDto.setSolidWasteType(SolidWasteType.INDUSTRY);
        savedRecords.add(createSolidWasteData(solidWasteDto));

        // garden
        solidWasteDto.setAmountDeposited(solidWasteExcelDto.getGardenDepositedAmount());
        solidWasteDto.setSolidWasteType(SolidWasteType.GARDEN);
        savedRecords.add(createSolidWasteData(solidWasteDto));

        // wood
        solidWasteDto.setAmountDeposited(solidWasteExcelDto.getWoodDepositedAmount());
        solidWasteDto.setSolidWasteType(SolidWasteType.WOOD);
        savedRecords.add(createSolidWasteData(solidWasteDto));

        // textile
        solidWasteDto.setAmountDeposited(solidWasteExcelDto.getTextilesDepositedAmount());
        solidWasteDto.setSolidWasteType(SolidWasteType.TEXTILES);
        savedRecords.add(createSolidWasteData(solidWasteDto));

        // nappies
        solidWasteDto.setAmountDeposited(solidWasteExcelDto.getNappiesDepositedAmount());
        solidWasteDto.setSolidWasteType(SolidWasteType.NAPPIES);
        savedRecords.add(createSolidWasteData(solidWasteDto));
    }

    private EICVReport findEICVReport(int year) {
        Optional<EICVReport> eicvReport = eicvReportRepository.findByYear(year);
        if (eicvReport.isEmpty()) {
            if (year > 2022) {
                return eicvReportRepository.findByYear(2022).isPresent() ? eicvReportRepository.findByYear(2022).get() : null;
            } else if (year < 2022 && year > 2017) {
                return eicvReportRepository.findByYear(2017).isPresent() ? eicvReportRepository.findByYear(2017).get() : null;
            } else if (year < 2017 && year > 2014) {
                return eicvReportRepository.findByYear(2014).isPresent() ? eicvReportRepository.findByYear(2014).get() : null;
            } else if (year < 2014 && year > 2011) {
                return eicvReportRepository.findByYear(2011).isPresent() ? eicvReportRepository.findByYear(2011).get() : null;
            } else if (year < 2011 && year > 2006) {
                return eicvReportRepository.findByYear(2006).isPresent() ? eicvReportRepository.findByYear(2006).get() : null;
            } else {
                return eicvReportRepository.findByYear(2000).isPresent() ? eicvReportRepository.findByYear(2000).get() : null;
            }
        }
        return eicvReport.get();
    }

    // ============= UPDATE METHODS =============

    @Transactional
    @Override
    public WasteDataAbstract updateIndustrialWasteWaterData(UUID id, IndustrialWasteDto wasteData) {
        WasteDataAbstract wasteDataAbstract = wasteDataRepository.findById(id).orElseThrow(() -> new RuntimeException("Industrial waste water data not found with id: " + id));

        // Validate it's the correct waste type
        if (wasteDataAbstract.getWasteType() != WasteType.INDUSTRIAL_WASTE_WATER) {
            throw new IllegalArgumentException("Record with ID " + id + " is not an industrial waste water record");
        }

        IndustrialWasteWaterData industrialWasteWaterData = (IndustrialWasteWaterData) wasteDataAbstract;

        industrialWasteWaterData.setSugarProductionAmount(wasteData.getSugarProductionAmount());
        industrialWasteWaterData.setBeerProductionAmount(wasteData.getBeerProductionAmount());
        industrialWasteWaterData.setDairyProductionAmount(wasteData.getDairyProductionAmount());
        industrialWasteWaterData.setMeatAndPoultryProductionAmount(wasteData.getMeatAndPoultryProductionAmount());

        // Get population record first to ensure year consistency
        PopulationRecords populationRecords = populationRecordRepository.findById(wasteData.getPopulationRecords()).orElseThrow(() -> new RuntimeException("Population record not found"));

        industrialWasteWaterData.setPopulationRecords(populationRecords);
        industrialWasteWaterData.setScope(wasteData.getScope());
        industrialWasteWaterData.setRegion(regionRepository.findById(wasteData.getRegion()).orElseThrow(() -> new RuntimeException("Region not found")));

        // Handle activityYear: Use DTO's activityYear if year matches
        // populationRecords.year,
        // otherwise use populationRecords.year
        int populationYear = populationRecords.getYear();
        int dtoYear = wasteData.getActivityYear() != null ? wasteData.getActivityYear().getYear() : -1;

        LocalDateTime finalActivityYear;
        if (dtoYear == populationYear) {
            // Years match - use DTO's activityYear (respects user's date choice, handles
            // timezone)
            finalActivityYear = wasteData.getActivityYear();
        } else {
            // Years don't match - use populationRecords.year as source of truth
            finalActivityYear = LocalDateTime.of(populationYear, 12, 31, 23, 59, 59);
        }
        industrialWasteWaterData.setActivityYear(finalActivityYear);

        // Recalculate emissions
        industrialWasteWaterData.setFossilCO2Emissions(0.0);
        industrialWasteWaterData.setBioCO2Emissions(0.0);
        industrialWasteWaterData.setN2OEmissions(industrialWasteWaterData.calculateN2OEmissions());
        industrialWasteWaterData.setCH4Emissions(industrialWasteWaterData.calculateCH4Emissions());

        return wasteDataRepository.save(industrialWasteWaterData);
    }

    @Override
    public WasteDataAbstract updateSolidWasteData(UUID id, SolidWasteDto wasteData) {
        SolidWasteData solidWasteData = (SolidWasteData) wasteDataRepository.findById(id).orElseThrow(() -> new RuntimeException("Solid waste data not found with id: " + id));

        // Find latest solid waste record excluding the current one being updated
        SolidWasteData latestSolidWasteRecord = (SolidWasteData) wasteDataRepository.findFirstByWasteTypeAndIdNotOrderByCreatedAtDesc(SOLID_WASTE, id);

        solidWasteData.setRegion(regionRepository.findById(wasteData.getRegion()).orElseThrow(() -> new RuntimeException("Region not found")));
        solidWasteData.setSolidWasteType(wasteData.getSolidWasteType());
        solidWasteData.setActivityYear(wasteData.getActivityYear());
        solidWasteData.setScope(wasteData.getScope());
        solidWasteData.setAmountDeposited(wasteData.getAmountDeposited());
        solidWasteData.setMethaneRecovery(wasteData.getMethaneRecovery());

        // Recalculate emissions using latest record's accumulated DDOCm (excluding
        // current record)
        solidWasteData.setCH4Emissions(solidWasteData.calculateCH4Emissions(latestSolidWasteRecord == null ? 0.0 : latestSolidWasteRecord.getDDOCmAccumulated()));

        return wasteDataRepository.save(solidWasteData);
    }

    @Transactional
    @Override
    public WasteDataAbstract updateWasteWaterData(UUID id, WasteWaterDto wasteData) {
        WasteDataAbstract wasteDataAbstract = wasteDataRepository.findById(id).orElseThrow(() -> new RuntimeException("Waste water data not found with id: " + id));

        // Validate it's the correct waste type
        if (wasteDataAbstract.getWasteType() != WasteType.WASTE_WATER) {
            throw new IllegalArgumentException("Record with ID " + id + " is not a waste water record");
        }

        WasteWaterData wasteWaterData = (WasteWaterData) wasteDataAbstract;

        // Step 1: Fetch all three entities first
        PopulationRecords populationRecords = populationRecordRepository.findById(wasteData.getPopulationRecords()).orElseThrow(() -> new RuntimeException("Population record not found"));

        Optional<EICVReport> providedEicvReport = eicvReportRepository.findById(wasteData.getEicvReport());
        if (providedEicvReport.isEmpty()) {
            throw new RuntimeException("EICV Report not found with id: " + wasteData.getEicvReport());
        }

        // Step 2: Extract years from all three sources
        int populationYear = populationRecords.getYear();
        int dtoYear = wasteData.getActivityYear() != null ? wasteData.getActivityYear().getYear() : -1;
        int providedEicvYear = providedEicvReport.get().getYear();

        // Step 3: Determine source of truth (populationRecords.year is authoritative)
        // Handle activityYear: Use DTO's activityYear if year matches
        // populationRecords.year
        LocalDateTime finalActivityYear;
        if (dtoYear == populationYear) {
            // Years match - use DTO's activityYear (respects user's date choice, handles
            // timezone)
            finalActivityYear = wasteData.getActivityYear();
        } else {
            // Years don't match - use populationRecords.year as source of truth
            finalActivityYear = LocalDateTime.of(populationYear, 12, 31, 23, 59, 59);
        }

        // Step 4: Validate and auto-correct EICVReport year
        EICVReport finalEicvReport;
        if (providedEicvYear == populationYear) {
            // EICVReport year matches - use provided EICVReport
            finalEicvReport = providedEicvReport.get();
        } else {
            // EICVReport year doesn't match - auto-find correct one
            finalEicvReport = findEICVReport(populationYear);
            if (finalEicvReport == null) {
                throw new RuntimeException("No EICV Report found for year " + populationYear + ". Population record year is " + populationYear + ", but provided EICV Report year is " + providedEicvYear + ". Please ensure EICV Report year matches population record year.");
            }
        }

        // Step 5: Set all validated values
        wasteWaterData.setPopulationRecords(populationRecords);
        wasteWaterData.setRegion(regionRepository.findById(wasteData.getRegion()).orElseThrow(() -> new RuntimeException("Region not found")));
        wasteWaterData.setScope(wasteData.getScope());
        wasteWaterData.setActivityYear(finalActivityYear);
        wasteWaterData.setEICVReport(finalEicvReport);

        // Reset BioCO2Emissions (waste water doesn't emit Bio CO2)
        wasteWaterData.setBioCO2Emissions(0.0);

        // Step 6: Recalculate emissions
        wasteWaterData.setCH4Emissions(wasteWaterData.calculateCH4Emissions());
        wasteWaterData.setN2OEmissions(wasteWaterData.calculateN2OEmissions());

        return wasteDataRepository.save(wasteWaterData);
    }

    @Transactional
    @Override
    public WasteDataAbstract updateBioTreatedWasteWaterData(UUID id, GeneralWasteByPopulationDto wasteData) {
        WasteDataAbstract wasteDataAbstract = wasteDataRepository.findById(id).orElseThrow(() -> new RuntimeException("Biologically treated waste water data not found with id: " + id));

        // Validate it's the correct waste type
        if (wasteDataAbstract.getWasteType() != WasteType.BIOLOGICALLY_TREATED_WASTE) {
            throw new IllegalArgumentException("Record with ID " + id + " is not a biologically treated waste record");
        }

        BiologicallyTreatedWasteData biologicallyTreatedWasteData = (BiologicallyTreatedWasteData) wasteDataAbstract;

        // Get population record first to ensure year consistency
        PopulationRecords populationRecords = populationRecordRepository.findById(wasteData.getPopulationRecords()).orElseThrow(() -> new RuntimeException("Population record not found"));

        biologicallyTreatedWasteData.setPopulationRecords(populationRecords);
        biologicallyTreatedWasteData.setRegion(regionRepository.findById(wasteData.getRegion()).orElseThrow(() -> new RuntimeException("Region not found")));
        biologicallyTreatedWasteData.setScope(wasteData.getScope());

        // Handle activityYear: Use DTO's activityYear if year matches
        // populationRecords.year,
        // otherwise use populationRecords.year (calculations depend on
        // populationRecords.year)
        int populationYear = populationRecords.getYear();
        int dtoYear = wasteData.getActivityYear() != null ? wasteData.getActivityYear().getYear() : -1;

        LocalDateTime finalActivityYear;
        if (dtoYear == populationYear) {
            // Years match - use DTO's activityYear (respects user's date choice, handles
            // timezone)
            finalActivityYear = wasteData.getActivityYear();
        } else {
            // Years don't match - use populationRecords.year as source of truth
            // (calculations depend on populationRecords.year for CWFraction)
            finalActivityYear = LocalDateTime.of(populationYear, 12, 31, 23, 59, 59);
        }
        biologicallyTreatedWasteData.setActivityYear(finalActivityYear);

        // Reset CO2 emissions (biological waste doesn't emit CO2)
        biologicallyTreatedWasteData.setFossilCO2Emissions(0.0);
        biologicallyTreatedWasteData.setBioCO2Emissions(0.0);

        // Recalculate emissions
        biologicallyTreatedWasteData.setCH4Emissions(biologicallyTreatedWasteData.calculateCH4Emissions());
        biologicallyTreatedWasteData.setN2OEmissions(biologicallyTreatedWasteData.calculateN2OEmissions());

        return wasteDataRepository.save(biologicallyTreatedWasteData);
    }

    @Transactional
    @Override
    public WasteDataAbstract updateBurntWasteData(UUID id, GeneralWasteByPopulationDto wasteData) {
        WasteDataAbstract wasteDataAbstract = wasteDataRepository.findById(id).orElseThrow(() -> new RuntimeException("Burning waste data not found with id: " + id));

        // Validate it's the correct waste type
        if (wasteDataAbstract.getWasteType() != WasteType.BURNT_WASTE) {
            throw new IllegalArgumentException("Record with ID " + id + " is not a burnt waste record");
        }

        BurningWasteData burningWasteData = (BurningWasteData) wasteDataAbstract;

        // Get population record first to ensure year consistency
        PopulationRecords populationRecords = populationRecordRepository.findById(wasteData.getPopulationRecords()).orElseThrow(() -> new RuntimeException("Population record not found"));

        burningWasteData.setPopulationRecords(populationRecords);
        burningWasteData.setRegion(regionRepository.findById(wasteData.getRegion()).orElseThrow(() -> new RuntimeException("Region not found")));
        burningWasteData.setScope(wasteData.getScope());

        // Handle activityYear: Use DTO's activityYear if year matches
        // populationRecords.year,
        // otherwise use populationRecords.year (calculations depend on
        // populationRecords.year)
        int populationYear = populationRecords.getYear();
        int dtoYear = wasteData.getActivityYear() != null ? wasteData.getActivityYear().getYear() : -1;

        LocalDateTime finalActivityYear;
        if (dtoYear == populationYear) {
            // Years match - use DTO's activityYear (respects user's date choice, handles
            // timezone)
            finalActivityYear = wasteData.getActivityYear();
        } else {
            // Years don't match - use populationRecords.year as source of truth
            finalActivityYear = LocalDateTime.of(populationYear, 12, 31, 23, 59, 59);
        }
        burningWasteData.setActivityYear(finalActivityYear);

        // Reset BioCO2Emissions (burnt waste emits Fossil CO2, not Bio CO2)
        burningWasteData.setBioCO2Emissions(0.0);

        // Recalculate emissions
        burningWasteData.setCH4Emissions(burningWasteData.calculateCH4Emissions());
        burningWasteData.setN2OEmissions(burningWasteData.calculateN2OEmissions());
        burningWasteData.setFossilCO2Emissions(burningWasteData.calculateCO2Emissions());

        return wasteDataRepository.save(burningWasteData);
    }

    @Transactional
    @Override
    public WasteDataAbstract updateIncinerationWasteData(UUID id, GeneralWasteByPopulationDto wasteData) {
        WasteDataAbstract wasteDataAbstract = wasteDataRepository.findById(id).orElseThrow(() -> new RuntimeException("Incineration waste data not found with id: " + id));

        // Validate it's the correct waste type
        if (wasteDataAbstract.getWasteType() != WasteType.INCINERATED_WASTE) {
            throw new IllegalArgumentException("Record with ID " + id + " is not an incineration waste record");
        }

        IncinerationWasteData incinerationWasteData = (IncinerationWasteData) wasteDataAbstract;

        // Get population record first to ensure year consistency
        PopulationRecords populationRecords = populationRecordRepository.findById(wasteData.getPopulationRecords()).orElseThrow(() -> new RuntimeException("Population record not found"));

        incinerationWasteData.setPopulationRecords(populationRecords);
        incinerationWasteData.setRegion(regionRepository.findById(wasteData.getRegion()).orElseThrow(() -> new RuntimeException("Region not found")));
        incinerationWasteData.setScope(wasteData.getScope());

        // Handle activityYear: Use DTO's activityYear if year matches
        // populationRecords.year,
        // otherwise use populationRecords.year
        int populationYear = populationRecords.getYear();
        int dtoYear = wasteData.getActivityYear() != null ? wasteData.getActivityYear().getYear() : -1;

        LocalDateTime finalActivityYear;
        if (dtoYear == populationYear) {
            // Years match - use DTO's activityYear (respects user's date choice, handles
            // timezone)
            finalActivityYear = wasteData.getActivityYear();
        } else {
            // Years don't match - use populationRecords.year as source of truth
            finalActivityYear = LocalDateTime.of(populationYear, 12, 31, 23, 59, 59);
        }
        incinerationWasteData.setActivityYear(finalActivityYear);

        // Reset BioCO2Emissions (incineration emits Fossil CO2, not Bio CO2)
        incinerationWasteData.setBioCO2Emissions(0.0);

        // Recalculate emissions
        incinerationWasteData.setCH4Emissions(incinerationWasteData.calculateCH4Emissions());
        incinerationWasteData.setN2OEmissions(incinerationWasteData.calculateN2OEmissions());
        incinerationWasteData.setFossilCO2Emissions(incinerationWasteData.calculateCO2Emissions());

        return wasteDataRepository.save(incinerationWasteData);
    }

    // ============= MINI DASHBOARDS =============

    @Override
    public DashboardData getWasteDashboardSummary(Integer startingYear, Integer endingYear) {
        List<WasteDataAbstract> wasteData = wasteDataRepository.findAll();

        if (startingYear != null && endingYear != null) {
            wasteData = wasteData.stream().filter(w -> w.getYear() >= startingYear && w.getYear() <= endingYear).toList();
        }

        return calculateWasteDashboardData(wasteData);
    }

    @Override
    public List<DashboardData> getWasteDashboardGraph(Integer startingYear, Integer endingYear) {
        List<WasteDataAbstract> wasteData = wasteDataRepository.findAll();

        // Default to last 5 years if not specified
        if (startingYear == null || endingYear == null) {
            int currentYear = LocalDateTime.now().getYear();
            startingYear = currentYear - 4;
            endingYear = currentYear;
        }

        // Filter by year range
        final int finalStartYear = startingYear;
        final int finalEndYear = endingYear;
        wasteData = wasteData.stream().filter(w -> w.getYear() >= finalStartYear && w.getYear() <= finalEndYear).toList();

        // Group by year
        Map<Integer, List<WasteDataAbstract>> groupedByYear = wasteData.stream().collect(groupingBy(WasteDataAbstract::getYear));

        // Create dashboard data for each year
        List<DashboardData> dashboardDataList = new ArrayList<>();
        for (int year = startingYear; year <= endingYear; year++) {
            List<WasteDataAbstract> yearWaste = groupedByYear.getOrDefault(year, List.of());
            DashboardData data = calculateWasteDashboardData(yearWaste);
            data.setStartingDate(LocalDateTime.of(year, 1, 1, 0, 0).toString());
            data.setEndingDate(LocalDateTime.of(year, 12, 31, 23, 59).toString());
            data.setYear(Year.of(year));
            dashboardDataList.add(data);
        }

        return dashboardDataList;
    }

    private DashboardData calculateWasteDashboardData(List<WasteDataAbstract> wasteData) {
        DashboardData dashboardData = new DashboardData();

        for (WasteDataAbstract waste : wasteData) {
            dashboardData.setTotalCH4Emissions(dashboardData.getTotalCH4Emissions() + waste.getCH4Emissions());
            dashboardData.setTotalN2OEmissions(dashboardData.getTotalN2OEmissions() + waste.getN2OEmissions());
            dashboardData.setTotalFossilCO2Emissions(dashboardData.getTotalFossilCO2Emissions() + waste.getFossilCO2Emissions());
            dashboardData.setTotalBioCO2Emissions(dashboardData.getTotalBioCO2Emissions() + waste.getBioCO2Emissions());
        }

        // Calculate CO2 equivalent
        dashboardData.setTotalCO2EqEmissions(dashboardData.getTotalFossilCO2Emissions() + dashboardData.getTotalBioCO2Emissions() + dashboardData.getTotalCH4Emissions() * GWP.CH4.getValue() + dashboardData.getTotalN2OEmissions() * GWP.N2O.getValue());

        return dashboardData;
    }

    // ============= DELETE METHODS =============

    @Transactional
    @Override
    public void deleteIndustrialWasteWaterData(UUID id) {
        WasteDataAbstract wasteData = wasteDataRepository.findById(id).orElseThrow(() -> new RuntimeException("Industrial waste water data not found with id: " + id));

        // Validate it's the correct waste type
        if (wasteData.getWasteType() != WasteType.INDUSTRIAL_WASTE_WATER) {
            throw new IllegalArgumentException("Record with ID " + id + " is not an industrial waste water record");
        }

        wasteDataRepository.delete(wasteData);
    }

    @Transactional
    @Override
    public void deleteSolidWasteData(UUID id) {
        WasteDataAbstract wasteData = wasteDataRepository.findById(id).orElseThrow(() -> new RuntimeException("Solid waste data not found with id: " + id));

        // Validate it's the correct waste type
        if (wasteData.getWasteType() != WasteType.SOLID_WASTE) {
            throw new IllegalArgumentException("Record with ID " + id + " is not a solid waste record");
        }

        wasteDataRepository.delete(wasteData);
    }

    @Transactional
    @Override
    public void deleteWasteWaterData(UUID id) {
        WasteDataAbstract wasteData = wasteDataRepository.findById(id).orElseThrow(() -> new RuntimeException("Waste water data not found with id: " + id));

        // Validate it's the correct waste type
        if (wasteData.getWasteType() != WasteType.WASTE_WATER) {
            throw new IllegalArgumentException("Record with ID " + id + " is not a waste water record");
        }

        wasteDataRepository.delete(wasteData);
    }

    @Transactional
    @Override
    public void deleteBioTreatedWasteWaterData(UUID id) {
        WasteDataAbstract wasteData = wasteDataRepository.findById(id).orElseThrow(() -> new RuntimeException("Biologically treated waste data not found with id: " + id));

        // Validate it's the correct waste type
        if (wasteData.getWasteType() != WasteType.BIOLOGICALLY_TREATED_WASTE) {
            throw new IllegalArgumentException("Record with ID " + id + " is not a biologically treated waste record");
        }

        wasteDataRepository.delete(wasteData);
    }

    @Transactional
    @Override
    public void deleteBurntWasteData(UUID id) {
        WasteDataAbstract wasteData = wasteDataRepository.findById(id).orElseThrow(() -> new RuntimeException("Burnt waste data not found with id: " + id));

        // Validate it's the correct waste type
        if (wasteData.getWasteType() != WasteType.BURNT_WASTE) {
            throw new IllegalArgumentException("Record with ID " + id + " is not a burnt waste record");
        }

        wasteDataRepository.delete(wasteData);
    }

    @Transactional
    @Override
    public void deleteIncinerationWasteData(UUID id) {
        WasteDataAbstract wasteData = wasteDataRepository.findById(id).orElseThrow(() -> new RuntimeException("Incineration waste data not found with id: " + id));

        // Validate it's the correct waste type
        if (wasteData.getWasteType() != WasteType.INCINERATED_WASTE) {
            throw new IllegalArgumentException("Record with ID " + id + " is not an incineration waste record");
        }

        wasteDataRepository.delete(wasteData);
    }

}
