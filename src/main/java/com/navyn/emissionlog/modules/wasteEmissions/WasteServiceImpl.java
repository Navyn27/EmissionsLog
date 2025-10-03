package com.navyn.emissionlog.modules.wasteEmissions;

import com.navyn.emissionlog.Enums.ExcelType;
import com.navyn.emissionlog.Enums.Scopes;
import com.navyn.emissionlog.Enums.Waste.SolidWasteType;
import com.navyn.emissionlog.Enums.Waste.WasteType;
import com.navyn.emissionlog.modules.eicvReports.EICVReport;
import com.navyn.emissionlog.modules.populationRecords.PopulationRecords;
import com.navyn.emissionlog.modules.regions.Region;
import com.navyn.emissionlog.modules.eicvReports.EICVReportRepository;
import com.navyn.emissionlog.modules.populationRecords.PopulationRecordsRepository;
import com.navyn.emissionlog.modules.regions.RegionRepository;
import com.navyn.emissionlog.utils.ExcelReader;
import com.navyn.emissionlog.modules.wasteEmissions.models.*;
import com.navyn.emissionlog.modules.wasteEmissions.dtos.*;
import com.navyn.emissionlog.utils.Specifications.WasteSpecifications;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
        industrialWasteWaterData.setPopulationRecords(populationRecordRepository.findById(wasteData.getPopulationRecords())
                .orElseThrow(() -> new RuntimeException("Population record not found")));
        industrialWasteWaterData.setScope(wasteData.getScope());
        industrialWasteWaterData.setRegion(regionRepository.findById(wasteData.getRegion())
                .orElseThrow(() -> new RuntimeException("Region not found")));
        industrialWasteWaterData.setActivityYear(wasteData.getActivityYear());

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
        solidWasteData.setRegion(regionRepository.findById(wasteData.getRegion())
                .orElseThrow(() -> new RuntimeException("Region not found")));
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
        //Find year's EICV Report
