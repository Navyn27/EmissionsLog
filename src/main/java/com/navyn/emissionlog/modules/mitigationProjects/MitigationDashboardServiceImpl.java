package com.navyn.emissionlog.modules.mitigationProjects;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.models.CropRotationMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.repositories.CropRotationMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.greenFences.models.GreenFencesMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.greenFences.repositories.GreenFencesMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.models.ManureCoveringMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.repository.ManureCoveringMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.models.AddingStrawMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.repository.AddingStrawMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.dailySpread.models.DailySpreadMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.dailySpread.repository.DailySpreadMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.protectiveForest.models.ProtectiveForestMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.protectiveForest.repositories.ProtectiveForestMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.settlementTrees.models.SettlementTreesMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.settlementTrees.repositories.SettlementTreesMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.streetTrees.models.StreetTreesMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.streetTrees.repositories.StreetTreesMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.models.WetlandParksMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.repositories.WetlandParksMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.zeroTillage.models.ZeroTillageMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.zeroTillage.repositories.ZeroTillageMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.models.WasteToEnergyMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.repository.WasteToEnergyMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.models.LandfillGasUtilizationMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.repository.LandfillGasUtilizationMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.models.MBTCompostingMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.repository.MBTCompostingMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.models.EPRPlasticWasteMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.repository.EPRPlasticWasteMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.models.KigaliFSTPMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.repository.KigaliFSTPMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.models.KigaliWWTPMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.repository.KigaliWWTPMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.models.ISWMMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.repository.ISWMMitigationRepository;
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
    private final ManureCoveringMitigationRepository manureCoveringRepository;
    private final AddingStrawMitigationRepository addingStrawRepository;
    private final DailySpreadMitigationRepository dailySpreadRepository;
    private final WasteToEnergyMitigationRepository wasteToEnergyRepository;
    private final LandfillGasUtilizationMitigationRepository landfillGasUtilizationRepository;
    private final MBTCompostingMitigationRepository mbtCompostingRepository;
    private final EPRPlasticWasteMitigationRepository eprPlasticWasteRepository;
    private final KigaliFSTPMitigationRepository kigaliFSTPRepository;
    private final KigaliWWTPMitigationRepository kigaliWWTPRepository;
    private final ISWMMitigationRepository iswmRepository;
    
    @Override
    public DashboardData getMitigationDashboardSummary(Integer startingYear, Integer endingYear) {
        // Fetch all 17 mitigation projects (10 AFOLU + 7 Waste)
        List<WetlandParksMitigation> wetlandParks = wetlandParksRepository.findAll();
        List<SettlementTreesMitigation> settlementTrees = settlementTreesRepository.findAll();
        List<StreetTreesMitigation> streetTrees = streetTreesRepository.findAll();
        List<GreenFencesMitigation> greenFences = greenFencesRepository.findAll();
        List<CropRotationMitigation> cropRotation = cropRotationRepository.findAll();
        List<ZeroTillageMitigation> zeroTillage = zeroTillageRepository.findAll();
        List<ProtectiveForestMitigation> protectiveForest = protectiveForestRepository.findAll();
        List<ManureCoveringMitigation> manureCovering = manureCoveringRepository.findAll();
        List<AddingStrawMitigation> addingStraw = addingStrawRepository.findAll();
        List<DailySpreadMitigation> dailySpread = dailySpreadRepository.findAll();
        List<WasteToEnergyMitigation> wasteToEnergy = wasteToEnergyRepository.findAll();
        List<LandfillGasUtilizationMitigation> landfillGasUtilization = landfillGasUtilizationRepository.findAll();
        List<MBTCompostingMitigation> mbtComposting = mbtCompostingRepository.findAll();
        List<EPRPlasticWasteMitigation> eprPlasticWaste = eprPlasticWasteRepository.findAll();
        List<KigaliFSTPMitigation> kigaliFSTP = kigaliFSTPRepository.findAll();
        List<KigaliWWTPMitigation> kigaliWWTP = kigaliWWTPRepository.findAll();
        List<ISWMMitigation> iswm = iswmRepository.findAll();
        
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
            manureCovering = manureCovering.stream()
                .filter(m -> m.getYear() >= startingYear && m.getYear() <= endingYear)
                .toList();
            addingStraw = addingStraw.stream()
                .filter(a -> a.getYear() >= startingYear && a.getYear() <= endingYear)
                .toList();
            dailySpread = dailySpread.stream()
                .filter(d -> d.getYear() >= startingYear && d.getYear() <= endingYear)
                .toList();
            wasteToEnergy = wasteToEnergy.stream()
                .filter(w -> w.getYear() >= startingYear && w.getYear() <= endingYear)
                .toList();
            landfillGasUtilization = landfillGasUtilization.stream()
                .filter(l -> l.getYear() >= startingYear && l.getYear() <= endingYear)
                .toList();
            mbtComposting = mbtComposting.stream()
                .filter(m -> m.getYear() >= startingYear && m.getYear() <= endingYear)
                .toList();
            eprPlasticWaste = eprPlasticWaste.stream()
                .filter(e -> e.getYear() >= startingYear && e.getYear() <= endingYear)
                .toList();
            kigaliFSTP = kigaliFSTP.stream()
                .filter(k -> k.getYear() >= startingYear && k.getYear() <= endingYear)
                .toList();
            kigaliWWTP = kigaliWWTP.stream()
                .filter(w -> w.getYear() >= startingYear && w.getYear() <= endingYear)
                .toList();
            iswm = iswm.stream()
                .filter(i -> i.getYear() >= startingYear && i.getYear() <= endingYear)
                .toList();
        }
        
        return calculateMitigationDashboardData(wetlandParks, settlementTrees, streetTrees, 
                greenFences, cropRotation, zeroTillage, protectiveForest, manureCovering, addingStraw, dailySpread, wasteToEnergy, landfillGasUtilization, mbtComposting, eprPlasticWaste, kigaliFSTP, kigaliWWTP, iswm);
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
        List<ManureCoveringMitigation> manureCovering = manureCoveringRepository.findAll();
        List<AddingStrawMitigation> addingStraw = addingStrawRepository.findAll();
        List<DailySpreadMitigation> dailySpread = dailySpreadRepository.findAll();
        List<WasteToEnergyMitigation> wasteToEnergy = wasteToEnergyRepository.findAll();
        List<LandfillGasUtilizationMitigation> landfillGasUtilization = landfillGasUtilizationRepository.findAll();
        List<MBTCompostingMitigation> mbtComposting = mbtCompostingRepository.findAll();
        List<EPRPlasticWasteMitigation> eprPlasticWaste = eprPlasticWasteRepository.findAll();
        List<KigaliFSTPMitigation> kigaliFSTP = kigaliFSTPRepository.findAll();
        List<KigaliWWTPMitigation> kigaliWWTP = kigaliWWTPRepository.findAll();
        List<ISWMMitigation> iswm = iswmRepository.findAll();
        
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
        manureCovering = manureCovering.stream()
            .filter(m -> m.getYear() >= finalStartYear && m.getYear() <= finalEndYear)
            .toList();
        addingStraw = addingStraw.stream()
            .filter(a -> a.getYear() >= finalStartYear && a.getYear() <= finalEndYear)
            .toList();
        dailySpread = dailySpread.stream()
            .filter(d -> d.getYear() >= finalStartYear && d.getYear() <= finalEndYear)
            .toList();
        wasteToEnergy = wasteToEnergy.stream()
            .filter(w -> w.getYear() >= finalStartYear && w.getYear() <= finalEndYear)
            .toList();
        landfillGasUtilization = landfillGasUtilization.stream()
            .filter(l -> l.getYear() >= finalStartYear && l.getYear() <= finalEndYear)
            .toList();
        mbtComposting = mbtComposting.stream()
            .filter(m -> m.getYear() >= finalStartYear && m.getYear() <= finalEndYear)
            .toList();
        eprPlasticWaste = eprPlasticWaste.stream()
            .filter(e -> e.getYear() >= finalStartYear && e.getYear() <= finalEndYear)
            .toList();
        kigaliFSTP = kigaliFSTP.stream()
            .filter(k -> k.getYear() >= finalStartYear && k.getYear() <= finalEndYear)
            .toList();
        kigaliWWTP = kigaliWWTP.stream()
            .filter(w -> w.getYear() >= finalStartYear && w.getYear() <= finalEndYear)
            .toList();
        iswm = iswm.stream()
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
        Map<Integer, List<ManureCoveringMitigation>> manureCoveringByYear = manureCovering.stream().collect(groupingBy(ManureCoveringMitigation::getYear));
        Map<Integer, List<AddingStrawMitigation>> addingStrawByYear = addingStraw.stream().collect(groupingBy(AddingStrawMitigation::getYear));
        Map<Integer, List<DailySpreadMitigation>> dailySpreadByYear = dailySpread.stream().collect(groupingBy(DailySpreadMitigation::getYear));
        Map<Integer, List<WasteToEnergyMitigation>> wasteToEnergyByYear = wasteToEnergy.stream().collect(groupingBy(WasteToEnergyMitigation::getYear));
        Map<Integer, List<LandfillGasUtilizationMitigation>> landfillGasUtilizationByYear = landfillGasUtilization.stream().collect(groupingBy(LandfillGasUtilizationMitigation::getYear));
        Map<Integer, List<MBTCompostingMitigation>> mbtCompostingByYear = mbtComposting.stream().collect(groupingBy(MBTCompostingMitigation::getYear));
        Map<Integer, List<EPRPlasticWasteMitigation>> eprPlasticWasteByYear = eprPlasticWaste.stream().collect(groupingBy(EPRPlasticWasteMitigation::getYear));
        Map<Integer, List<KigaliFSTPMitigation>> kigaliFSTPByYear = kigaliFSTP.stream().collect(groupingBy(KigaliFSTPMitigation::getYear));
        Map<Integer, List<KigaliWWTPMitigation>> kigaliWWTPByYear = kigaliWWTP.stream().collect(groupingBy(KigaliWWTPMitigation::getYear));
        Map<Integer, List<ISWMMitigation>> iswmByYear = iswm.stream().collect(groupingBy(ISWMMitigation::getYear));
        
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
                manureCoveringByYear.getOrDefault(year, List.of()),
                addingStrawByYear.getOrDefault(year, List.of()),
                dailySpreadByYear.getOrDefault(year, List.of()),
                wasteToEnergyByYear.getOrDefault(year, List.of()),
                landfillGasUtilizationByYear.getOrDefault(year, List.of()),
                mbtCompostingByYear.getOrDefault(year, List.of()),
                eprPlasticWasteByYear.getOrDefault(year, List.of()),
                kigaliFSTPByYear.getOrDefault(year, List.of()),
                kigaliWWTPByYear.getOrDefault(year, List.of()),
                iswmByYear.getOrDefault(year, List.of())
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
            List<ManureCoveringMitigation> manureCovering,
            List<AddingStrawMitigation> addingStraw,
            List<DailySpreadMitigation> dailySpread,
            List<WasteToEnergyMitigation> wasteToEnergy,
            List<LandfillGasUtilizationMitigation> landfillGasUtilization,
            List<MBTCompostingMitigation> mbtComposting,
            List<EPRPlasticWasteMitigation> eprPlasticWaste,
            List<KigaliFSTPMitigation> kigaliFSTP,
            List<KigaliWWTPMitigation> kigaliWWTP,
            List<ISWMMitigation> iswm) {
        
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
        
        // Manure Covering uses mitigatedN2oEmissionsKilotonnes
        for (ManureCoveringMitigation m : manureCovering) {
            if (m.getMitigatedN2oEmissionsKilotonnes() != null) {
                totalMitigation += m.getMitigatedN2oEmissionsKilotonnes();
            }
        }
        
        // Adding Straw uses mitigatedCh4EmissionsKilotonnes
        for (AddingStrawMitigation a : addingStraw) {
            if (a.getMitigatedCh4EmissionsKilotonnes() != null) {
                totalMitigation += a.getMitigatedCh4EmissionsKilotonnes();
            }
        }
        
        // Daily Spread uses mitigatedCh4EmissionsKilotonnes
        for (DailySpreadMitigation d : dailySpread) {
            if (d.getMitigatedCh4EmissionsKilotonnes() != null) {
                totalMitigation += d.getMitigatedCh4EmissionsKilotonnes();
            }
        }
        
        // Waste-to-Energy uses ghgReductionKilotonnes
        for (WasteToEnergyMitigation m : wasteToEnergy) {
            if (m.getGhgReductionKilotonnes() != null) {
                totalMitigation += m.getGhgReductionKilotonnes();
            }
        }
        
        // Landfill Gas Utilization uses projectReductionEmissions
        for (LandfillGasUtilizationMitigation m : landfillGasUtilization) {
            if (m.getProjectReductionEmissions() != null) {
                totalMitigation += m.getProjectReductionEmissions();
            }
        }
        
        // MBT Composting uses estimatedGhgReductionKilotonnesPerYear
        for (MBTCompostingMitigation m : mbtComposting) {
            if (m.getEstimatedGhgReductionKilotonnesPerYear() != null) {
                totalMitigation += m.getEstimatedGhgReductionKilotonnesPerYear();
            }
        }
        
        // EPR Plastic Waste uses ghgReductionKilotonnes
        for (EPRPlasticWasteMitigation e : eprPlasticWaste) {
            if (e.getGhgReductionKilotonnes() != null) {
                totalMitigation += e.getGhgReductionKilotonnes();
            }
        }
        
        // Kigali FSTP uses annualEmissionsReductionKilotonnes
        for (KigaliFSTPMitigation k : kigaliFSTP) {
            if (k.getAnnualEmissionsReductionKilotonnes() != null) {
                totalMitigation += k.getAnnualEmissionsReductionKilotonnes();
            }
        }
        
        // Kigali WWTP uses annualEmissionsReductionKilotonnes
        for (KigaliWWTPMitigation w : kigaliWWTP) {
            if (w.getAnnualEmissionsReductionKilotonnes() != null) {
                totalMitigation += w.getAnnualEmissionsReductionKilotonnes();
            }
        }
        
        // ISWM uses annualReduction
        for (ISWMMitigation i : iswm) {
            if (i.getNetAnnualReduction() != null) {
                totalMitigation += i.getNetAnnualReduction();
            }
        }
        
        data.setTotalMitigationKtCO2e(totalMitigation);
        
        return data;
    }
}
