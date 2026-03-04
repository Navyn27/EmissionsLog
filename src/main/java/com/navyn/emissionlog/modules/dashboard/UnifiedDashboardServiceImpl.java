package com.navyn.emissionlog.modules.dashboard;

import com.navyn.emissionlog.modules.LandUseEmissions.Repositories.*;
import com.navyn.emissionlog.modules.LandUseEmissions.models.*;
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
import com.navyn.emissionlog.modules.dashboard.dto.DashboardResponseDto;
import com.navyn.emissionlog.modules.dashboard.dto.DashboardSummaryDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.models.CropRotationMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.repositories.CropRotationMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.greenFences.models.GreenFencesMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.greenFences.repositories.GreenFencesMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.models.AddingStrawMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.repository.AddingStrawMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.dailySpread.models.DailySpreadMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.dailySpread.repository.DailySpreadMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.models.ManureCoveringMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.repository.ManureCoveringMitigationRepository;
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
import com.navyn.emissionlog.modules.wasteEmissions.WasteDataRepository;
import com.navyn.emissionlog.modules.wasteEmissions.models.WasteDataAbstract;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UnifiedDashboardServiceImpl implements UnifiedDashboardService {

    // Repositories
    private final ActivityRepository activityRepository;
    private final WasteDataRepository wasteDataRepository;
    private final AquacultureEmissionsRepository aquacultureRepository;
    private final EntericFermentationEmissionsRepository entericRepository;
    private final LimingEmissionsRepository limingRepository;
    private final AnimalManureAndCompostEmissionsRepository manureRepository;
    private final RiceCultivationEmissionsRepository riceRepository;
    private final SyntheticFertilizerEmissionsRepository fertilizerRepository;
    private final UreaEmissionsRepository ureaRepository;
    private final BiomassGainRepository biomassGainRepository;
    private final DisturbanceBiomassLossRepository disturbanceLossRepository;
    private final FirewoodRemovalBiomassLossRepository firewoodLossRepository;
    private final HarvestedBiomassLossRepository harvestedLossRepository;
    private final RewettedMineralWetlandsRepository rewettedWetlandsRepository;
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

    @Override
    public DashboardResponseDto getDashboard(Integer startYear, Integer endYear, String mode) {
        // Default to current year if not specified
        int currentYear = LocalDateTime.now().getYear();
        if (startYear == null)
            startYear = currentYear;
        if (endYear == null)
            endYear = currentYear;

        // Get time series based on mode FIRST
        List<DashboardSummaryDto> timeSeries;
        if ("MONTH".equalsIgnoreCase(mode)) {
            timeSeries = getMonthlyData(startYear);
        } else {
            timeSeries = getYearlyData(startYear, endYear);
        }

        // Calculate summary by summing the timeSeries (ensures card matches chart)
        DashboardSummaryDto summary = sumTimeSeries(timeSeries, startYear, endYear);

        return DashboardResponseDto.builder()
                .summary(summary)
                .timeSeries(timeSeries)
                .startYear(startYear)
                .endYear(endYear)
                .mode(mode)
                .build();
    }

    @Override
    public DashboardSummaryDto getSummary(Integer startYear, Integer endYear) {
        DashboardSummaryDto summary = DashboardSummaryDto.builder().build();

        // Aggregate all emission sources
        aggregateActivities(summary, startYear, endYear);
        aggregateWaste(summary, startYear, endYear);
        aggregateAgriculture(summary, startYear, endYear);
        aggregateLandUse(summary, startYear, endYear);
        aggregateMitigation(summary, startYear, endYear);

        // Calculate totals
        summary.calculateCO2Equivalent();
        summary.calculateNetEmissions();

        summary.setStartDate(startYear + "-01-01");
        summary.setEndDate(endYear + "-12-31");

        return summary;
    }

    @Override
    public List<DashboardSummaryDto> getMonthlyData(Integer year) {
        List<DashboardSummaryDto> monthlyData = new ArrayList<>();

        // Fetch all data for the year
        LocalDateTime startDate = LocalDateTime.of(year, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(year, 12, 31, 23, 59);

        // Activities grouped by month
        List<Activity> activities = activityRepository.findAllByActivityYearBetweenOrderByActivityYearDesc(startDate,
                endDate);
        Map<Integer, List<Activity>> activitiesByMonth = activities.stream()
                .collect(Collectors.groupingBy(a -> a.getActivityYear().getMonthValue()));

        // Waste grouped by month
        List<WasteDataAbstract> wasteData = wasteDataRepository.findByActivityYearBetweenOrderByYearDesc(startDate,
                endDate);
        Map<Integer, List<WasteDataAbstract>> wasteByMonth = wasteData.stream()
                .collect(Collectors.groupingBy(w -> w.getActivityYear().getMonthValue()));

        // Agriculture data (annual - distribute evenly across months)
        DashboardSummaryDto annualAgriculture = getAgricultureSummary(year, year);
        DashboardSummaryDto monthlyAgriculturePortion = divideByMonths(annualAgriculture, 12);

        // Land use data (annual - distribute evenly across months)
        DashboardSummaryDto annualLandUse = getLandUseSummary(year, year);
        DashboardSummaryDto monthlyLandUsePortion = divideByMonths(annualLandUse, 12);

        // Mitigation data (annual - distribute evenly across months)
        double annualMitigation = getMitigationTotal(year, year);
        double monthlyMitigation = annualMitigation / 12.0;

        // Build monthly summaries
        for (int month = 1; month <= 12; month++) {
            DashboardSummaryDto monthSummary = DashboardSummaryDto.builder()
                    .year(year)
                    .month(Month.of(month).name())
                    .build();

            // Add activity emissions for this month
            List<Activity> monthActivities = activitiesByMonth.getOrDefault(month, Collections.emptyList());
            for (Activity a : monthActivities) {
                monthSummary
                        .setTotalCH4EmissionsKt(safeAdd(monthSummary.getTotalCH4EmissionsKt(), a.getCH4Emissions()));
                monthSummary
                        .setTotalN2OEmissionsKt(safeAdd(monthSummary.getTotalN2OEmissionsKt(), a.getN2OEmissions()));
                monthSummary.setTotalFossilCO2EmissionsKt(
                        safeAdd(monthSummary.getTotalFossilCO2EmissionsKt(), a.getFossilCO2Emissions()));
                monthSummary.setTotalBioCO2EmissionsKt(
                        safeAdd(monthSummary.getTotalBioCO2EmissionsKt(), a.getBioCO2Emissions()));
            }

            // Add waste emissions for this month
            List<WasteDataAbstract> monthWaste = wasteByMonth.getOrDefault(month, Collections.emptyList());
            for (WasteDataAbstract w : monthWaste) {
                monthSummary
                        .setTotalCH4EmissionsKt(safeAdd(monthSummary.getTotalCH4EmissionsKt(), w.getCH4Emissions()));
                monthSummary
                        .setTotalN2OEmissionsKt(safeAdd(monthSummary.getTotalN2OEmissionsKt(), w.getN2OEmissions()));
                monthSummary.setTotalFossilCO2EmissionsKt(
                        safeAdd(monthSummary.getTotalFossilCO2EmissionsKt(), w.getFossilCO2Emissions()));
                monthSummary.setTotalBioCO2EmissionsKt(
                        safeAdd(monthSummary.getTotalBioCO2EmissionsKt(), w.getBioCO2Emissions()));
            }

            // Add 1/12 of annual agriculture and land use
            monthSummary.add(monthlyAgriculturePortion);
            monthSummary.add(monthlyLandUsePortion);
            monthSummary.setTotalMitigationKtCO2e(monthlyMitigation);

            // Calculate totals
            monthSummary.calculateCO2Equivalent();
            monthSummary.calculateNetEmissions();

            YearMonth ym = YearMonth.of(year, month);
            monthSummary.setStartDate(ym.atDay(1).toString());
            monthSummary.setEndDate(ym.atEndOfMonth().toString());

            monthlyData.add(monthSummary);
        }

        return monthlyData;
    }

    @Override
    public List<DashboardSummaryDto> getYearlyData(Integer startYear, Integer endYear) {
        List<DashboardSummaryDto> yearlyData = new ArrayList<>();

        for (int year = startYear; year <= endYear; year++) {
            DashboardSummaryDto yearSummary = getSummary(year, year);
            yearSummary.setYear(year);
            yearSummary.setMonth(null);
            yearlyData.add(yearSummary);
        }

        return yearlyData;
    }

    // ==================== PRIVATE HELPER METHODS ====================

    private void aggregateActivities(DashboardSummaryDto summary, Integer startYear, Integer endYear) {
        LocalDateTime start = LocalDateTime.of(startYear, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(endYear, 12, 31, 23, 59);

        List<Activity> activities = activityRepository.findAllByActivityYearBetweenOrderByActivityYearDesc(start, end);

        for (Activity a : activities) {
            summary.setTotalCH4EmissionsKt(safeAdd(summary.getTotalCH4EmissionsKt(), a.getCH4Emissions()));
            summary.setTotalN2OEmissionsKt(safeAdd(summary.getTotalN2OEmissionsKt(), a.getN2OEmissions()));
            summary.setTotalFossilCO2EmissionsKt(
                    safeAdd(summary.getTotalFossilCO2EmissionsKt(), a.getFossilCO2Emissions()));
            summary.setTotalBioCO2EmissionsKt(safeAdd(summary.getTotalBioCO2EmissionsKt(), a.getBioCO2Emissions()));
        }
    }

    private void aggregateWaste(DashboardSummaryDto summary, Integer startYear, Integer endYear) {
        LocalDateTime start = LocalDateTime.of(startYear, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(endYear, 12, 31, 23, 59);

        List<WasteDataAbstract> wasteData = wasteDataRepository.findByActivityYearBetweenOrderByYearDesc(start, end);

        for (WasteDataAbstract w : wasteData) {
            summary.setTotalCH4EmissionsKt(safeAdd(summary.getTotalCH4EmissionsKt(), w.getCH4Emissions()));
            summary.setTotalN2OEmissionsKt(safeAdd(summary.getTotalN2OEmissionsKt(), w.getN2OEmissions()));
            summary.setTotalFossilCO2EmissionsKt(
                    safeAdd(summary.getTotalFossilCO2EmissionsKt(), w.getFossilCO2Emissions()));
            summary.setTotalBioCO2EmissionsKt(safeAdd(summary.getTotalBioCO2EmissionsKt(), w.getBioCO2Emissions()));
        }
    }

    private void aggregateAgriculture(DashboardSummaryDto summary, Integer startYear, Integer endYear) {
        DashboardSummaryDto agSummary = getAgricultureSummary(startYear, endYear);
        summary.add(agSummary);
    }

    private DashboardSummaryDto getAgricultureSummary(Integer startYear, Integer endYear) {
        DashboardSummaryDto summary = DashboardSummaryDto.builder().build();

        // Aquaculture - N2O
        List<AquacultureEmissions> aquaculture = aquacultureRepository.findByYearRange(startYear, endYear);
        for (AquacultureEmissions a : aquaculture) {
            summary.setTotalN2OEmissionsKt(safeAdd(summary.getTotalN2OEmissionsKt(), a.getN2OEmissions()));
        }

        // Enteric Fermentation - CH4
        List<EntericFermentationEmissions> enteric = entericRepository.findByYearRange(startYear, endYear);
        for (EntericFermentationEmissions e : enteric) {
            summary.setTotalCH4EmissionsKt(safeAdd(summary.getTotalCH4EmissionsKt(), e.getCH4Emissions()));
        }

        // Liming - Bio CO2
        List<LimingEmissions> liming = limingRepository.findByYearRange(startYear, endYear);
        for (LimingEmissions l : liming) {
            summary.setTotalBioCO2EmissionsKt(safeAdd(summary.getTotalBioCO2EmissionsKt(), l.getCO2Emissions()));
        }

        // Manure - CH4 and N2O
        List<AnimalManureAndCompostEmissions> manure = manureRepository.findByYearRange(startYear, endYear);
        for (AnimalManureAndCompostEmissions m : manure) {
            summary.setTotalCH4EmissionsKt(safeAdd(summary.getTotalCH4EmissionsKt(), m.getCH4Emissions()));
            summary.setTotalN2OEmissionsKt(safeAdd(summary.getTotalN2OEmissionsKt(), m.getN2OEmissions()));
        }

        // Rice - CH4
        List<RiceCultivationEmissions> rice = riceRepository.findByYearRange(startYear, endYear);
        for (RiceCultivationEmissions r : rice) {
            summary.setTotalCH4EmissionsKt(safeAdd(summary.getTotalCH4EmissionsKt(), r.getAnnualCH4Emissions()));
        }

        // Fertilizer - N2O
        List<SyntheticFertilizerEmissions> fertilizer = fertilizerRepository.findByYearRange(startYear, endYear);
        for (SyntheticFertilizerEmissions f : fertilizer) {
            summary.setTotalN2OEmissionsKt(safeAdd(summary.getTotalN2OEmissionsKt(), f.getN2OEmissions()));
        }

        // Urea - Bio CO2
        List<UreaEmissions> urea = ureaRepository.findByYearRange(startYear, endYear);
        for (UreaEmissions u : urea) {
            summary.setTotalBioCO2EmissionsKt(safeAdd(summary.getTotalBioCO2EmissionsKt(), u.getCO2Emissions()));
        }

        return summary;
    }

    private void aggregateLandUse(DashboardSummaryDto summary, Integer startYear, Integer endYear) {
        DashboardSummaryDto landUseSummary = getLandUseSummary(startYear, endYear);
        summary.setTotalLandUseEmissionsKtCO2e(
                safeAdd(summary.getTotalLandUseEmissionsKtCO2e(), landUseSummary.getTotalLandUseEmissionsKtCO2e()));
    }

    private DashboardSummaryDto getLandUseSummary(Integer startYear, Integer endYear) {
        DashboardSummaryDto summary = DashboardSummaryDto.builder().build();
        double landUseTotal = 0.0;

        // Biomass Gain (sequestration - negative)
        List<BiomassGain> gains = biomassGainRepository.findByYearRange(startYear, endYear);
        for (BiomassGain g : gains) {
            landUseTotal -= safeValue(g.getCO2EqOfBiomassCarbonGained());
        }

        // Disturbance Loss
        List<DisturbanceBiomassLoss> disturbance = disturbanceLossRepository.findByYearRange(startYear, endYear);
        for (DisturbanceBiomassLoss d : disturbance) {
            landUseTotal += safeValue(d.getCO2EqOfBiomassCarbonLoss());
        }

        // Firewood Loss
        List<FirewoodRemovalBiomassLoss> firewood = firewoodLossRepository.findByYearRange(startYear, endYear);
        for (FirewoodRemovalBiomassLoss f : firewood) {
            landUseTotal += safeValue(f.getCO2EqOfBiomassCarbonLoss());
        }

        // Harvested Loss
        List<HarvestedBiomassLoss> harvested = harvestedLossRepository.findByYearRange(startYear, endYear);
        for (HarvestedBiomassLoss h : harvested) {
            landUseTotal += safeValue(h.getCO2EqOfBiomassCarbonLoss());
        }

        // Rewetted Wetlands (reduces emissions - negative)
        List<RewettedMineralWetlands> rewetted = rewettedWetlandsRepository.findByYearRange(startYear, endYear);
        for (RewettedMineralWetlands r : rewetted) {
            landUseTotal -= safeValue(r.getCO2EqEmissions());
        }

        summary.setTotalLandUseEmissionsKtCO2e(landUseTotal);
        return summary;
    }

    private void aggregateMitigation(DashboardSummaryDto summary, Integer startYear, Integer endYear) {
        double totalMitigation = getMitigationTotal(startYear, endYear);
        summary.setTotalMitigationKtCO2e(totalMitigation);
    }

    private double getMitigationTotal(Integer startYear, Integer endYear) {
        double total = 0.0;

        // Wetland Parks
        List<WetlandParksMitigation> wetlandParks = wetlandParksRepository.findByYearRange(startYear, endYear);
        for (WetlandParksMitigation m : wetlandParks) {
            total += safeValue(m.getMitigatedEmissionsKtCO2e());
        }

        // Settlement Trees
        List<SettlementTreesMitigation> settlementTrees = settlementTreesRepository.findByYearRange(startYear, endYear);
        for (SettlementTreesMitigation m : settlementTrees) {
            total += safeValue(m.getMitigatedEmissionsKtCO2e());
        }

        // Street Trees
        List<StreetTreesMitigation> streetTrees = streetTreesRepository.findByYearRange(startYear, endYear);
        for (StreetTreesMitigation m : streetTrees) {
            total += safeValue(m.getMitigatedEmissionsKtCO2e());
        }

        // Green Fences
        List<GreenFencesMitigation> greenFences = greenFencesRepository.findByYearRange(startYear, endYear);
        for (GreenFencesMitigation m : greenFences) {
            total += safeValue(m.getMitigatedEmissionsKtCO2e());
        }

        // Crop Rotation
        List<CropRotationMitigation> cropRotation = cropRotationRepository.findByYearRange(startYear, endYear);
        for (CropRotationMitigation m : cropRotation) {
            total += safeValue(m.getMitigatedEmissionsKtCO2e());
        }

        // Zero Tillage
        List<ZeroTillageMitigation> zeroTillage = zeroTillageRepository.findByYearRange(startYear, endYear);
        for (ZeroTillageMitigation m : zeroTillage) {
            total += safeValue(m.getGhgEmissionsSavings());
        }

        // Protective Forest
        List<ProtectiveForestMitigation> protectiveForest = protectiveForestRepository.findByYearRange(startYear,
                endYear);
        for (ProtectiveForestMitigation m : protectiveForest) {
            total += safeValue(m.getMitigatedEmissionsKtCO2e());
        }

        // Manure Covering
        List<ManureCoveringMitigation> manureCovering = manureCoveringRepository.findByYearRange(startYear, endYear);
        for (ManureCoveringMitigation m : manureCovering) {
            total += safeValue(m.getMitigatedN2oEmissionsKilotonnes());
        }

        // Adding Straw
        List<AddingStrawMitigation> addingStraw = addingStrawRepository.findByYearRange(startYear, endYear);
        for (AddingStrawMitigation m : addingStraw) {
            total += safeValue(m.getMitigatedCh4EmissionsKilotonnes());
        }

        // Daily Spread
        List<DailySpreadMitigation> dailySpread = dailySpreadRepository.findByYearRange(startYear, endYear);
        for (DailySpreadMitigation m : dailySpread) {
            total += safeValue(m.getMitigatedCh4EmissionsKilotonnes());
        }

        return total;
    }

    private DashboardSummaryDto divideByMonths(DashboardSummaryDto annual, int months) {
        if (annual == null || months <= 0) {
            return DashboardSummaryDto.builder().build();
        }

        return DashboardSummaryDto.builder()
                .totalN2OEmissionsKt(safeValue(annual.getTotalN2OEmissionsKt()) / months)
                .totalCH4EmissionsKt(safeValue(annual.getTotalCH4EmissionsKt()) / months)
                .totalFossilCO2EmissionsKt(safeValue(annual.getTotalFossilCO2EmissionsKt()) / months)
                .totalBioCO2EmissionsKt(safeValue(annual.getTotalBioCO2EmissionsKt()) / months)
                .totalLandUseEmissionsKtCO2e(safeValue(annual.getTotalLandUseEmissionsKtCO2e()) / months)
                .totalMitigationKtCO2e(safeValue(annual.getTotalMitigationKtCO2e()) / months)
                .build();
    }

    private Double safeAdd(Double a, Double b) {
        return safeValue(a) + safeValue(b);
    }

    private double safeValue(Double value) {
        if (value == null || Double.isNaN(value) || Double.isInfinite(value)) {
            return 0.0;
        }
        return value;
    }

    /**
     * Sum all timeSeries data points to create the summary
     * This ensures card totals match chart sums exactly
     */
    private DashboardSummaryDto sumTimeSeries(List<DashboardSummaryDto> timeSeries, Integer startYear,
            Integer endYear) {
        DashboardSummaryDto summary = DashboardSummaryDto.builder().build();

        for (DashboardSummaryDto item : timeSeries) {
            summary.setTotalN2OEmissionsKt(safeAdd(summary.getTotalN2OEmissionsKt(), item.getTotalN2OEmissionsKt()));
            summary.setTotalCH4EmissionsKt(safeAdd(summary.getTotalCH4EmissionsKt(), item.getTotalCH4EmissionsKt()));
            summary.setTotalFossilCO2EmissionsKt(
                    safeAdd(summary.getTotalFossilCO2EmissionsKt(), item.getTotalFossilCO2EmissionsKt()));
            summary.setTotalBioCO2EmissionsKt(
                    safeAdd(summary.getTotalBioCO2EmissionsKt(), item.getTotalBioCO2EmissionsKt()));
            summary.setTotalLandUseEmissionsKtCO2e(
                    safeAdd(summary.getTotalLandUseEmissionsKtCO2e(), item.getTotalLandUseEmissionsKtCO2e()));
            summary.setTotalCO2EqEmissionsKtCO2e(
                    safeAdd(summary.getTotalCO2EqEmissionsKtCO2e(), item.getTotalCO2EqEmissionsKtCO2e()));
            summary.setTotalMitigationKtCO2e(
                    safeAdd(summary.getTotalMitigationKtCO2e(), item.getTotalMitigationKtCO2e()));
            summary.setNetEmissionsKtCO2e(safeAdd(summary.getNetEmissionsKtCO2e(), item.getNetEmissionsKtCO2e()));
        }

        summary.setStartDate(startYear + "-01-01");
        summary.setEndDate(endYear + "-12-31");

        return summary;
    }
}
