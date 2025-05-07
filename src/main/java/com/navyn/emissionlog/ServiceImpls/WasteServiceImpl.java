package com.navyn.emissionlog.ServiceImpls;

import com.navyn.emissionlog.Enums.WasteType;
import com.navyn.emissionlog.Models.EICVReport;
import com.navyn.emissionlog.Models.WasteData.*;
import com.navyn.emissionlog.Payload.Requests.Waste.GeneralWasteByPopulationDto;
import com.navyn.emissionlog.Payload.Requests.Waste.IndustrialWasteDto;
import com.navyn.emissionlog.Payload.Requests.Waste.SolidWasteDto;
import com.navyn.emissionlog.Repositories.EICVReportRepository;
import com.navyn.emissionlog.Repositories.PopulationRecordsRepository;
import com.navyn.emissionlog.Repositories.RegionRepository;
import com.navyn.emissionlog.Repositories.WasteDataRepository;
import com.navyn.emissionlog.Services.WasteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WasteServiceImpl implements WasteService {

    @Autowired
    private PopulationRecordsRepository populationRecordRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private WasteDataRepository wasteDataRepository;

    @Autowired
    private EICVReportRepository eicvReportRepository;

    @Override
    public WasteDataAbstract createIndustrialWasteWaterData(IndustrialWasteDto wasteData) {
        IndustrialWasteWaterData industrialWasteWaterData = new IndustrialWasteWaterData();
        industrialWasteWaterData.setWasteType(WasteType.INDUSTRIAL_WASTE_WATER);
        industrialWasteWaterData.setSugarProductionAmount(wasteData.getSugarProductionAmount());
        industrialWasteWaterData.setBearProductionAmount(wasteData.getBearProductionAmount());
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
        industrialWasteWaterData.setCH4Emissions(0.0);
        industrialWasteWaterData.setN2OEmissions(industrialWasteWaterData.calculateN2OEmissions());
        industrialWasteWaterData.setNH4Emissions(industrialWasteWaterData.calculateNH4Emissions());
        return wasteDataRepository.save(industrialWasteWaterData);
    }

    @Override
    public WasteDataAbstract createSolidWasteData(SolidWasteDto wasteData) {

        SolidWasteData latestSolidWasteRecord = (SolidWasteData) wasteDataRepository.findFirstByWasteTypeOrderByCreatedAtDesc(WasteType.SOLID_WASTE);

        SolidWasteData solidWasteData = new SolidWasteData();
        solidWasteData.setWasteType(WasteType.SOLID_WASTE);
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

    @Override
    public WasteDataAbstract createWasteWaterData(GeneralWasteByPopulationDto wasteData) {
        //Find year's EICV Report
        EICVReport eicvReport = eicvReportRepository.findByYear(wasteData.getActivityYear().getYear());

        if(eicvReport == null){
            //Do the interpolation and extrapolation stuff
        }

        WasteWaterData wasteWaterData = new WasteWaterData();
        wasteWaterData.setWasteType(WasteType.WASTE_WATER);
        wasteWaterData.setPopulationRecords(populationRecordRepository.findById(wasteData.getPopulationRecords())
                .orElseThrow(() -> new RuntimeException("Population record not found")));
        wasteWaterData.setRegion(regionRepository.findById(wasteData.getRegion())
                .orElseThrow(() -> new RuntimeException("Region not found")));
        wasteWaterData.setActivityYear(wasteData.getActivityYear());
        wasteWaterData.setScope(wasteData.getScope());
        wasteWaterData.setEICVReport(eicvReport);

        wasteWaterData.setCH4Emissions(wasteWaterData.calculateCH4Emissions());
        return wasteDataRepository.save(wasteWaterData);
    }

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
        biologicallyTreatedWasteData.setNH4Emissions(biologicallyTreatedWasteData.getCH4Emissions()/1000000);
        return wasteDataRepository.save(biologicallyTreatedWasteData);
    }

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
    public List<WasteDataAbstract> getWasteDataByType(WasteType wasteType) {
        return wasteDataRepository.findAllByWasteType(wasteType);
    }
}