//        EICVReport eicvReport = eicvReportRepository.findByYear(wasteData.getActivityYear().getYear());
        Optional<EICVReport> eicvReport = eicvReportRepository.findById(wasteData.getEicvReport());

        WasteWaterData wasteWaterData = new WasteWaterData();
        wasteWaterData.setWasteType(WasteType.WASTE_WATER);
        wasteWaterData.setPopulationRecords(populationRecordRepository.findById(wasteData.getPopulationRecords())
                .orElseThrow(() -> new RuntimeException("Population record not found")));
        wasteWaterData.setRegion(regionRepository.findById(wasteData.getRegion())
                .orElseThrow(() -> new RuntimeException("Region not found")));
        wasteWaterData.setActivityYear(wasteData.getActivityYear());
        wasteWaterData.setScope(wasteData.getScope());
        wasteWaterData.setEICVReport(eicvReport.get());
        wasteWaterData.setCH4Emissions(wasteWaterData.calculateCH4Emissions());
        return wasteDataRepository.save(wasteWaterData);
    }

    @Transactional
    @Override
    public WasteDataAbstract createBioTreatedWasteWaterData(GeneralWasteByPopulationDto wasteData) {
        BiologicallyTreatedWasteData biologicallyTreatedWasteData = new BiologicallyTreatedWasteData();
        biologicallyTreatedWasteData.setWasteType(WasteType.BIOLOGICALLY_TREATED_WASTE);
        biologicallyTreatedWasteData.setPopulationRecords(populationRecordRepository.findById(wasteData.getPopulationRecords())
                .orElseThrow(() -> new RuntimeException("Population record not found")));
        biologicallyTreatedWasteData.setRegion(regionRepository.findById(wasteData.getRegion())
                .orElseThrow(() -> new RuntimeException("Region not found")));
        biologicallyTreatedWasteData.setActivityYear(wasteData.getActivityYear());
        biologicallyTreatedWasteData.setScope(wasteData.getScope());
        biologicallyTreatedWasteData.setCH4Emissions(biologicallyTreatedWasteData.calculateCH4Emissions());
        biologicallyTreatedWasteData.setN2OEmissions(biologicallyTreatedWasteData.calculateN2OEmissions());
        return wasteDataRepository.save(biologicallyTreatedWasteData);
    }

    @Transactional
    @Override
    public WasteDataAbstract createBurntWasteData(GeneralWasteByPopulationDto wasteData) {
        BurningWasteData burningWasteData = new BurningWasteData();
        burningWasteData.setWasteType(WasteType.BURNT_WASTE);
        burningWasteData.setPopulationRecords(populationRecordRepository.findById(wasteData.getPopulationRecords())
                .orElseThrow(() -> new RuntimeException("Population record not found")));
        burningWasteData.setRegion(regionRepository.findById(wasteData.getRegion())
                .orElseThrow(() -> new RuntimeException("Region not found")));
        burningWasteData.setActivityYear(wasteData.getActivityYear());
        burningWasteData.setScope(wasteData.getScope());
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
        incinerationWasteData.setPopulationRecords(populationRecordRepository.findById(wasteData.getPopulationRecords())
                .orElseThrow(() -> new RuntimeException("Population record not found")));
        incinerationWasteData.setRegion(regionRepository.findById(wasteData.getRegion())
                .orElseThrow(() -> new RuntimeException("Region not found")));
        incinerationWasteData.setActivityYear(wasteData.getActivityYear());
        incinerationWasteData.setScope(wasteData.getScope());
        incinerationWasteData.setCH4Emissions(incinerationWasteData.calculateCH4Emissions());
        incinerationWasteData.setN2OEmissions(incinerationWasteData.calculateN2OEmissions());
        incinerationWasteData.setFossilCO2Emissions(incinerationWasteData.calculateCO2Emissions());
        return wasteDataRepository.save(incinerationWasteData);
    }

    @Override
    public List<WasteDataAbstract> getAllWasteData() {
        return wasteDataRepository.findAll();
    }

    @Override
    public List<WasteDataAbstract> getWasteData(WasteType wasteType, Integer year, UUID regionId) {

        Specification<WasteDataAbstract> spec = Specification
                .where(WasteSpecifications.hasWasteType(wasteType))
                .and(WasteSpecifications.hasRegion(regionId))
                .and(WasteSpecifications.hasYear(year));

        return wasteDataRepository.findAll(spec);
    }

    //populate population affiliated waste data
    @Override
    public List<WasteDataAbstract>  populatePopulationAffiliatedWasteData() {

        //default region
        Region defaultRegion = regionRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No regions found in database"));

        List<WasteDataAbstract> wasteData = new ArrayList<>();
        List<PopulationRecords> populationRecords = populationRecordRepository.findAll();
        for(PopulationRecords populationRecord: populationRecords){
            if(populationRecord.getYear() > 2022){
                break;
            }

            //create waste water Dto
            WasteWaterDto  wasteWaterDto = new WasteWaterDto();
            wasteWaterDto.setEicvReport(findEICVReport(populationRecord.getYear()).getId());
            wasteWaterDto.setPopulationRecords(populationRecord.getId());
            wasteWaterDto.setActivityYear(LocalDateTime.of(populationRecord.getYear(),12,31,23,59));
            wasteWaterDto.setScope(Scopes.SCOPE_1);
            wasteWaterDto.setRegion(defaultRegion.getId());

            //createDto
            GeneralWasteByPopulationDto generalWasteByPopulationDto = new GeneralWasteByPopulationDto();
            generalWasteByPopulationDto.setPopulationRecords(populationRecord.getId());
            generalWasteByPopulationDto.setActivityYear(LocalDateTime.of(populationRecord.getYear(),12,31,23,59));
            generalWasteByPopulationDto.setScope(Scopes.SCOPE_1);
            generalWasteByPopulationDto.setRegion(defaultRegion.getId());

            //create waste data
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
        for(IndustrialWasteExcelDto industrialWasteExcelDto : industrialWasteExcelDtos){
            Region defaultRegion = regionRepository.findAll()
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No regions found in database"));
            populationRecords = populationRecordRepository.findByYear(industrialWasteExcelDto.getYear().intValue());
            if(populationRecords == null){
                throw new RuntimeException("No population records found for the specified year " + industrialWasteExcelDto.getYear());
            }
            //createDto
            IndustrialWasteDto industrialWasteDto = new IndustrialWasteDto();
            industrialWasteDto.setSugarProductionAmount(industrialWasteExcelDto.getSugarProductionAmount());
            industrialWasteDto.setBeerProductionAmount(industrialWasteExcelDto.getBeerProductionAmount());
            industrialWasteDto.setPopulationRecords(populationRecords.getId());
            industrialWasteDto.setDairyProductionAmount(industrialWasteExcelDto.getDairyProductionAmount());
            industrialWasteDto.setMeatAndPoultryProductionAmount(industrialWasteExcelDto.getMeatAndPoultryProductionAmount());
            industrialWasteDto.setActivityYear(LocalDateTime.of(industrialWasteExcelDto.getYear().intValue(),12,31,23,59));
            industrialWasteDto.setScope(Scopes.SCOPE_1);
            industrialWasteDto.setRegion(defaultRegion.getId());
            //create waste data
            savedRecords.add(createIndustrialWasteWaterData(industrialWasteDto));
        }
        return savedRecords;
    }

    @Override
    public List<WasteDataAbstract> populateSolidWasteData(MultipartFile file) throws IOException {
        List<SolidWasteExcelDto> solidWasteExcelDtos = ExcelReader.readExcel(file.getInputStream(), SolidWasteExcelDto.class, ExcelType.SOLID_WASTE_STARTER_DATA);
        List<WasteDataAbstract> savedRecords = new ArrayList<>();
        Region defaultRegion = regionRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No regions found in database"));
        for(SolidWasteExcelDto solidWasteExcelDto : solidWasteExcelDtos){
            //createDto
            SolidWasteDto solidWasteDto = new SolidWasteDto();
            solidWasteDto.setActivityYear(LocalDateTime.of(solidWasteExcelDto.getYear().intValue(),12,31,23,59));
            solidWasteDto.setScope(Scopes.SCOPE_1);
            solidWasteDto.setRegion(defaultRegion.getId());
            solidWasteDto.setMethaneRecovery(solidWasteExcelDto.getMethaneRecovery());
            //create waste data
            handleMultipleSolidWasteTypes(solidWasteExcelDto, savedRecords, solidWasteDto);
        }
        return savedRecords;
    }

    @Override
    public List<SolidWasteData> getSolidWasteData(SolidWasteType solidWasteType, Integer year, UUID regionId) {
        List<SolidWasteData> solidWasteDataList = new ArrayList<>();

        if(solidWasteType == null){
            for(SolidWasteType type : SolidWasteType.values()) {
                solidWasteDataList.addAll(wasteDataRepository.findAllBySolidWasteType(type));
            }
            return solidWasteDataList;
        }

        Specification<SolidWasteData> spec = Specification
                .where(WasteSpecifications.hasSolidWasteType(solidWasteType))
                .and(WasteSpecifications.hasRegion_solidWaste(regionId))
                .and(WasteSpecifications.hasYear(year));

        return wasteDataRepository.findAllBySolidWasteType(solidWasteType);
    }

    private void handleMultipleSolidWasteTypes(SolidWasteExcelDto solidWasteExcelDto, List<WasteDataAbstract> savedRecords, SolidWasteDto solidWasteDto){
        //food
        solidWasteDto.setAmountDeposited(solidWasteExcelDto.getFoodDepositedAmount());
        solidWasteDto.setSolidWasteType(SolidWasteType.FOOD);
        savedRecords.add(createSolidWasteData(solidWasteDto));

        //paper
        solidWasteDto.setAmountDeposited(solidWasteExcelDto.getPaperDepositedAmount());
        solidWasteDto.setSolidWasteType(SolidWasteType.PAPER);
        savedRecords.add(createSolidWasteData(solidWasteDto));

        //sludge
        solidWasteDto.setAmountDeposited(solidWasteExcelDto.getSludgeDepositedAmount());
        solidWasteDto.setSolidWasteType(SolidWasteType.SLUDGE);
        savedRecords.add(createSolidWasteData(solidWasteDto));

        //msw
        solidWasteDto.setAmountDeposited(solidWasteExcelDto.getMswDepositedAmount());
        solidWasteDto.setSolidWasteType(SolidWasteType.MSW);
        savedRecords.add(createSolidWasteData(solidWasteDto));

        //industry
        solidWasteDto.setAmountDeposited(solidWasteExcelDto.getIndustryDepositedAmount());
        solidWasteDto.setSolidWasteType(SolidWasteType.INDUSTRY);
        savedRecords.add(createSolidWasteData(solidWasteDto));

        //garden
        solidWasteDto.setAmountDeposited(solidWasteExcelDto.getGardenDepositedAmount());
        solidWasteDto.setSolidWasteType(SolidWasteType.GARDEN);
        savedRecords.add(createSolidWasteData(solidWasteDto));

        //wood
        solidWasteDto.setAmountDeposited(solidWasteExcelDto.getWoodDepositedAmount());
        solidWasteDto.setSolidWasteType(SolidWasteType.WOOD);
        savedRecords.add(createSolidWasteData(solidWasteDto));

        //textile
        solidWasteDto.setAmountDeposited(solidWasteExcelDto.getTextilesDepositedAmount());
        solidWasteDto.setSolidWasteType(SolidWasteType.TEXTILES);
        savedRecords.add(createSolidWasteData(solidWasteDto));

        //nappies
        solidWasteDto.setAmountDeposited(solidWasteExcelDto.getNappiesDepositedAmount());
        solidWasteDto.setSolidWasteType(SolidWasteType.NAPPIES);
        savedRecords.add(createSolidWasteData(solidWasteDto));
    }

    private EICVReport findEICVReport(int year) {
        Optional<EICVReport> eicvReport = eicvReportRepository.findByYear(year);
        if(eicvReport.isEmpty()){
            if(year>2022){
                return eicvReportRepository.findByYear(2022).get();
            }
            else if(year<2022 && year > 2017){
                return eicvReportRepository.findByYear(2017).get();
            }
            else if(year<2017 && year > 2014){
                return eicvReportRepository.findByYear(2014).get();
            }
            else if(year<2014 && year > 2011){
                return eicvReportRepository.findByYear(2011).get();
            }
            else if(year<2011 && year > 2006){
                return eicvReportRepository.findByYear(2006).get();
            }
            else{
                return eicvReportRepository.findByYear(2000).get();
            }
        }
        return eicvReport.get();
    }

}
