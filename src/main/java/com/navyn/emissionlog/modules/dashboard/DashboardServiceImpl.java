package com.navyn.emissionlog.modules.dashboard;

import com.navyn.emissionlog.modules.activities.models.Activity;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.*;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.DirectLandEmissions.AnimalManureAndCompostEmissions;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.DirectLandEmissions.SyntheticFertilizerEmissions;
import com.navyn.emissionlog.modules.agricultureEmissions.models.Livestock.EntericFermentationEmissions;
import com.navyn.emissionlog.modules.LandUseEmissions.models.*;
import com.navyn.emissionlog.modules.wasteEmissions.models.WasteDataAbstract;
import com.navyn.emissionlog.modules.mitigationProjects.MitigationDashboardService;
import com.navyn.emissionlog.utils.DashboardData;
import com.navyn.emissionlog.utils.FetchMethods;
import com.navyn.emissionlog.utils.DashboardHelperMethods;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final MitigationDashboardService mitigationDashboardService;
    private final FetchMethods fetchMethods;
    private final DashboardHelperMethods dashboardHelperMethods;

    // GWP factors
    private static final double CH4_GWP = 25.0;
    private static final double N2O_GWP = 298.0;
    
    @Override
    public DashboardData getMainDashboard(Integer startingYear, Integer endingYear) {
        DashboardData data = new DashboardData();
        
        // Fetch all data sources
        List<Activity> activities = fetchMethods.fetchActivities(startingYear, endingYear);
        List<WasteDataAbstract> wasteData = fetchMethods.fetchWasteData(startingYear, endingYear);
        List<AquacultureEmissions> aquaculture = fetchMethods.fetchAquaculture(startingYear, endingYear);
        List<EntericFermentationEmissions> enteric = fetchMethods.fetchEntericFermentation(startingYear, endingYear);
        List<LimingEmissions> liming = fetchMethods.fetchLiming(startingYear, endingYear);
        List<AnimalManureAndCompostEmissions> manure = fetchMethods.fetchManure(startingYear, endingYear);
        List<RiceCultivationEmissions> rice = fetchMethods.fetchRice(startingYear, endingYear);
        List<SyntheticFertilizerEmissions> fertilizer = fetchMethods.fetchFertilizer(startingYear, endingYear);
        List<UreaEmissions> urea = fetchMethods.fetchUrea(startingYear, endingYear);
        List<BiomassGain> biomassGains = fetchMethods.fetchBiomassGains(startingYear, endingYear);
        List<DisturbanceBiomassLoss> disturbanceLosses = fetchMethods.fetchDisturbanceLosses(startingYear, endingYear);
        List<FirewoodRemovalBiomassLoss> firewoodLosses = fetchMethods.fetchFirewoodLosses(startingYear, endingYear);
        List<HarvestedBiomassLoss> harvestedLosses = fetchMethods.fetchHarvestedLosses(startingYear, endingYear);
        List<RewettedMineralWetlands> rewettedWetlands = fetchMethods.fetchRewettedWetlands(startingYear, endingYear);

        //aggregate all emissions
        dashboardHelperMethods.aggregateActivityEmissions(data, activities);
        dashboardHelperMethods.aggregateWasteEmissions(data, wasteData);
        dashboardHelperMethods.aggregateAgricultureEmissions(data, aquaculture, enteric, liming, manure, rice, fertilizer, urea);
        dashboardHelperMethods.aggregateLandUseEmissions(data, biomassGains, disturbanceLosses, firewoodLosses, harvestedLosses, rewettedWetlands);

        // Calculate CO2 equivalent
        dashboardHelperMethods.calculateCO2Equivalent(data);

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
        List<Activity> activities = fetchMethods.fetchActivities(startingYear, endingYear);
        List<Activity> stationaryActivities = activities.stream()
                .filter(a -> a.getSector() != null && "STATIONARY".equalsIgnoreCase(a.getSector().name()))
                .toList();

        dashboardHelperMethods.aggregateActivityEmissions(data, stationaryActivities);
        dashboardHelperMethods.calculateCO2Equivalent(data);

        if (startingYear != null && endingYear != null) {
            data.setStartingDate(LocalDateTime.of(startingYear, 1, 1, 0, 0).toString());
            data.setEndingDate(LocalDateTime.of(endingYear, 12, 31, 23, 59).toString());
        }

        return data;
    }

    @Override
    public DashboardData getWasteDashboard(Integer startingYear, Integer endingYear) {
        DashboardData data = new DashboardData();

        List<WasteDataAbstract> wasteData = fetchMethods.fetchWasteData(startingYear, endingYear);
        dashboardHelperMethods.aggregateWasteEmissions(data, wasteData);
        dashboardHelperMethods.calculateCO2Equivalent(data);

        if (startingYear != null && endingYear != null) {
            data.setStartingDate(LocalDateTime.of(startingYear, 1, 1, 0, 0).toString());
            data.setEndingDate(LocalDateTime.of(endingYear, 12, 31, 23, 59).toString());
        }

        return data;
    }

    @Override
    public DashboardData getAgricultureDashboard(Integer startingYear, Integer endingYear) {
        DashboardData data = new DashboardData();

        List<AquacultureEmissions> aquaculture = fetchMethods.fetchAquaculture(startingYear, endingYear);
        List<EntericFermentationEmissions> enteric = fetchMethods.fetchEntericFermentation(startingYear, endingYear);
        List<LimingEmissions> liming = fetchMethods.fetchLiming(startingYear, endingYear);
        List<AnimalManureAndCompostEmissions> manure = fetchMethods.fetchManure(startingYear, endingYear);
        List<RiceCultivationEmissions> rice = fetchMethods.fetchRice(startingYear, endingYear);
        List<SyntheticFertilizerEmissions> fertilizer = fetchMethods.fetchFertilizer(startingYear, endingYear);
        List<UreaEmissions> urea = fetchMethods.fetchUrea(startingYear, endingYear);

        dashboardHelperMethods.aggregateAgricultureEmissions(data, aquaculture, enteric, liming, manure, rice, fertilizer, urea);
        dashboardHelperMethods.calculateCO2Equivalent(data);

        if (startingYear != null && endingYear != null) {
            data.setStartingDate(LocalDateTime.of(startingYear, 1, 1, 0, 0).toString());
            data.setEndingDate(LocalDateTime.of(endingYear, 12, 31, 23, 59).toString());
        }

        return data;
    }

    @Override
    public DashboardData getLandUseDashboard(Integer startingYear, Integer endingYear) {
        DashboardData data = new DashboardData();

        List<BiomassGain> biomassGains = fetchMethods.fetchBiomassGains(startingYear, endingYear);
        List<DisturbanceBiomassLoss> disturbanceLosses = fetchMethods.fetchDisturbanceLosses(startingYear, endingYear);
        List<FirewoodRemovalBiomassLoss> firewoodLosses = fetchMethods.fetchFirewoodLosses(startingYear, endingYear);
        List<HarvestedBiomassLoss> harvestedLosses = fetchMethods.fetchHarvestedLosses(startingYear, endingYear);
        List<RewettedMineralWetlands> rewettedWetlands = fetchMethods.fetchRewettedWetlands(startingYear, endingYear);
        
        dashboardHelperMethods.aggregateLandUseEmissions(data, biomassGains, disturbanceLosses, firewoodLosses, harvestedLosses, rewettedWetlands);
        dashboardHelperMethods.calculateCO2Equivalent(data);
        
        if (startingYear != null && endingYear != null) {
            data.setStartingDate(LocalDateTime.of(startingYear, 1, 1, 0, 0).toString());
            data.setEndingDate(LocalDateTime.of(endingYear, 12, 31, 23, 59).toString());
        }
        
        return data;
    }
}
