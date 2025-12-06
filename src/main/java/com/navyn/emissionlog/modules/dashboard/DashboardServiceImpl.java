package com.navyn.emissionlog.modules.dashboard;

import com.navyn.emissionlog.modules.activities.models.Activity;
import com.navyn.emissionlog.modules.activities.repositories.ActivityRepository;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.*;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.DirectLandEmissions.AnimalManureAndCompostEmissions;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.DirectLandEmissions.SyntheticFertilizerEmissions;
import com.navyn.emissionlog.modules.agricultureEmissions.models.Livestock.EntericFermentationEmissions;
import com.navyn.emissionlog.modules.agricultureEmissions.repositories.AgriculturalLand.*;
import com.navyn.emissionlog.modules.agricultureEmissions.repositories.AgriculturalLand.DirectLandEmissions.AnimalManureAndCompostEmissionsRepository;
import com.navyn.emissionlog.modules.agricultureEmissions.repositories.AgriculturalLand.DirectLandEmissions.SyntheticFertilizerEmissionsRepository;
import com.navyn.emissionlog.modules.agricultureEmissions.repositories.Livestock.EntericFermentationEmissionsRepository;
import com.navyn.emissionlog.modules.LandUseEmissions.models.*;
import com.navyn.emissionlog.modules.LandUseEmissions.Repositories.*;
import com.navyn.emissionlog.modules.wasteEmissions.models.WasteDataAbstract;
import com.navyn.emissionlog.modules.wasteEmissions.WasteDataRepository;
import com.navyn.emissionlog.modules.mitigationProjects.MitigationDashboardService;
import com.navyn.emissionlog.utils.DashboardData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    
    private final ActivityRepository activityRepository;
    private final WasteDataRepository wasteDataRepository;
    private final AquacultureEmissionsRepository aquacultureEmissionsRepository;
    private final EntericFermentationEmissionsRepository entericFermentationEmissionsRepository;
    private final LimingEmissionsRepository limingEmissionsRepository;
    private final AnimalManureAndCompostEmissionsRepository animalManureAndCompostEmissionsRepository;
    private final RiceCultivationEmissionsRepository riceCultivationEmissionsRepository;
    private final SyntheticFertilizerEmissionsRepository syntheticFertilizerEmissionsRepository;
    private final UreaEmissionsRepository ureaEmissionsRepository;
    private final BiomassGainRepository biomassGainRepository;
    private final DisturbanceBiomassLossRepository disturbanceBiomassLossRepository;
    private final FirewoodRemovalBiomassLossRepository firewoodRemovalBiomassLossRepository;
    private final HarvestedBiomassLossRepository harvestedBiomassLossRepository;
    private final RewettedMineralWetlandsRepository rewettedMineralWetlandsRepository;
    private final MitigationDashboardService mitigationDashboardService;
    
    // GWP factors
    private static final double CH4_GWP = 25.0;
    private static final double N2O_GWP = 298.0;
    
    @Override
    public DashboardData getMainDashboard(Integer startingYear, Integer endingYear) {
        DashboardData data = new DashboardData();
        
        // Fetch all data sources
        List<Activity> activities = fetchActivities(startingYear, endingYear);
        List<WasteDataAbstract> wasteData = fetchWasteData(startingYear, endingYear);
        List<AquacultureEmissions> aquaculture = fetchAquaculture(startingYear, endingYear);
        List<EntericFermentationEmissions> enteric = fetchEntericFermentation(startingYear, endingYear);
        List<LimingEmissions> liming = fetchLiming(startingYear, endingYear);
        List<AnimalManureAndCompostEmissions> manure = fetchManure(startingYear, endingYear);
        List<RiceCultivationEmissions> rice = fetchRice(startingYear, endingYear);
        List<SyntheticFertilizerEmissions> fertilizer = fetchFertilizer(startingYear, endingYear);
        List<UreaEmissions> urea = fetchUrea(startingYear, endingYear);
        List<BiomassGain> biomassGains = fetchBiomassGains(startingYear, endingYear);
        List<DisturbanceBiomassLoss> disturbanceLosses = fetchDisturbanceLosses(startingYear, endingYear);
        List<FirewoodRemovalBiomassLoss> firewoodLosses = fetchFirewoodLosses(startingYear, endingYear);
        List<HarvestedBiomassLoss> harvestedLosses = fetchHarvestedLosses(startingYear, endingYear);
        List<RewettedMineralWetlands> rewettedWetlands = fetchRewettedWetlands(startingYear, endingYear);
        
        // Aggregate all emissions
        aggregateActivityEmissions(data, activities);
        aggregateWasteEmissions(data, wasteData);
        aggregateAgricultureEmissions(data, aquaculture, enteric, liming, manure, rice, fertilizer, urea);
        aggregateLandUseEmissions(data, biomassGains, disturbanceLosses, firewoodLosses, harvestedLosses, rewettedWetlands);
        
        // Calculate CO2 equivalent
        calculateCO2Equivalent(data);
        
        // Get mitigation data
        DashboardData mitigationData = mitigationDashboardService.getMitigationDashboardSummary(startingYear, endingYear);
        if (mitigationData != null && mitigationData.getTotalMitigationKtCO2e() != null) {
            data.setTotalMitigationKtCO2e(mitigationData.getTotalMitigationKtCO2e());
        }
        
        // Calculate net emissions (Gross - Mitigation)
        double grossEmissions = data.getTotalCO2EqEmissions(); // in Kt CO2e
        double mitigation = data.getTotalMitigationKtCO2e() != null ? data.getTotalMitigationKtCO2e() : 0.0;
        data.setNetEmissionsKtCO2e(grossEmissions - mitigation);
        
        // Set date range if specified
        if (startingYear != null && endingYear != null) {
            data.setStartingDate(LocalDateTime.of(startingYear, 1, 1, 0, 0).toString());
            data.setEndingDate(LocalDateTime.of(endingYear, 12, 31, 23, 59).toString());
        }
        
        return data;
    }
    
    @Override
    public List<DashboardData> getMainDashboardGraph(Integer startingYear, Integer endingYear) {
        List<DashboardData> dashboardDataList = new ArrayList<>();
        
        if (startingYear == null || endingYear == null) {
            int currentYear = LocalDateTime.now().getYear();
            startingYear = currentYear - 4;
            endingYear = currentYear;
        }
        
        for (int year = startingYear; year <= endingYear; year++) {
            DashboardData yearData = getMainDashboard(year, year);
            yearData.setYear(Year.of(year));
            dashboardDataList.add(yearData);
        }
        
        return dashboardDataList;
    }
    
    @Override
    public DashboardData getStationaryDashboard(Integer startingYear, Integer endingYear) {
        DashboardData data = new DashboardData();
        
        // Fetch only stationary activities
        List<Activity> activities = fetchActivities(startingYear, endingYear);
        List<Activity> stationaryActivities = activities.stream()
                .filter(a -> a.getSector() != null && "STATIONARY".equalsIgnoreCase(a.getSector().name()))
                .toList();
        
        aggregateActivityEmissions(data, stationaryActivities);
        calculateCO2Equivalent(data);
        
        if (startingYear != null && endingYear != null) {
            data.setStartingDate(LocalDateTime.of(startingYear, 1, 1, 0, 0).toString());
            data.setEndingDate(LocalDateTime.of(endingYear, 12, 31, 23, 59).toString());
        }
        
        return data;
    }
    
    @Override
    public DashboardData getWasteDashboard(Integer startingYear, Integer endingYear) {
        DashboardData data = new DashboardData();
        
        List<WasteDataAbstract> wasteData = fetchWasteData(startingYear, endingYear);
        aggregateWasteEmissions(data, wasteData);
        calculateCO2Equivalent(data);
        
        if (startingYear != null && endingYear != null) {
            data.setStartingDate(LocalDateTime.of(startingYear, 1, 1, 0, 0).toString());
            data.setEndingDate(LocalDateTime.of(endingYear, 12, 31, 23, 59).toString());
        }
        
        return data;
    }
    
    @Override
    public DashboardData getAgricultureDashboard(Integer startingYear, Integer endingYear) {
        DashboardData data = new DashboardData();
        
        List<AquacultureEmissions> aquaculture = fetchAquaculture(startingYear, endingYear);
        List<EntericFermentationEmissions> enteric = fetchEntericFermentation(startingYear, endingYear);
        List<LimingEmissions> liming = fetchLiming(startingYear, endingYear);
        List<AnimalManureAndCompostEmissions> manure = fetchManure(startingYear, endingYear);
        List<RiceCultivationEmissions> rice = fetchRice(startingYear, endingYear);
        List<SyntheticFertilizerEmissions> fertilizer = fetchFertilizer(startingYear, endingYear);
        List<UreaEmissions> urea = fetchUrea(startingYear, endingYear);
        
        aggregateAgricultureEmissions(data, aquaculture, enteric, liming, manure, rice, fertilizer, urea);
        calculateCO2Equivalent(data);
        
        if (startingYear != null && endingYear != null) {
            data.setStartingDate(LocalDateTime.of(startingYear, 1, 1, 0, 0).toString());
            data.setEndingDate(LocalDateTime.of(endingYear, 12, 31, 23, 59).toString());
        }
        
        return data;
    }
    
    @Override
    public DashboardData getLandUseDashboard(Integer startingYear, Integer endingYear) {
        DashboardData data = new DashboardData();
        
        List<BiomassGain> biomassGains = fetchBiomassGains(startingYear, endingYear);
        List<DisturbanceBiomassLoss> disturbanceLosses = fetchDisturbanceLosses(startingYear, endingYear);
        List<FirewoodRemovalBiomassLoss> firewoodLosses = fetchFirewoodLosses(startingYear, endingYear);
        List<HarvestedBiomassLoss> harvestedLosses = fetchHarvestedLosses(startingYear, endingYear);
        List<RewettedMineralWetlands> rewettedWetlands = fetchRewettedWetlands(startingYear, endingYear);
        
        aggregateLandUseEmissions(data, biomassGains, disturbanceLosses, firewoodLosses, harvestedLosses, rewettedWetlands);
        calculateCO2Equivalent(data);
        
        if (startingYear != null && endingYear != null) {
            data.setStartingDate(LocalDateTime.of(startingYear, 1, 1, 0, 0).toString());
            data.setEndingDate(LocalDateTime.of(endingYear, 12, 31, 23, 59).toString());
        }
        
        return data;
    }
    
    // ============= PRIVATE HELPER METHODS =============
    
    private void aggregateActivityEmissions(DashboardData data, List<Activity> activities) {
        for (Activity activity : activities) {
            if (activity.getCH4Emissions() != null) {
                data.setTotalCH4Emissions(data.getTotalCH4Emissions() + activity.getCH4Emissions());
            }
            if (activity.getN2OEmissions() != null) {
                data.setTotalN2OEmissions(data.getTotalN2OEmissions() + activity.getN2OEmissions());
            }
            if (activity.getFossilCO2Emissions() != null) {
                data.setTotalFossilCO2Emissions(data.getTotalFossilCO2Emissions() + activity.getFossilCO2Emissions());
            }
            if (activity.getBioCO2Emissions() != null) {
                data.setTotalBioCO2Emissions(data.getTotalBioCO2Emissions() + activity.getBioCO2Emissions());
            }
        }
    }
    
    private void aggregateWasteEmissions(DashboardData data, List<WasteDataAbstract> wasteData) {
        for (WasteDataAbstract waste : wasteData) {
            if (waste.getCH4Emissions() != null) {
                data.setTotalCH4Emissions(data.getTotalCH4Emissions() + waste.getCH4Emissions());
            }
            if (waste.getN2OEmissions() != null) {
                data.setTotalN2OEmissions(data.getTotalN2OEmissions() + waste.getN2OEmissions());
            }
            if (waste.getFossilCO2Emissions() != null) {
                data.setTotalFossilCO2Emissions(data.getTotalFossilCO2Emissions() + waste.getFossilCO2Emissions());
            }
            if (waste.getBioCO2Emissions() != null) {
                data.setTotalBioCO2Emissions(data.getTotalBioCO2Emissions() + waste.getBioCO2Emissions());
            }
        }
    }
    
    private void aggregateAgricultureEmissions(DashboardData data,
            List<AquacultureEmissions> aquaculture,
            List<EntericFermentationEmissions> enteric,
            List<LimingEmissions> liming,
            List<AnimalManureAndCompostEmissions> manure,
            List<RiceCultivationEmissions> rice,
            List<SyntheticFertilizerEmissions> fertilizer,
            List<UreaEmissions> urea) {
        
        // Aquaculture - N2O only (double primitive, always present)
        for (AquacultureEmissions a : aquaculture) {
            data.setTotalN2OEmissions(data.getTotalN2OEmissions() + a.getN2OEmissions());
        }
        
        // Enteric Fermentation - CH4 only (double primitive)
        for (EntericFermentationEmissions e : enteric) {
            data.setTotalCH4Emissions(data.getTotalCH4Emissions() + e.getCH4Emissions());
        }
        
        // Liming - CO2 (bio) (double primitive)
        for (LimingEmissions l : liming) {
            data.setTotalBioCO2Emissions(data.getTotalBioCO2Emissions() + l.getCO2Emissions());
        }
        
        // Manure - CH4 and N2O (double primitives)
        for (AnimalManureAndCompostEmissions m : manure) {
            data.setTotalCH4Emissions(data.getTotalCH4Emissions() + m.getCH4Emissions());
            data.setTotalN2OEmissions(data.getTotalN2OEmissions() + m.getN2OEmissions());
        }
        
        // Rice - CH4 only (double primitive)
        for (RiceCultivationEmissions r : rice) {
            data.setTotalCH4Emissions(data.getTotalCH4Emissions() + r.getAnnualCH4Emissions());
        }
        
        // Fertilizer - N2O only (double primitive)
        for (SyntheticFertilizerEmissions f : fertilizer) {
            data.setTotalN2OEmissions(data.getTotalN2OEmissions() + f.getN2OEmissions());
        }
        
        // Urea - CO2 (bio) (double primitive)
        for (UreaEmissions u : urea) {
            data.setTotalBioCO2Emissions(data.getTotalBioCO2Emissions() + u.getCO2Emissions());
        }
    }
    
    private void aggregateLandUseEmissions(DashboardData data,
            List<BiomassGain> biomassGains,
            List<DisturbanceBiomassLoss> disturbanceLosses,
            List<FirewoodRemovalBiomassLoss> firewoodLosses,
            List<HarvestedBiomassLoss> harvestedLosses,
            List<RewettedMineralWetlands> rewettedWetlands) {
        
        double landUseTotal = 0.0;
        
        // Biomass Gains (negative emissions - sequestration, double primitive)
        for (BiomassGain bg : biomassGains) {
            landUseTotal -= bg.getCO2EqOfBiomassCarbonGained(); // Subtract because it's sequestration
        }
        
        // Biomass Losses (positive emissions, double primitives)
        for (DisturbanceBiomassLoss dl : disturbanceLosses) {
            landUseTotal += dl.getCO2EqOfBiomassCarbonLoss();
        }
        
        for (FirewoodRemovalBiomassLoss fl : firewoodLosses) {
            landUseTotal += fl.getCO2EqOfBiomassCarbonLoss();
        }
        
        for (HarvestedBiomassLoss hl : harvestedLosses) {
            landUseTotal += hl.getCO2EqOfBiomassCarbonLoss();
        }
        
        // Rewetted Wetlands (negative emissions from CH4 reduction, double primitive)
        for (RewettedMineralWetlands rw : rewettedWetlands) {
            landUseTotal -= rw.getCO2EqEmissions(); // Rewetting reduces emissions
        }
        
        data.setTotalLandUseEmissions(landUseTotal);
    }
    
    private void calculateCO2Equivalent(DashboardData data) {
        double co2Eq = 0.0;
        
        // CO2 (1:1 ratio)
        if (data.getTotalFossilCO2Emissions() != null) {
            co2Eq += data.getTotalFossilCO2Emissions();
        }
        if (data.getTotalBioCO2Emissions() != null) {
            co2Eq += data.getTotalBioCO2Emissions();
        }
        
        // CH4 (GWP = 25)
        if (data.getTotalCH4Emissions() != null) {
            co2Eq += data.getTotalCH4Emissions() * CH4_GWP;
        }
        
        // N2O (GWP = 298)
        if (data.getTotalN2OEmissions() != null) {
            co2Eq += data.getTotalN2OEmissions() * N2O_GWP;
        }
        
        // Add land use emissions
        if (data.getTotalLandUseEmissions() != null) {
            co2Eq += data.getTotalLandUseEmissions();
        }
        
        data.setTotalCO2EqEmissions(co2Eq);
    }
    
    // ============= FETCH METHODS =============
    
    private List<Activity> fetchActivities(Integer startYear, Integer endYear) {
        if (startYear != null && endYear != null) {
            LocalDateTime start = LocalDateTime.of(startYear, 1, 1, 0, 0);
            LocalDateTime end = LocalDateTime.of(endYear, 12, 31, 23, 59);
            return activityRepository.findAllByActivityYearBetweenOrderByActivityYearDesc(start, end);
        }
        return activityRepository.findAll();
    }
    
    private List<WasteDataAbstract> fetchWasteData(Integer startYear, Integer endYear) {
        if (startYear != null && endYear != null) {
            LocalDateTime start = LocalDateTime.of(startYear, 1, 1, 0, 0);
            LocalDateTime end = LocalDateTime.of(endYear, 12, 31, 23, 59);
            return wasteDataRepository.findByActivityYearBetweenOrderByYearDesc(start, end);
        }
        return wasteDataRepository.findAll();
    }
    
    private List<AquacultureEmissions> fetchAquaculture(Integer startYear, Integer endYear) {
        if (startYear != null && endYear != null) {
            return aquacultureEmissionsRepository.findByYearRange(startYear, endYear);
        }
        return aquacultureEmissionsRepository.findAll();
    }
    
    private List<EntericFermentationEmissions> fetchEntericFermentation(Integer startYear, Integer endYear) {
        if (startYear != null && endYear != null) {
            return entericFermentationEmissionsRepository.findByYearRange(startYear, endYear);
        }
        return entericFermentationEmissionsRepository.findAll();
    }
    
    private List<LimingEmissions> fetchLiming(Integer startYear, Integer endYear) {
        if (startYear != null && endYear != null) {
            return limingEmissionsRepository.findByYearRange(startYear, endYear);
        }
        return limingEmissionsRepository.findAll();
    }
    
    private List<AnimalManureAndCompostEmissions> fetchManure(Integer startYear, Integer endYear) {
        if (startYear != null && endYear != null) {
            return animalManureAndCompostEmissionsRepository.findByYearRange(startYear, endYear);
        }
        return animalManureAndCompostEmissionsRepository.findAll();
    }
    
    private List<RiceCultivationEmissions> fetchRice(Integer startYear, Integer endYear) {
        if (startYear != null && endYear != null) {
            return riceCultivationEmissionsRepository.findByYearRange(startYear, endYear);
        }
        return riceCultivationEmissionsRepository.findAll();
    }
    
    private List<SyntheticFertilizerEmissions> fetchFertilizer(Integer startYear, Integer endYear) {
        if (startYear != null && endYear != null) {
            return syntheticFertilizerEmissionsRepository.findByYearRange(startYear, endYear);
        }
        return syntheticFertilizerEmissionsRepository.findAll();
    }
    
    private List<UreaEmissions> fetchUrea(Integer startYear, Integer endYear) {
        if (startYear != null && endYear != null) {
            return ureaEmissionsRepository.findByYearRange(startYear, endYear);
        }
        return ureaEmissionsRepository.findAll();
    }
    
    private List<BiomassGain> fetchBiomassGains(Integer startYear, Integer endYear) {
        List<BiomassGain> all = biomassGainRepository.findAll();
        if (startYear != null && endYear != null) {
            return all.stream()
                    .filter(bg -> bg.getYear() >= startYear && bg.getYear() <= endYear)
                    .toList();
        }
        return all;
    }
    
    private List<DisturbanceBiomassLoss> fetchDisturbanceLosses(Integer startYear, Integer endYear) {
        List<DisturbanceBiomassLoss> all = disturbanceBiomassLossRepository.findAll();
        if (startYear != null && endYear != null) {
            return all.stream()
                    .filter(d -> d.getYear() >= startYear && d.getYear() <= endYear)
                    .toList();
        }
        return all;
    }
    
    private List<FirewoodRemovalBiomassLoss> fetchFirewoodLosses(Integer startYear, Integer endYear) {
        List<FirewoodRemovalBiomassLoss> all = firewoodRemovalBiomassLossRepository.findAll();
        if (startYear != null && endYear != null) {
            return all.stream()
                    .filter(f -> f.getYear() >= startYear && f.getYear() <= endYear)
                    .toList();
        }
        return all;
    }
    
    private List<HarvestedBiomassLoss> fetchHarvestedLosses(Integer startYear, Integer endYear) {
        List<HarvestedBiomassLoss> all = harvestedBiomassLossRepository.findAll();
        if (startYear != null && endYear != null) {
            return all.stream()
                    .filter(h -> h.getYear() >= startYear && h.getYear() <= endYear)
                    .toList();
        }
        return all;
    }
    
    private List<RewettedMineralWetlands> fetchRewettedWetlands(Integer startYear, Integer endYear) {
        List<RewettedMineralWetlands> all = rewettedMineralWetlandsRepository.findAll();
        if (startYear != null && endYear != null) {
            return all.stream()
                    .filter(rw -> rw.getYear() >= startYear && rw.getYear() <= endYear)
                    .toList();
        }
        return all;
    }
}
