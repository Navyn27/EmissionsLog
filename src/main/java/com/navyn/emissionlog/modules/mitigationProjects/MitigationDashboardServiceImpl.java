package com.navyn.emissionlog.modules.mitigationProjects;

import com.navyn.emissionlog.modules.mitigationProjects.cropRotation.models.CropRotationMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.cropRotation.repositories.CropRotationMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.greenFences.models.GreenFencesMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.greenFences.repositories.GreenFencesMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.improvedMMS.models.ImprovedMMSMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.improvedMMS.repositories.ImprovedMMSMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.protectiveForest.models.ProtectiveForestMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.protectiveForest.repositories.ProtectiveForestMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.settlementTrees.models.SettlementTreesMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.settlementTrees.repositories.SettlementTreesMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.streetTrees.models.StreetTreesMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.streetTrees.repositories.StreetTreesMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.wetlandParks.models.WetlandParksMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.wetlandParks.repositories.WetlandParksMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.zeroTillage.models.ZeroTillageMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.zeroTillage.repositories.ZeroTillageMitigationRepository;
import com.navyn.emissionlog.utils.DashboardData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@Service
@RequiredArgsConstructor
public class MitigationDashboardServiceImpl implements MitigationDashboardService {
    
    private final WetlandParksMitigationRepository wetlandParksRepository;
    private final SettlementTreesMitigationRepository settlementTreesRepository;
    private final StreetTreesMitigationRepository streetTreesRepository;
    private final GreenFencesMitigationRepository greenFencesRepository;
    private final CropRotationMitigationRepository cropRotationRepository;
    private final ZeroTillageMitigationRepository zeroTillageRepository;
    private final ProtectiveForestMitigationRepository protectiveForestRepository;
    private final ImprovedMMSMitigationRepository improvedMMSRepository;
    
    @Override
    public DashboardData getMitigationDashboardSummary(Integer startingYear, Integer endingYear) {
        // Fetch all 8 mitigation projects
        List<WetlandParksMitigation> wetlandParks = wetlandParksRepository.findAll();
        List<SettlementTreesMitigation> settlementTrees = settlementTreesRepository.findAll();
        List<StreetTreesMitigation> streetTrees = streetTreesRepository.findAll();
        List<GreenFencesMitigation> greenFences = greenFencesRepository.findAll();
        List<CropRotationMitigation> cropRotation = cropRotationRepository.findAll();
        List<ZeroTillageMitigation> zeroTillage = zeroTillageRepository.findAll();
        List<ProtectiveForestMitigation> protectiveForest = protectiveForestRepository.findAll();
        List<ImprovedMMSMitigation> improvedMMS = improvedMMSRepository.findAll();
        
        // Filter by year if specified
        if (startingYear != null && endingYear != null) {
            wetlandParks = wetlandParks.stream()
                .filter(w -> w.getYear() >= startingYear && w.getYear() <= endingYear)
                .toList();
            settlementTrees = settlementTrees.stream()
                .filter(s -> s.getYear() >= startingYear && s.getYear() <= endingYear)
                .toList();
            streetTrees = streetTrees.stream()
                .filter(s -> s.getYear() >= startingYear && s.getYear() <= endingYear)
                .toList();
            greenFences = greenFences.stream()
                .filter(g -> g.getYear() >= startingYear && g.getYear() <= endingYear)
                .toList();
            cropRotation = cropRotation.stream()
                .filter(c -> c.getYear() >= startingYear && c.getYear() <= endingYear)
                .toList();
            zeroTillage = zeroTillage.stream()
                .filter(z -> z.getYear() >= startingYear && z.getYear() <= endingYear)
                .toList();
            protectiveForest = protectiveForest.stream()
                .filter(p -> p.getYear() >= startingYear && p.getYear() <= endingYear)
                .toList();
            improvedMMS = improvedMMS.stream()
                .filter(i -> i.getYear() >= startingYear && i.getYear() <= endingYear)
                .toList();
        }
        
        return calculateMitigationDashboardData(wetlandParks, settlementTrees, streetTrees, 
                greenFences, cropRotation, zeroTillage, protectiveForest, improvedMMS);
    }
    
    @Override
    public List<DashboardData> getMitigationDashboardGraph(Integer startingYear, Integer endingYear) {
        // Default to last 5 years if not specified
        if (startingYear == null || endingYear == null) {
            int currentYear = LocalDateTime.now().getYear();
            startingYear = currentYear - 4;
            endingYear = currentYear;
        }
        
        // Fetch all data
        List<WetlandParksMitigation> wetlandParks = wetlandParksRepository.findAll();
        List<SettlementTreesMitigation> settlementTrees = settlementTreesRepository.findAll();
        List<StreetTreesMitigation> streetTrees = streetTreesRepository.findAll();
        List<GreenFencesMitigation> greenFences = greenFencesRepository.findAll();
        List<CropRotationMitigation> cropRotation = cropRotationRepository.findAll();
        List<ZeroTillageMitigation> zeroTillage = zeroTillageRepository.findAll();
        List<ProtectiveForestMitigation> protectiveForest = protectiveForestRepository.findAll();
        List<ImprovedMMSMitigation> improvedMMS = improvedMMSRepository.findAll();
        
        // Filter by year range
        final int finalStartYear = startingYear;
        final int finalEndYear = endingYear;
        
        wetlandParks = wetlandParks.stream()
            .filter(w -> w.getYear() >= finalStartYear && w.getYear() <= finalEndYear)
            .toList();
        settlementTrees = settlementTrees.stream()
            .filter(s -> s.getYear() >= finalStartYear && s.getYear() <= finalEndYear)
            .toList();
        streetTrees = streetTrees.stream()
            .filter(s -> s.getYear() >= finalStartYear && s.getYear() <= finalEndYear)
            .toList();
        greenFences = greenFences.stream()
            .filter(g -> g.getYear() >= finalStartYear && g.getYear() <= finalEndYear)
            .toList();
        cropRotation = cropRotation.stream()
            .filter(c -> c.getYear() >= finalStartYear && c.getYear() <= finalEndYear)
            .toList();
        zeroTillage = zeroTillage.stream()
            .filter(z -> z.getYear() >= finalStartYear && z.getYear() <= finalEndYear)
            .toList();
        protectiveForest = protectiveForest.stream()
            .filter(p -> p.getYear() >= finalStartYear && p.getYear() <= finalEndYear)
            .toList();
        improvedMMS = improvedMMS.stream()
            .filter(i -> i.getYear() >= finalStartYear && i.getYear() <= finalEndYear)
            .toList();
        
        // Group by year
        Map<Integer, List<WetlandParksMitigation>> wetlandParksByYear = wetlandParks.stream().collect(groupingBy(WetlandParksMitigation::getYear));
        Map<Integer, List<SettlementTreesMitigation>> settlementTreesByYear = settlementTrees.stream().collect(groupingBy(SettlementTreesMitigation::getYear));
        Map<Integer, List<StreetTreesMitigation>> streetTreesByYear = streetTrees.stream().collect(groupingBy(StreetTreesMitigation::getYear));
        Map<Integer, List<GreenFencesMitigation>> greenFencesByYear = greenFences.stream().collect(groupingBy(GreenFencesMitigation::getYear));
        Map<Integer, List<CropRotationMitigation>> cropRotationByYear = cropRotation.stream().collect(groupingBy(CropRotationMitigation::getYear));
        Map<Integer, List<ZeroTillageMitigation>> zeroTillageByYear = zeroTillage.stream().collect(groupingBy(ZeroTillageMitigation::getYear));
        Map<Integer, List<ProtectiveForestMitigation>> protectiveForestByYear = protectiveForest.stream().collect(groupingBy(ProtectiveForestMitigation::getYear));
        Map<Integer, List<ImprovedMMSMitigation>> improvedMMSByYear = improvedMMS.stream().collect(groupingBy(ImprovedMMSMitigation::getYear));
        
        // Create dashboard data for each year
        List<DashboardData> dashboardDataList = new ArrayList<>();
        for (int year = startingYear; year <= endingYear; year++) {
            DashboardData data = calculateMitigationDashboardData(
                wetlandParksByYear.getOrDefault(year, List.of()),
                settlementTreesByYear.getOrDefault(year, List.of()),
                streetTreesByYear.getOrDefault(year, List.of()),
                greenFencesByYear.getOrDefault(year, List.of()),
                cropRotationByYear.getOrDefault(year, List.of()),
                zeroTillageByYear.getOrDefault(year, List.of()),
                protectiveForestByYear.getOrDefault(year, List.of()),
                improvedMMSByYear.getOrDefault(year, List.of())
            );
            data.setStartingDate(LocalDateTime.of(year, 1, 1, 0, 0).toString());
            data.setEndingDate(LocalDateTime.of(year, 12, 31, 23, 59).toString());
            data.setYear(Year.of(year));
            dashboardDataList.add(data);
        }
        
        return dashboardDataList;
    }
    
    private DashboardData calculateMitigationDashboardData(
            List<WetlandParksMitigation> wetlandParks,
            List<SettlementTreesMitigation> settlementTrees,
            List<StreetTreesMitigation> streetTrees,
            List<GreenFencesMitigation> greenFences,
            List<CropRotationMitigation> cropRotation,
            List<ZeroTillageMitigation> zeroTillage,
            List<ProtectiveForestMitigation> protectiveForest,
            List<ImprovedMMSMitigation> improvedMMS) {
        
        DashboardData data = new DashboardData();
        Double totalMitigation = 0.0;
        
        // Sum all mitigation projects (all in Kt CO2e)
        for (WetlandParksMitigation m : wetlandParks) {
            if (m.getMitigatedEmissionsKtCO2e() != null) {
                totalMitigation += m.getMitigatedEmissionsKtCO2e();
            }
        }
        
        for (SettlementTreesMitigation m : settlementTrees) {
            if (m.getMitigatedEmissionsKtCO2e() != null) {
                totalMitigation += m.getMitigatedEmissionsKtCO2e();
            }
        }
        
        for (StreetTreesMitigation m : streetTrees) {
            if (m.getMitigatedEmissionsKtCO2e() != null) {
                totalMitigation += m.getMitigatedEmissionsKtCO2e();
            }
        }
        
        for (GreenFencesMitigation m : greenFences) {
            if (m.getMitigatedEmissionsKtCO2e() != null) {
                totalMitigation += m.getMitigatedEmissionsKtCO2e();
            }
        }
        
        for (CropRotationMitigation m : cropRotation) {
            if (m.getMitigatedEmissionsKtCO2e() != null) {
                totalMitigation += m.getMitigatedEmissionsKtCO2e();
            }
        }
        
        // Zero Tillage uses ghgEmissionsSavings (net after urea offset)
        for (ZeroTillageMitigation m : zeroTillage) {
            if (m.getGhgEmissionsSavings() != null) {
                totalMitigation += m.getGhgEmissionsSavings();
            }
        }
        
        for (ProtectiveForestMitigation m : protectiveForest) {
            if (m.getMitigatedEmissionsKtCO2e() != null) {
                totalMitigation += m.getMitigatedEmissionsKtCO2e();
            }
        }
        
        // Improved MMS uses totalMitigation field
        for (ImprovedMMSMitigation m : improvedMMS) {
            if (m.getTotalMitigation() != null) {
                totalMitigation += m.getTotalMitigation();
            }
        }
        
        data.setTotalMitigationKtCO2e(totalMitigation);
        
        return data;
    }
}
