package com.navyn.emissionlog.modules.mitigationProjects.AFOLU;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.dto.AFOLUDashboardSummaryDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.dto.AFOLUDashboardYearDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.models.WetlandParksMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.repositories.WetlandParksMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.settlementTrees.models.SettlementTreesMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.settlementTrees.repositories.SettlementTreesMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.streetTrees.models.StreetTreesMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.streetTrees.repositories.StreetTreesMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.greenFences.models.GreenFencesMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.greenFences.repositories.GreenFencesMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.models.CropRotationMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.repositories.CropRotationMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.zeroTillage.models.ZeroTillageMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.zeroTillage.repositories.ZeroTillageMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.protectiveForest.models.ProtectiveForestMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.protectiveForest.repositories.ProtectiveForestMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.models.ManureCoveringMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.repository.ManureCoveringMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.models.AddingStrawMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.repository.AddingStrawMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.dailySpread.models.DailySpreadMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.dailySpread.repository.DailySpreadMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.enums.ESector;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.models.BAU;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.repositories.BAURepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xddf.usermodel.chart.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class AFOLUDashboardServiceImpl implements AFOLUDashboardService {

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
    private final BAURepository bauRepository;

    @Override
    @Transactional(readOnly = true)
    public AFOLUDashboardSummaryDto getAFOLUDashboardSummary(Integer startingYear, Integer endingYear) {
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

        // Filter by year range if specified
        if (startingYear != null && endingYear != null) {
            wetlandParks = filterByYear(wetlandParks, WetlandParksMitigation::getYear, startingYear, endingYear);
            settlementTrees = filterByYear(settlementTrees, SettlementTreesMitigation::getYear, startingYear, endingYear);
            streetTrees = filterByYear(streetTrees, StreetTreesMitigation::getYear, startingYear, endingYear);
            greenFences = filterByYear(greenFences, GreenFencesMitigation::getYear, startingYear, endingYear);
            cropRotation = filterByYear(cropRotation, CropRotationMitigation::getYear, startingYear, endingYear);
            zeroTillage = filterByYear(zeroTillage, ZeroTillageMitigation::getYear, startingYear, endingYear);
            protectiveForest = filterByYear(protectiveForest, ProtectiveForestMitigation::getYear, startingYear, endingYear);
            manureCovering = filterByYear(manureCovering, ManureCoveringMitigation::getYear, startingYear, endingYear);
            addingStraw = filterByYear(addingStraw, AddingStrawMitigation::getYear, startingYear, endingYear);
            dailySpread = filterByYear(dailySpread, DailySpreadMitigation::getYear, startingYear, endingYear);
        }

        // Aggregate: Wetland Parks (composite uniqueness - sum by year across categories)
        double wetlandParksTotal = wetlandParks.stream()
                .filter(w -> w.getMitigatedEmissionsKtCO2e() != null)
                .mapToDouble(WetlandParksMitigation::getMitigatedEmissionsKtCO2e)
                .sum();

        // Aggregate: Settlement Trees
        double settlementTreesTotal = sumDouble(settlementTrees, SettlementTreesMitigation::getMitigatedEmissionsKtCO2e);

        // Aggregate: Street Trees
        double streetTreesTotal = sumDouble(streetTrees, StreetTreesMitigation::getMitigatedEmissionsKtCO2e);

        // Aggregate: Green Fences
        double greenFencesTotal = sumDouble(greenFences, GreenFencesMitigation::getMitigatedEmissionsKtCO2e);

        // Aggregate: Crop Rotation
        double cropRotationTotal = sumDouble(cropRotation, CropRotationMitigation::getMitigatedEmissionsKtCO2e);

        // Aggregate: Zero Tillage (different field name)
        double zeroTillageTotal = sumDouble(zeroTillage, ZeroTillageMitigation::getGhgEmissionsSavings);

        // Aggregate: Protective Forest (composite uniqueness - sum by year across categories)
        double protectiveForestTotal = protectiveForest.stream()
                .filter(p -> p.getMitigatedEmissionsKtCO2e() != null)
                .mapToDouble(ProtectiveForestMitigation::getMitigatedEmissionsKtCO2e)
                .sum();

        // Aggregate: Manure Covering (different field name)
        double manureCoveringTotal = sumDouble(manureCovering, ManureCoveringMitigation::getMitigatedN2oEmissionsKilotonnes);

        // Aggregate: Adding Straw (different field name)
        double addingStrawTotal = sumDouble(addingStraw, AddingStrawMitigation::getMitigatedCh4EmissionsKilotonnes);

        // Aggregate: Daily Spread (different field name)
        double dailySpreadTotal = sumDouble(dailySpread, DailySpreadMitigation::getMitigatedCh4EmissionsKilotonnes);

        double totalMitigation = wetlandParksTotal + settlementTreesTotal + streetTreesTotal + greenFencesTotal
                + cropRotationTotal + zeroTillageTotal + protectiveForestTotal + manureCoveringTotal
                + addingStrawTotal + dailySpreadTotal;

        // Calculate Improved MMS Total
        double improvedMMSTotal = manureCoveringTotal + addingStrawTotal + dailySpreadTotal;

        // Calculate Adjustment Mitigation: Sum of BAU values in year range - Total Mitigation
        double bauSum = 0.0;
        if (startingYear != null && endingYear != null) {
            for (int year = startingYear; year <= endingYear; year++) {
                Optional<BAU> bau = bauRepository.findByYearAndSector(year, ESector.AFOLU);
                if (bau.isPresent() && bau.get().getValue() != null) {
                    bauSum += bau.get().getValue();
                }
            }
        } else {
            // If no year range specified, get all AFOLU BAU records
            List<BAU> allBAUs = bauRepository.findBySectorOrderByYearAsc(ESector.AFOLU);
            bauSum = allBAUs.stream()
                    .filter(b -> b.getValue() != null)
                    .mapToDouble(BAU::getValue)
                    .sum();
        }
        double adjustmentMitigation = bauSum - totalMitigation;

        // Calculate record counts
        Map<String, Long> recordCounts = new HashMap<>();
        recordCounts.put("wetlandParks", (long) wetlandParks.size());
        recordCounts.put("settlementTrees", (long) settlementTrees.size());
        recordCounts.put("streetTrees", (long) streetTrees.size());
        recordCounts.put("greenFences", (long) greenFences.size());
        recordCounts.put("cropRotation", (long) cropRotation.size());
        recordCounts.put("zeroTillage", (long) zeroTillage.size());
        recordCounts.put("protectiveForest", (long) protectiveForest.size());
        recordCounts.put("manureCovering", (long) manureCovering.size());
        recordCounts.put("addingStraw", (long) addingStraw.size());
        recordCounts.put("dailySpread", (long) dailySpread.size());

        // Calculate data coverage (percentage of years with data)
        Map<String, Double> dataCoverage = new HashMap<>();
        Set<Integer> wetlandParksYears = wetlandParks.stream().map(WetlandParksMitigation::getYear).collect(Collectors.toSet());
        Set<Integer> settlementTreesYears = settlementTrees.stream().map(SettlementTreesMitigation::getYear).collect(Collectors.toSet());
        Set<Integer> streetTreesYears = streetTrees.stream().map(StreetTreesMitigation::getYear).collect(Collectors.toSet());
        Set<Integer> greenFencesYears = greenFences.stream().map(GreenFencesMitigation::getYear).collect(Collectors.toSet());
        Set<Integer> cropRotationYears = cropRotation.stream().map(CropRotationMitigation::getYear).collect(Collectors.toSet());
        Set<Integer> zeroTillageYears = zeroTillage.stream().map(ZeroTillageMitigation::getYear).collect(Collectors.toSet());
        Set<Integer> protectiveForestYears = protectiveForest.stream().map(ProtectiveForestMitigation::getYear).collect(Collectors.toSet());
        Set<Integer> manureCoveringYears = manureCovering.stream().map(ManureCoveringMitigation::getYear).collect(Collectors.toSet());
        Set<Integer> addingStrawYears = addingStraw.stream().map(AddingStrawMitigation::getYear).collect(Collectors.toSet());
        Set<Integer> dailySpreadYears = dailySpread.stream().map(DailySpreadMitigation::getYear).collect(Collectors.toSet());

        if (startingYear != null && endingYear != null) {
            dataCoverage.put("wetlandParks", calculateCoverage(wetlandParksYears, startingYear, endingYear));
            dataCoverage.put("settlementTrees", calculateCoverage(settlementTreesYears, startingYear, endingYear));
            dataCoverage.put("streetTrees", calculateCoverage(streetTreesYears, startingYear, endingYear));
            dataCoverage.put("greenFences", calculateCoverage(greenFencesYears, startingYear, endingYear));
            dataCoverage.put("cropRotation", calculateCoverage(cropRotationYears, startingYear, endingYear));
            dataCoverage.put("zeroTillage", calculateCoverage(zeroTillageYears, startingYear, endingYear));
            dataCoverage.put("protectiveForest", calculateCoverage(protectiveForestYears, startingYear, endingYear));
            dataCoverage.put("manureCovering", calculateCoverage(manureCoveringYears, startingYear, endingYear));
            dataCoverage.put("addingStraw", calculateCoverage(addingStrawYears, startingYear, endingYear));
            dataCoverage.put("dailySpread", calculateCoverage(dailySpreadYears, startingYear, endingYear));
        }

        // Calculate intervention breakdown (aggregate by intervention name)
        Map<String, Double> interventionBreakdown = new HashMap<>();
        
        // Aggregate interventions from all projects - initialize lazy-loaded interventions first
        wetlandParks.stream()
            .filter(w -> w.getIntervention() != null && w.getMitigatedEmissionsKtCO2e() != null)
            .forEach(w -> {
                Hibernate.initialize(w.getIntervention());
                interventionBreakdown.merge(
                    w.getIntervention().getName(),
                    w.getMitigatedEmissionsKtCO2e(),
                    Double::sum
                );
            });
        
        protectiveForest.stream()
            .filter(p -> p.getIntervention() != null && p.getMitigatedEmissionsKtCO2e() != null)
            .forEach(p -> {
                Hibernate.initialize(p.getIntervention());
                interventionBreakdown.merge(
                    p.getIntervention().getName(),
                    p.getMitigatedEmissionsKtCO2e(),
                    Double::sum
                );
            });
        
        settlementTrees.stream()
            .filter(s -> s.getIntervention() != null && s.getMitigatedEmissionsKtCO2e() != null)
            .forEach(s -> {
                Hibernate.initialize(s.getIntervention());
                interventionBreakdown.merge(
                    s.getIntervention().getName(),
                    s.getMitigatedEmissionsKtCO2e(),
                    Double::sum
                );
            });
        
        streetTrees.stream()
            .filter(s -> s.getIntervention() != null && s.getMitigatedEmissionsKtCO2e() != null)
            .forEach(s -> {
                Hibernate.initialize(s.getIntervention());
                interventionBreakdown.merge(
                    s.getIntervention().getName(),
                    s.getMitigatedEmissionsKtCO2e(),
                    Double::sum
                );
            });
        
        greenFences.stream()
            .filter(g -> g.getIntervention() != null && g.getMitigatedEmissionsKtCO2e() != null)
            .forEach(g -> {
                Hibernate.initialize(g.getIntervention());
                interventionBreakdown.merge(
                    g.getIntervention().getName(),
                    g.getMitigatedEmissionsKtCO2e(),
                    Double::sum
                );
            });
        
        cropRotation.stream()
            .filter(c -> c.getIntervention() != null && c.getMitigatedEmissionsKtCO2e() != null)
            .forEach(c -> {
                Hibernate.initialize(c.getIntervention());
                interventionBreakdown.merge(
                    c.getIntervention().getName(),
                    c.getMitigatedEmissionsKtCO2e(),
                    Double::sum
                );
            });
        
        zeroTillage.stream()
            .filter(z -> z.getIntervention() != null && z.getGhgEmissionsSavings() != null)
            .forEach(z -> {
                Hibernate.initialize(z.getIntervention());
                interventionBreakdown.merge(
                    z.getIntervention().getName(),
                    z.getGhgEmissionsSavings(),
                    Double::sum
                );
            });
        
        manureCovering.stream()
            .filter(m -> m.getIntervention() != null && m.getMitigatedN2oEmissionsKilotonnes() != null)
            .forEach(m -> {
                Hibernate.initialize(m.getIntervention());
                interventionBreakdown.merge(
                    m.getIntervention().getName(),
                    m.getMitigatedN2oEmissionsKilotonnes(),
                    Double::sum
                );
            });
        
        addingStraw.stream()
            .filter(a -> a.getIntervention() != null && a.getMitigatedCh4EmissionsKilotonnes() != null)
            .forEach(a -> {
                Hibernate.initialize(a.getIntervention());
                interventionBreakdown.merge(
                    a.getIntervention().getName(),
                    a.getMitigatedCh4EmissionsKilotonnes(),
                    Double::sum
                );
            });
        
        dailySpread.stream()
            .filter(d -> d.getIntervention() != null && d.getMitigatedCh4EmissionsKilotonnes() != null)
            .forEach(d -> {
                Hibernate.initialize(d.getIntervention());
                interventionBreakdown.merge(
                    d.getIntervention().getName(),
                    d.getMitigatedCh4EmissionsKilotonnes(),
                    Double::sum
                );
            });

        AFOLUDashboardSummaryDto dto = new AFOLUDashboardSummaryDto();
        dto.setStartingYear(startingYear);
        dto.setEndingYear(endingYear);
        dto.setWetlandParks(wetlandParksTotal);
        dto.setSettlementTrees(settlementTreesTotal);
        dto.setStreetTrees(streetTreesTotal);
        dto.setGreenFences(greenFencesTotal);
        dto.setCropRotation(cropRotationTotal);
        dto.setZeroTillage(zeroTillageTotal);
        dto.setProtectiveForest(protectiveForestTotal);
        dto.setManureCovering(manureCoveringTotal);
        dto.setAddingStraw(addingStrawTotal);
        dto.setDailySpread(dailySpreadTotal);
        dto.setTotalMitigationKtCO2e(totalMitigation);
        dto.setTotalBAU(bauSum);
        dto.setAdjustmentMitigation(adjustmentMitigation);
        dto.setImprovedMMSTotal(improvedMMSTotal);
        dto.setRecordCounts(recordCounts);
        dto.setDataCoverage(dataCoverage);
        dto.setInterventionBreakdown(interventionBreakdown.isEmpty() ? null : interventionBreakdown);

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AFOLUDashboardYearDto> getAFOLUDashboardGraph(Integer startingYear, Integer endingYear) {
        int currentYear = LocalDateTime.now().getYear();
        int start = startingYear != null ? startingYear : currentYear - 4;
        int end = endingYear != null ? endingYear : currentYear;

        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }

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

        // Group by year for composite uniqueness projects
        Map<Integer, Double> wetlandParksByYear = wetlandParks.stream()
                .filter(w -> w.getMitigatedEmissionsKtCO2e() != null)
                .collect(Collectors.groupingBy(
                        WetlandParksMitigation::getYear,
                        Collectors.summingDouble(WetlandParksMitigation::getMitigatedEmissionsKtCO2e)
                ));

        Map<Integer, Double> protectiveForestByYear = protectiveForest.stream()
                .filter(p -> p.getMitigatedEmissionsKtCO2e() != null)
                .collect(Collectors.groupingBy(
                        ProtectiveForestMitigation::getYear,
                        Collectors.summingDouble(ProtectiveForestMitigation::getMitigatedEmissionsKtCO2e)
                ));

        List<AFOLUDashboardYearDto> response = new ArrayList<>();
        for (int year = start; year <= end; year++) {
            // Get values for each project for this year
            // Composite uniqueness projects (already grouped by year)
            double wetlandParksValue = wetlandParksByYear.getOrDefault(year, 0.0);
            double protectiveForestValue = protectiveForestByYear.getOrDefault(year, 0.0);
            
            // Simple uniqueness projects (use sumDouble with filterByYear)
            double settlementTreesValue = sumDouble(
                    filterByYear(settlementTrees, SettlementTreesMitigation::getYear, year, year),
                    SettlementTreesMitigation::getMitigatedEmissionsKtCO2e);
            double streetTreesValue = sumDouble(
                    filterByYear(streetTrees, StreetTreesMitigation::getYear, year, year),
                    StreetTreesMitigation::getMitigatedEmissionsKtCO2e);
            double greenFencesValue = sumDouble(
                    filterByYear(greenFences, GreenFencesMitigation::getYear, year, year),
                    GreenFencesMitigation::getMitigatedEmissionsKtCO2e);
            double cropRotationValue = sumDouble(
                    filterByYear(cropRotation, CropRotationMitigation::getYear, year, year),
                    CropRotationMitigation::getMitigatedEmissionsKtCO2e);
            double zeroTillageValue = sumDouble(
                    filterByYear(zeroTillage, ZeroTillageMitigation::getYear, year, year),
                    ZeroTillageMitigation::getGhgEmissionsSavings);
            double manureCoveringValue = sumDouble(
                    filterByYear(manureCovering, ManureCoveringMitigation::getYear, year, year),
                    ManureCoveringMitigation::getMitigatedN2oEmissionsKilotonnes);
            double addingStrawValue = sumDouble(
                    filterByYear(addingStraw, AddingStrawMitigation::getYear, year, year),
                    AddingStrawMitigation::getMitigatedCh4EmissionsKilotonnes);
            double dailySpreadValue = sumDouble(
                    filterByYear(dailySpread, DailySpreadMitigation::getYear, year, year),
                    DailySpreadMitigation::getMitigatedCh4EmissionsKilotonnes);

            double totalMitigation = wetlandParksValue + settlementTreesValue + streetTreesValue + greenFencesValue
                    + cropRotationValue + zeroTillageValue + protectiveForestValue + manureCoveringValue
                    + addingStrawValue + dailySpreadValue;

            // Calculate Improved MMS Total for this year
            double improvedMMSTotal = manureCoveringValue + addingStrawValue + dailySpreadValue;

            // Calculate BAU and Adjustment Mitigation for this year
            double bauValue = 0.0;
            double adjustmentMitigation = 0.0;
            Optional<BAU> bau = bauRepository.findByYearAndSector(year, ESector.AFOLU);
            if (bau.isPresent() && bau.get().getValue() != null) {
                bauValue = bau.get().getValue();
                adjustmentMitigation = bauValue - totalMitigation;
            }

            AFOLUDashboardYearDto dto = new AFOLUDashboardYearDto();
            dto.setYear(year);
            dto.setWetlandParks(wetlandParksValue);
            dto.setSettlementTrees(settlementTreesValue);
            dto.setStreetTrees(streetTreesValue);
            dto.setGreenFences(greenFencesValue);
            dto.setCropRotation(cropRotationValue);
            dto.setZeroTillage(zeroTillageValue);
            dto.setProtectiveForest(protectiveForestValue);
            dto.setManureCovering(manureCoveringValue);
            dto.setAddingStraw(addingStrawValue);
            dto.setDailySpread(dailySpreadValue);
            dto.setTotalMitigationKtCO2e(totalMitigation);
            dto.setBauValue(bauValue);
            dto.setAdjustmentMitigation(adjustmentMitigation);
            dto.setImprovedMMSTotal(improvedMMSTotal);

            response.add(dto);
        }

        return response;
    }

    private <T> List<T> filterByYear(List<T> source, Function<T, Integer> yearExtractor, Integer start, Integer end) {
        return source.stream()
                .filter(item -> {
                    Integer year = yearExtractor.apply(item);
                    return year != null && year >= start && year <= end;
                })
                .toList();
    }

    private <T> double sumDouble(List<T> source, Function<T, Double> extractor) {
        return source.stream()
                .map(extractor)
                .filter(value -> value != null)
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    private double calculateCoverage(Set<Integer> yearsWithData, int startYear, int endYear) {
        if (startYear > endYear) return 0.0;
        long yearsInRange = endYear - startYear + 1;
        long yearsWithDataCount = yearsWithData.stream()
                .filter(year -> year >= startYear && year <= endYear)
                .count();
        return yearsInRange > 0 ? (double) yearsWithDataCount / yearsInRange * 100.0 : 0.0;
    }


    @Override
    public byte[] exportAFOLUDashboard(Integer startingYear, Integer endingYear) {
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

        // Find min/max year
        int minYear = Stream.of(
                        wetlandParks.stream().map(WetlandParksMitigation::getYear),
                        settlementTrees.stream().map(SettlementTreesMitigation::getYear),
                        streetTrees.stream().map(StreetTreesMitigation::getYear),
                        greenFences.stream().map(GreenFencesMitigation::getYear),
                        cropRotation.stream().map(CropRotationMitigation::getYear),
                        zeroTillage.stream().map(ZeroTillageMitigation::getYear),
                        protectiveForest.stream().map(ProtectiveForestMitigation::getYear),
                        manureCovering.stream().map(ManureCoveringMitigation::getYear),
                        addingStraw.stream().map(AddingStrawMitigation::getYear),
                        dailySpread.stream().map(DailySpreadMitigation::getYear))
                .flatMap(s -> s)
                .filter(y -> y != null)
                .min(Comparator.naturalOrder())
                .orElse(LocalDateTime.now().getYear());

        int maxYear = Stream.of(
                        wetlandParks.stream().map(WetlandParksMitigation::getYear),
                        settlementTrees.stream().map(SettlementTreesMitigation::getYear),
                        streetTrees.stream().map(StreetTreesMitigation::getYear),
                        greenFences.stream().map(GreenFencesMitigation::getYear),
                        cropRotation.stream().map(CropRotationMitigation::getYear),
                        zeroTillage.stream().map(ZeroTillageMitigation::getYear),
                        protectiveForest.stream().map(ProtectiveForestMitigation::getYear),
                        manureCovering.stream().map(ManureCoveringMitigation::getYear),
                        addingStraw.stream().map(AddingStrawMitigation::getYear),
                        dailySpread.stream().map(DailySpreadMitigation::getYear))
                .flatMap(s -> s)
                .filter(y -> y != null)
                .max(Comparator.naturalOrder())
                .orElse(LocalDateTime.now().getYear());

        int start = Optional.ofNullable(startingYear).orElse(minYear);
        int end = Optional.ofNullable(endingYear).orElse(maxYear);
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }

        wetlandParks = filterByYear(wetlandParks, WetlandParksMitigation::getYear, start, end);
        settlementTrees = filterByYear(settlementTrees, SettlementTreesMitigation::getYear, start, end);
        streetTrees = filterByYear(streetTrees, StreetTreesMitigation::getYear, start, end);
        greenFences = filterByYear(greenFences, GreenFencesMitigation::getYear, start, end);
        cropRotation = filterByYear(cropRotation, CropRotationMitigation::getYear, start, end);
        zeroTillage = filterByYear(zeroTillage, ZeroTillageMitigation::getYear, start, end);
        protectiveForest = filterByYear(protectiveForest, ProtectiveForestMitigation::getYear, start, end);
        manureCovering = filterByYear(manureCovering, ManureCoveringMitigation::getYear, start, end);
        addingStraw = filterByYear(addingStraw, AddingStrawMitigation::getYear, start, end);
        dailySpread = filterByYear(dailySpread, DailySpreadMitigation::getYear, start, end);

        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            CreationHelper creationHelper = workbook.getCreationHelper();

            // Create professional styles
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle alternateDataStyle = createAlternateDataStyle(workbook);
            CellStyle summaryHeaderStyle = createSummaryHeaderStyle(workbook);
            CellStyle summaryDataStyle = createSummaryDataStyle(workbook);
            CellStyle numberStyle = createNumberStyle(workbook);
            DataFormat dataFormat = workbook.createDataFormat();

            XSSFSheet summarySheet = workbook.createSheet("Summary");
            buildSummarySheet(summarySheet, titleStyle, headerStyle, dataStyle, alternateDataStyle,
                    summaryHeaderStyle, summaryDataStyle, numberStyle, dataFormat, start, end,
                    wetlandParks, settlementTrees, streetTrees, greenFences, cropRotation,
                    zeroTillage, protectiveForest, manureCovering, addingStraw, dailySpread, creationHelper);

            // Create individual project sheets
            buildWetlandParksSheet(workbook.createSheet("Wetland Parks"), headerStyle, dataStyle,
                    alternateDataStyle, numberStyle, wetlandParks);
            buildSettlementTreesSheet(workbook.createSheet("Settlement Trees"), headerStyle, dataStyle,
                    alternateDataStyle, numberStyle, settlementTrees);
            buildStreetTreesSheet(workbook.createSheet("Street Trees"), headerStyle, dataStyle,
                    alternateDataStyle, numberStyle, streetTrees);
            buildGreenFencesSheet(workbook.createSheet("Green Fences"), headerStyle, dataStyle,
                    alternateDataStyle, numberStyle, greenFences);
            buildCropRotationSheet(workbook.createSheet("Crop Rotation"), headerStyle, dataStyle,
                    alternateDataStyle, numberStyle, cropRotation);
            buildZeroTillageSheet(workbook.createSheet("Zero Tillage"), headerStyle, dataStyle,
                    alternateDataStyle, numberStyle, zeroTillage);
            buildProtectiveForestSheet(workbook.createSheet("Protective Forest"), headerStyle, dataStyle,
                    alternateDataStyle, numberStyle, protectiveForest);
            buildManureCoveringSheet(workbook.createSheet("Manure Covering"), headerStyle, dataStyle,
                    alternateDataStyle, numberStyle, manureCovering);
            buildAddingStrawSheet(workbook.createSheet("Adding Straw"), headerStyle, dataStyle,
                    alternateDataStyle, numberStyle, addingStraw);
            buildDailySpreadSheet(workbook.createSheet("Daily Spread"), headerStyle, dataStyle,
                    alternateDataStyle, numberStyle, dailySpread);

            workbook.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate AFOLU dashboard export", e);
        }
    }

    // Style creation methods
    private CellStyle createTitleStyle(Workbook workbook) {
        XSSFCellStyle style = (XSSFCellStyle) workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 18);
        font.setColor(IndexedColors.WHITE.getIndex());
        font.setFontName("Calibri");
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.MEDIUM);
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.MEDIUM);
        style.setBorderRight(BorderStyle.MEDIUM);
        return style;
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        XSSFCellStyle style = (XSSFCellStyle) workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        font.setColor(IndexedColors.WHITE.getIndex());
        font.setFontName("Calibri");
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setWrapText(true);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        XSSFCellStyle style = (XSSFCellStyle) workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);
        return style;
    }

    private CellStyle createAlternateDataStyle(Workbook workbook) {
        CellStyle style = createDataStyle(workbook);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle createSummaryHeaderStyle(Workbook workbook) {
        XSSFCellStyle style = (XSSFCellStyle) workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        font.setColor(IndexedColors.WHITE.getIndex());
        font.setFontName("Calibri");
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.TEAL.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.MEDIUM);
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.MEDIUM);
        style.setBorderRight(BorderStyle.MEDIUM);
        return style;
    }

    private CellStyle createSummaryDataStyle(Workbook workbook) {
        XSSFCellStyle style = (XSSFCellStyle) workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        font.setFontName("Calibri");
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderTop(BorderStyle.MEDIUM);
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.MEDIUM);
        style.setBorderRight(BorderStyle.MEDIUM);
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createNumberStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        DataFormat dataFormat = workbook.createDataFormat();
        style.setDataFormat(dataFormat.getFormat("#,##0.00"));
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createAlternateNumberStyle(Workbook workbook) {
        CellStyle style = createNumberStyle(workbook);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private void buildSummarySheet(XSSFSheet sheet, CellStyle titleStyle, CellStyle headerStyle,
                                   CellStyle dataStyle, CellStyle alternateDataStyle,
                                   CellStyle summaryHeaderStyle, CellStyle summaryDataStyle,
                                   CellStyle numberStyle, DataFormat dataFormat,
                                   int startYear, int endYear,
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
                                   CreationHelper creationHelper) {
        int rowIdx = 0;

        // Title row
        Row title = sheet.createRow(rowIdx++);
        Cell titleCell = title.createCell(0);
        titleCell.setCellValue("AFOLU Mitigation Dashboard Summary");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 14));
        title.setHeightInPoints(30);

        rowIdx++; // Blank row

        // Year Range row
        Row rangeRow = sheet.createRow(rowIdx++);
        Cell rangeLabel = rangeRow.createCell(0);
        rangeLabel.setCellValue("Year Range:");
        CellStyle labelStyle = sheet.getWorkbook().createCellStyle();
        Font labelFont = sheet.getWorkbook().createFont();
        labelFont.setBold(true);
        labelFont.setFontHeightInPoints((short) 11);
        labelStyle.setFont(labelFont);
        labelStyle.setAlignment(HorizontalAlignment.LEFT);
        rangeLabel.setCellStyle(labelStyle);

        Cell rangeStart = rangeRow.createCell(1);
        rangeStart.setCellValue(startYear);
        CellStyle yearStyle = sheet.getWorkbook().createCellStyle();
        yearStyle.setDataFormat(dataFormat.getFormat("0"));
        yearStyle.setAlignment(HorizontalAlignment.LEFT);
        rangeStart.setCellStyle(yearStyle);

        Cell rangeTo = rangeRow.createCell(2);
        rangeTo.setCellValue("to");
        rangeTo.setCellStyle(labelStyle);

        Cell rangeEnd = rangeRow.createCell(3);
        rangeEnd.setCellValue(endYear);
        rangeEnd.setCellStyle(yearStyle);

        // Calculate totals
        Map<Integer, Double> wetlandParksByYear = wetlandParks.stream()
                .filter(w -> w.getMitigatedEmissionsKtCO2e() != null)
                .collect(Collectors.groupingBy(
                        WetlandParksMitigation::getYear,
                        Collectors.summingDouble(WetlandParksMitigation::getMitigatedEmissionsKtCO2e)
                ));
        Map<Integer, Double> protectiveForestByYear = protectiveForest.stream()
                .filter(p -> p.getMitigatedEmissionsKtCO2e() != null)
                .collect(Collectors.groupingBy(
                        ProtectiveForestMitigation::getYear,
                        Collectors.summingDouble(ProtectiveForestMitigation::getMitigatedEmissionsKtCO2e)
                ));

        double wetlandParksTotal = wetlandParksByYear.values().stream().mapToDouble(Double::doubleValue).sum();
        double settlementTreesTotal = sumDouble(settlementTrees, SettlementTreesMitigation::getMitigatedEmissionsKtCO2e);
        double streetTreesTotal = sumDouble(streetTrees, StreetTreesMitigation::getMitigatedEmissionsKtCO2e);
        double greenFencesTotal = sumDouble(greenFences, GreenFencesMitigation::getMitigatedEmissionsKtCO2e);
        double cropRotationTotal = sumDouble(cropRotation, CropRotationMitigation::getMitigatedEmissionsKtCO2e);
        double zeroTillageTotal = sumDouble(zeroTillage, ZeroTillageMitigation::getGhgEmissionsSavings);
        double protectiveForestTotal = protectiveForestByYear.values().stream().mapToDouble(Double::doubleValue).sum();
        double manureCoveringTotal = sumDouble(manureCovering, ManureCoveringMitigation::getMitigatedN2oEmissionsKilotonnes);
        double addingStrawTotal = sumDouble(addingStraw, AddingStrawMitigation::getMitigatedCh4EmissionsKilotonnes);
        double dailySpreadTotal = sumDouble(dailySpread, DailySpreadMitigation::getMitigatedCh4EmissionsKilotonnes);
        double totalMitigation = wetlandParksTotal + settlementTreesTotal + streetTreesTotal + greenFencesTotal
                + cropRotationTotal + zeroTillageTotal + protectiveForestTotal + manureCoveringTotal
                + addingStrawTotal + dailySpreadTotal;

        rowIdx++; // Blank row

        // Totals Summary Section
        Row totalsHeader = sheet.createRow(rowIdx++);
        totalsHeader.setHeightInPoints(20);
        // Calculate Improved MMS Total
        double improvedMMSTotal = manureCoveringTotal + addingStrawTotal + dailySpreadTotal;
        
        // Calculate Adjustment Mitigation
        double bauSum = 0.0;
        for (int year = startYear; year <= endYear; year++) {
            Optional<BAU> bau = bauRepository.findByYearAndSector(year, ESector.AFOLU);
            if (bau.isPresent() && bau.get().getValue() != null) {
                bauSum += bau.get().getValue();
            }
        }
        double adjustmentMitigation = bauSum - totalMitigation;

        String[] totalLabels = new String[]{"Project", "Total Mitigation (ktCO2e)"};
        String[] projectNames = new String[]{
                "Wetland Parks", "Settlement Trees", "Street Trees", "Green Fences",
                "Crop Rotation", "Zero Tillage", "Protective Forest", 
                "Improved MMS (Total)", "  - Manure Covering", "  - Adding Straw", "  - Daily Spread",
                "TOTAL", "Total BAU", "Adjustment Mitigation"
        };
        double[] totalValues = new double[]{
                wetlandParksTotal, settlementTreesTotal, streetTreesTotal, greenFencesTotal,
                cropRotationTotal, zeroTillageTotal, protectiveForestTotal, 
                improvedMMSTotal, manureCoveringTotal, addingStrawTotal, dailySpreadTotal,
                totalMitigation, bauSum, adjustmentMitigation
        };

        for (int i = 0; i < totalLabels.length; i++) {
            Cell h = totalsHeader.createCell(i);
            h.setCellValue(totalLabels[i]);
            h.setCellStyle(summaryHeaderStyle);
        }

        for (int i = 0; i < projectNames.length; i++) {
            Row totalsRow = sheet.createRow(rowIdx++);
            Cell nameCell = totalsRow.createCell(0);
            nameCell.setCellValue(projectNames[i]);
            CellStyle nameStyle = sheet.getWorkbook().createCellStyle();
            nameStyle.setBorderTop(BorderStyle.THIN);
            nameStyle.setBorderBottom(BorderStyle.THIN);
            nameStyle.setBorderLeft(BorderStyle.THIN);
            nameStyle.setBorderRight(BorderStyle.THIN);
            nameStyle.setAlignment(HorizontalAlignment.LEFT);
            nameStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            if (i == projectNames.length - 3 || i == projectNames.length - 2 || i == projectNames.length - 1) { // TOTAL, Total BAU, or Adjustment Mitigation row
                Font font = sheet.getWorkbook().createFont();
                font.setBold(true);
                nameStyle.setFont(font);
                if (i == projectNames.length - 3) { // TOTAL row
                    nameStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
                } else if (i == projectNames.length - 2) { // Total BAU row
                    nameStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
                } else { // Adjustment Mitigation row
                    nameStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
                }
                nameStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            }
            nameCell.setCellStyle(nameStyle);

            Cell valueCell = totalsRow.createCell(1);
            valueCell.setCellValue(totalValues[i]);
            CellStyle valueCellStyle = sheet.getWorkbook().createCellStyle();
            valueCellStyle.cloneStyleFrom(numberStyle);
            if (i == projectNames.length - 3 || i == projectNames.length - 2 || i == projectNames.length - 1) { // TOTAL, Total BAU, or Adjustment Mitigation row
                Font font = sheet.getWorkbook().createFont();
                font.setBold(true);
                valueCellStyle.setFont(font);
                if (i == projectNames.length - 3) { // TOTAL row
                    valueCellStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
                } else if (i == projectNames.length - 2) { // Total BAU row
                    valueCellStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
                } else { // Adjustment Mitigation row
                    valueCellStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
                }
                valueCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            }
            valueCell.setCellStyle(valueCellStyle);
        }

        rowIdx++; // Blank row

        // Per Year Data Table
        Row perYearHeader = sheet.createRow(rowIdx++);
        perYearHeader.setHeightInPoints(20);
        String[] header = new String[]{
                "Year", "Wetland Parks", "Settlement Trees", "Street Trees", "Green Fences",
                "Crop Rotation", "Zero Tillage", "Protective Forest", "Improved MMS Total",
                "  - Manure Covering", "  - Adding Straw", "  - Daily Spread", 
                "Total Mitigation", "BAU", "Adjustment Mitigation"
        };
        for (int c = 0; c < header.length; c++) {
            Cell cell = perYearHeader.createCell(c);
            cell.setCellValue(header[c]);
            cell.setCellStyle(headerStyle);
        }

        int dataStartRow = rowIdx;
        for (int year = startYear; year <= endYear; year++) {
            Row r = sheet.createRow(rowIdx++);
            r.setHeightInPoints(18);
            boolean isAlternate = (year - startYear) % 2 == 1;

            double wp = wetlandParksByYear.getOrDefault(year, 0.0);
            double st = sumDouble(filterByYear(settlementTrees, SettlementTreesMitigation::getYear, year, year),
                    SettlementTreesMitigation::getMitigatedEmissionsKtCO2e);
            double stt = sumDouble(filterByYear(streetTrees, StreetTreesMitigation::getYear, year, year),
                    StreetTreesMitigation::getMitigatedEmissionsKtCO2e);
            double gf = sumDouble(filterByYear(greenFences, GreenFencesMitigation::getYear, year, year),
                    GreenFencesMitigation::getMitigatedEmissionsKtCO2e);
            double cr = sumDouble(filterByYear(cropRotation, CropRotationMitigation::getYear, year, year),
                    CropRotationMitigation::getMitigatedEmissionsKtCO2e);
            double zt = sumDouble(filterByYear(zeroTillage, ZeroTillageMitigation::getYear, year, year),
                    ZeroTillageMitigation::getGhgEmissionsSavings);
            double pf = protectiveForestByYear.getOrDefault(year, 0.0);
            double mc = sumDouble(filterByYear(manureCovering, ManureCoveringMitigation::getYear, year, year),
                    ManureCoveringMitigation::getMitigatedN2oEmissionsKilotonnes);
            double ast = sumDouble(filterByYear(addingStraw, AddingStrawMitigation::getYear, year, year),
                    AddingStrawMitigation::getMitigatedCh4EmissionsKilotonnes);
            double ds = sumDouble(filterByYear(dailySpread, DailySpreadMitigation::getYear, year, year),
                    DailySpreadMitigation::getMitigatedCh4EmissionsKilotonnes);
            double improvedMMS = mc + ast + ds;
            double total = wp + st + stt + gf + cr + zt + pf + mc + ast + ds;
            
            // Calculate BAU and Adjustment Mitigation for this year
            double yearBauValue = 0.0;
            double yearAdjustmentMitigation = 0.0;
            Optional<BAU> bau = bauRepository.findByYearAndSector(year, ESector.AFOLU);
            if (bau.isPresent() && bau.get().getValue() != null) {
                yearBauValue = bau.get().getValue();
                yearAdjustmentMitigation = yearBauValue - total;
            }

            Cell yearCell = r.createCell(0);
            yearCell.setCellValue(year);
            CellStyle baseYearStyle = isAlternate ? alternateDataStyle : dataStyle;
            CellStyle yearCellStyle = sheet.getWorkbook().createCellStyle();
            yearCellStyle.cloneStyleFrom(baseYearStyle);
            yearCellStyle.setAlignment(HorizontalAlignment.CENTER);
            yearCellStyle.setDataFormat(dataFormat.getFormat("0"));
            yearCell.setCellStyle(yearCellStyle);

            CellStyle numStyle = isAlternate ? createAlternateNumberStyle(sheet.getWorkbook()) : numberStyle;
            r.createCell(1).setCellValue(wp);
            r.getCell(1).setCellStyle(numStyle);
            r.createCell(2).setCellValue(st);
            r.getCell(2).setCellStyle(numStyle);
            r.createCell(3).setCellValue(stt);
            r.getCell(3).setCellStyle(numStyle);
            r.createCell(4).setCellValue(gf);
            r.getCell(4).setCellStyle(numStyle);
            r.createCell(5).setCellValue(cr);
            r.getCell(5).setCellStyle(numStyle);
            r.createCell(6).setCellValue(zt);
            r.getCell(6).setCellStyle(numStyle);
            r.createCell(7).setCellValue(pf);
            r.getCell(7).setCellStyle(numStyle);
            r.createCell(8).setCellValue(improvedMMS);
            r.getCell(8).setCellStyle(numStyle);
            r.createCell(9).setCellValue(mc);
            r.getCell(9).setCellStyle(numStyle);
            r.createCell(10).setCellValue(ast);
            r.getCell(10).setCellStyle(numStyle);
            r.createCell(11).setCellValue(ds);
            r.getCell(11).setCellStyle(numStyle);

            Cell totalCell = r.createCell(12);
            totalCell.setCellValue(total);
            CellStyle totalStyle = sheet.getWorkbook().createCellStyle();
            totalStyle.cloneStyleFrom(numStyle);
            Font totalFont = sheet.getWorkbook().createFont();
            totalFont.setBold(true);
            totalStyle.setFont(totalFont);
            totalCell.setCellStyle(totalStyle);
            
            Cell bauCell = r.createCell(13);
            bauCell.setCellValue(yearBauValue);
            bauCell.setCellStyle(numStyle);
            
            Cell adjustmentCell = r.createCell(14);
            adjustmentCell.setCellValue(yearAdjustmentMitigation);
            adjustmentCell.setCellStyle(numStyle);
        }

        // Auto-size columns
        for (int i = 0; i < header.length; i++) {
            sheet.autoSizeColumn(i);
            int currentWidth = sheet.getColumnWidth(i);
            int minWidth = 2500;
            int maxWidth = 18000;
            if (i == 0) {
                minWidth = 1500;
            }
            if (currentWidth < minWidth) {
                sheet.setColumnWidth(i, minWidth);
            } else if (currentWidth > maxWidth) {
                sheet.setColumnWidth(i, maxWidth);
            }
        }

        // Chart
        int dataEndRow = rowIdx - 1;
        if (dataEndRow >= dataStartRow) {
            XSSFDrawing drawing = sheet.createDrawingPatriarch();
            XSSFClientAnchor anchor = new XSSFClientAnchor();
            anchor.setCol1(0);
            anchor.setRow1(dataEndRow + 2);
            anchor.setCol2(12);
            anchor.setRow2(dataEndRow + 30);

            XSSFChart chart = drawing.createChart(anchor);
            chart.setTitleText("Mitigation by Project and Year");
            chart.setTitleOverlay(false);
            XDDFChartLegend legend = chart.getOrAddLegend();
            legend.setPosition(LegendPosition.BOTTOM);

            XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
            bottomAxis.setTitle("Year");
            XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
            leftAxis.setTitle("Mitigation (ktCO2e)");
            leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

            int pointCount = dataEndRow - dataStartRow + 1;
            String[] yearLabels = new String[pointCount];
            for (int i = 0; i < pointCount; i++) {
                yearLabels[i] = String.valueOf(startYear + i);
            }
            XDDFCategoryDataSource categories = XDDFDataSourcesFactory.fromArray(yearLabels, null);

            XDDFChartData barData = chart.createData(ChartTypes.BAR, bottomAxis, leftAxis);
            XDDFBarChartData bar = (XDDFBarChartData) barData;
            bar.setBarGrouping(BarGrouping.STACKED);
            bar.setBarDirection(BarDirection.COL);
            bar.setGapWidth(75);

            // Add series for each project (excluding Improved MMS sub-items, BAU, and adjustment)
            int[] seriesColumns = {1, 2, 3, 4, 5, 6, 7, 8, 12, 13}; // Skip 9,10,11 (Improved MMS sub-items) and 14 (adjustment), include 13 (BAU)
            for (int c : seriesColumns) {
                if (c < header.length) {
                    CellRangeAddress valuesRange = new CellRangeAddress(dataStartRow, dataEndRow, c, c);
                    XDDFNumericalDataSource<Double> values = XDDFDataSourcesFactory
                            .fromNumericCellRange(sheet, valuesRange);
                    XDDFBarChartData.Series series = (XDDFBarChartData.Series) bar.addSeries(categories, values);
                    series.setTitle(header[c], null);
                }
            }

            chart.plot(barData);
        }
    }

    // Individual project sheet builders - simplified versions
    private void buildWetlandParksSheet(XSSFSheet sheet, CellStyle headerStyle, CellStyle dataStyle,
                                       CellStyle alternateDataStyle, CellStyle numberStyle,
                                       List<WetlandParksMitigation> data) {
        String[] headers = {"Year", "Tree Category", "Mitigated Emissions (ktCO2e)"};
        createHeader(sheet, headerStyle, headers);
        DataFormat dataFormat = sheet.getWorkbook().createDataFormat();
        int rowIdx = 1;
        for (int i = 0; i < data.size(); i++) {
            WetlandParksMitigation item = data.get(i);
            Row r = sheet.createRow(rowIdx++);
            r.setHeightInPoints(18);
            boolean isAlternate = i % 2 == 1;
            CellStyle cellStyle = isAlternate ? alternateDataStyle : dataStyle;
            CellStyle numStyle = isAlternate ? createAlternateNumberStyle(sheet.getWorkbook()) : numberStyle;

            Cell yearCell = r.createCell(0);
            yearCell.setCellValue(item.getYear());
            CellStyle yearCellStyle = sheet.getWorkbook().createCellStyle();
            yearCellStyle.cloneStyleFrom(cellStyle);
            yearCellStyle.setAlignment(HorizontalAlignment.CENTER);
            yearCellStyle.setDataFormat(dataFormat.getFormat("0"));
            yearCell.setCellStyle(yearCellStyle);

            r.createCell(1).setCellValue(item.getTreeCategory() != null ? item.getTreeCategory().name() : "");
            r.getCell(1).setCellStyle(cellStyle);
            r.createCell(2).setCellValue(item.getMitigatedEmissionsKtCO2e() != null ? item.getMitigatedEmissionsKtCO2e() : 0.0);
            r.getCell(2).setCellStyle(numStyle);
        }
        autoSizeWithLimits(sheet, headers.length);
    }

    private void buildSettlementTreesSheet(XSSFSheet sheet, CellStyle headerStyle, CellStyle dataStyle,
                                          CellStyle alternateDataStyle, CellStyle numberStyle,
                                          List<SettlementTreesMitigation> data) {
        String[] headers = {"Year", "Mitigated Emissions (ktCO2e)"};
        createHeader(sheet, headerStyle, headers);
        DataFormat dataFormat = sheet.getWorkbook().createDataFormat();
        int rowIdx = 1;
        for (int i = 0; i < data.size(); i++) {
            SettlementTreesMitigation item = data.get(i);
            Row r = sheet.createRow(rowIdx++);
            r.setHeightInPoints(18);
            boolean isAlternate = i % 2 == 1;
            CellStyle cellStyle = isAlternate ? alternateDataStyle : dataStyle;
            CellStyle numStyle = isAlternate ? createAlternateNumberStyle(sheet.getWorkbook()) : numberStyle;

            Cell yearCell = r.createCell(0);
            yearCell.setCellValue(item.getYear());
            CellStyle yearCellStyle = sheet.getWorkbook().createCellStyle();
            yearCellStyle.cloneStyleFrom(cellStyle);
            yearCellStyle.setAlignment(HorizontalAlignment.CENTER);
            yearCellStyle.setDataFormat(dataFormat.getFormat("0"));
            yearCell.setCellStyle(yearCellStyle);

            r.createCell(1).setCellValue(item.getMitigatedEmissionsKtCO2e() != null ? item.getMitigatedEmissionsKtCO2e() : 0.0);
            r.getCell(1).setCellStyle(numStyle);
        }
        autoSizeWithLimits(sheet, headers.length);
    }

    private void buildStreetTreesSheet(XSSFSheet sheet, CellStyle headerStyle, CellStyle dataStyle,
                                      CellStyle alternateDataStyle, CellStyle numberStyle,
                                      List<StreetTreesMitigation> data) {
        String[] headers = {"Year", "Mitigated Emissions (ktCO2e)"};
        createHeader(sheet, headerStyle, headers);
        DataFormat dataFormat = sheet.getWorkbook().createDataFormat();
        int rowIdx = 1;
        for (int i = 0; i < data.size(); i++) {
            StreetTreesMitigation item = data.get(i);
            Row r = sheet.createRow(rowIdx++);
            r.setHeightInPoints(18);
            boolean isAlternate = i % 2 == 1;
            CellStyle cellStyle = isAlternate ? alternateDataStyle : dataStyle;
            CellStyle numStyle = isAlternate ? createAlternateNumberStyle(sheet.getWorkbook()) : numberStyle;

            Cell yearCell = r.createCell(0);
            yearCell.setCellValue(item.getYear());
            CellStyle yearCellStyle = sheet.getWorkbook().createCellStyle();
            yearCellStyle.cloneStyleFrom(cellStyle);
            yearCellStyle.setAlignment(HorizontalAlignment.CENTER);
            yearCellStyle.setDataFormat(dataFormat.getFormat("0"));
            yearCell.setCellStyle(yearCellStyle);

            r.createCell(1).setCellValue(item.getMitigatedEmissionsKtCO2e() != null ? item.getMitigatedEmissionsKtCO2e() : 0.0);
            r.getCell(1).setCellStyle(numStyle);
        }
        autoSizeWithLimits(sheet, headers.length);
    }

    private void buildGreenFencesSheet(XSSFSheet sheet, CellStyle headerStyle, CellStyle dataStyle,
                                      CellStyle alternateDataStyle, CellStyle numberStyle,
                                      List<GreenFencesMitigation> data) {
        String[] headers = {"Year", "Mitigated Emissions (ktCO2e)"};
        createHeader(sheet, headerStyle, headers);
        DataFormat dataFormat = sheet.getWorkbook().createDataFormat();
        int rowIdx = 1;
        for (int i = 0; i < data.size(); i++) {
            GreenFencesMitigation item = data.get(i);
            Row r = sheet.createRow(rowIdx++);
            r.setHeightInPoints(18);
            boolean isAlternate = i % 2 == 1;
            CellStyle cellStyle = isAlternate ? alternateDataStyle : dataStyle;
            CellStyle numStyle = isAlternate ? createAlternateNumberStyle(sheet.getWorkbook()) : numberStyle;

            Cell yearCell = r.createCell(0);
            yearCell.setCellValue(item.getYear());
            CellStyle yearCellStyle = sheet.getWorkbook().createCellStyle();
            yearCellStyle.cloneStyleFrom(cellStyle);
            yearCellStyle.setAlignment(HorizontalAlignment.CENTER);
            yearCellStyle.setDataFormat(dataFormat.getFormat("0"));
            yearCell.setCellStyle(yearCellStyle);

            r.createCell(1).setCellValue(item.getMitigatedEmissionsKtCO2e() != null ? item.getMitigatedEmissionsKtCO2e() : 0.0);
            r.getCell(1).setCellStyle(numStyle);
        }
        autoSizeWithLimits(sheet, headers.length);
    }

    private void buildCropRotationSheet(XSSFSheet sheet, CellStyle headerStyle, CellStyle dataStyle,
                                       CellStyle alternateDataStyle, CellStyle numberStyle,
                                       List<CropRotationMitigation> data) {
        String[] headers = {"Year", "Mitigated Emissions (ktCO2e)"};
        createHeader(sheet, headerStyle, headers);
        DataFormat dataFormat = sheet.getWorkbook().createDataFormat();
        int rowIdx = 1;
        for (int i = 0; i < data.size(); i++) {
            CropRotationMitigation item = data.get(i);
            Row r = sheet.createRow(rowIdx++);
            r.setHeightInPoints(18);
            boolean isAlternate = i % 2 == 1;
            CellStyle cellStyle = isAlternate ? alternateDataStyle : dataStyle;
            CellStyle numStyle = isAlternate ? createAlternateNumberStyle(sheet.getWorkbook()) : numberStyle;

            Cell yearCell = r.createCell(0);
            yearCell.setCellValue(item.getYear());
            CellStyle yearCellStyle = sheet.getWorkbook().createCellStyle();
            yearCellStyle.cloneStyleFrom(cellStyle);
            yearCellStyle.setAlignment(HorizontalAlignment.CENTER);
            yearCellStyle.setDataFormat(dataFormat.getFormat("0"));
            yearCell.setCellStyle(yearCellStyle);

            r.createCell(1).setCellValue(item.getMitigatedEmissionsKtCO2e() != null ? item.getMitigatedEmissionsKtCO2e() : 0.0);
            r.getCell(1).setCellStyle(numStyle);
        }
        autoSizeWithLimits(sheet, headers.length);
    }

    private void buildZeroTillageSheet(XSSFSheet sheet, CellStyle headerStyle, CellStyle dataStyle,
                                      CellStyle alternateDataStyle, CellStyle numberStyle,
                                      List<ZeroTillageMitigation> data) {
        String[] headers = {"Year", "GHG Emissions Savings (ktCO2e)"};
        createHeader(sheet, headerStyle, headers);
        DataFormat dataFormat = sheet.getWorkbook().createDataFormat();
        int rowIdx = 1;
        for (int i = 0; i < data.size(); i++) {
            ZeroTillageMitigation item = data.get(i);
            Row r = sheet.createRow(rowIdx++);
            r.setHeightInPoints(18);
            boolean isAlternate = i % 2 == 1;
            CellStyle cellStyle = isAlternate ? alternateDataStyle : dataStyle;
            CellStyle numStyle = isAlternate ? createAlternateNumberStyle(sheet.getWorkbook()) : numberStyle;

            Cell yearCell = r.createCell(0);
            yearCell.setCellValue(item.getYear());
            CellStyle yearCellStyle = sheet.getWorkbook().createCellStyle();
            yearCellStyle.cloneStyleFrom(cellStyle);
            yearCellStyle.setAlignment(HorizontalAlignment.CENTER);
            yearCellStyle.setDataFormat(dataFormat.getFormat("0"));
            yearCell.setCellStyle(yearCellStyle);

            r.createCell(1).setCellValue(item.getGhgEmissionsSavings() != null ? item.getGhgEmissionsSavings() : 0.0);
            r.getCell(1).setCellStyle(numStyle);
        }
        autoSizeWithLimits(sheet, headers.length);
    }

    private void buildProtectiveForestSheet(XSSFSheet sheet, CellStyle headerStyle, CellStyle dataStyle,
                                           CellStyle alternateDataStyle, CellStyle numberStyle,
                                           List<ProtectiveForestMitigation> data) {
        String[] headers = {"Year", "Category", "Mitigated Emissions (ktCO2e)"};
        createHeader(sheet, headerStyle, headers);
        DataFormat dataFormat = sheet.getWorkbook().createDataFormat();
        int rowIdx = 1;
        for (int i = 0; i < data.size(); i++) {
            ProtectiveForestMitigation item = data.get(i);
            Row r = sheet.createRow(rowIdx++);
            r.setHeightInPoints(18);
            boolean isAlternate = i % 2 == 1;
            CellStyle cellStyle = isAlternate ? alternateDataStyle : dataStyle;
            CellStyle numStyle = isAlternate ? createAlternateNumberStyle(sheet.getWorkbook()) : numberStyle;

            Cell yearCell = r.createCell(0);
            yearCell.setCellValue(item.getYear());
            CellStyle yearCellStyle = sheet.getWorkbook().createCellStyle();
            yearCellStyle.cloneStyleFrom(cellStyle);
            yearCellStyle.setAlignment(HorizontalAlignment.CENTER);
            yearCellStyle.setDataFormat(dataFormat.getFormat("0"));
            yearCell.setCellStyle(yearCellStyle);

            r.createCell(1).setCellValue(item.getCategory() != null ? item.getCategory().name() : "");
            r.getCell(1).setCellStyle(cellStyle);
            r.createCell(2).setCellValue(item.getMitigatedEmissionsKtCO2e() != null ? item.getMitigatedEmissionsKtCO2e() : 0.0);
            r.getCell(2).setCellStyle(numStyle);
        }
        autoSizeWithLimits(sheet, headers.length);
    }

    private void buildManureCoveringSheet(XSSFSheet sheet, CellStyle headerStyle, CellStyle dataStyle,
                                         CellStyle alternateDataStyle, CellStyle numberStyle,
                                         List<ManureCoveringMitigation> data) {
        String[] headers = {"Year", "Mitigated N2O Emissions (ktCO2e)"};
        createHeader(sheet, headerStyle, headers);
        DataFormat dataFormat = sheet.getWorkbook().createDataFormat();
        int rowIdx = 1;
        for (int i = 0; i < data.size(); i++) {
            ManureCoveringMitigation item = data.get(i);
            Row r = sheet.createRow(rowIdx++);
            r.setHeightInPoints(18);
            boolean isAlternate = i % 2 == 1;
            CellStyle cellStyle = isAlternate ? alternateDataStyle : dataStyle;
            CellStyle numStyle = isAlternate ? createAlternateNumberStyle(sheet.getWorkbook()) : numberStyle;

            Cell yearCell = r.createCell(0);
            yearCell.setCellValue(item.getYear());
            CellStyle yearCellStyle = sheet.getWorkbook().createCellStyle();
            yearCellStyle.cloneStyleFrom(cellStyle);
            yearCellStyle.setAlignment(HorizontalAlignment.CENTER);
            yearCellStyle.setDataFormat(dataFormat.getFormat("0"));
            yearCell.setCellStyle(yearCellStyle);

            r.createCell(1).setCellValue(item.getMitigatedN2oEmissionsKilotonnes() != null ? item.getMitigatedN2oEmissionsKilotonnes() : 0.0);
            r.getCell(1).setCellStyle(numStyle);
        }
        autoSizeWithLimits(sheet, headers.length);
    }

    private void buildAddingStrawSheet(XSSFSheet sheet, CellStyle headerStyle, CellStyle dataStyle,
                                      CellStyle alternateDataStyle, CellStyle numberStyle,
                                      List<AddingStrawMitigation> data) {
        String[] headers = {"Year", "Mitigated CH4 Emissions (ktCO2e)"};
        createHeader(sheet, headerStyle, headers);
        DataFormat dataFormat = sheet.getWorkbook().createDataFormat();
        int rowIdx = 1;
        for (int i = 0; i < data.size(); i++) {
            AddingStrawMitigation item = data.get(i);
            Row r = sheet.createRow(rowIdx++);
            r.setHeightInPoints(18);
            boolean isAlternate = i % 2 == 1;
            CellStyle cellStyle = isAlternate ? alternateDataStyle : dataStyle;
            CellStyle numStyle = isAlternate ? createAlternateNumberStyle(sheet.getWorkbook()) : numberStyle;

            Cell yearCell = r.createCell(0);
            yearCell.setCellValue(item.getYear());
            CellStyle yearCellStyle = sheet.getWorkbook().createCellStyle();
            yearCellStyle.cloneStyleFrom(cellStyle);
            yearCellStyle.setAlignment(HorizontalAlignment.CENTER);
            yearCellStyle.setDataFormat(dataFormat.getFormat("0"));
            yearCell.setCellStyle(yearCellStyle);

            r.createCell(1).setCellValue(item.getMitigatedCh4EmissionsKilotonnes() != null ? item.getMitigatedCh4EmissionsKilotonnes() : 0.0);
            r.getCell(1).setCellStyle(numStyle);
        }
        autoSizeWithLimits(sheet, headers.length);
    }

    private void buildDailySpreadSheet(XSSFSheet sheet, CellStyle headerStyle, CellStyle dataStyle,
                                      CellStyle alternateDataStyle, CellStyle numberStyle,
                                      List<DailySpreadMitigation> data) {
        String[] headers = {"Year", "Mitigated CH4 Emissions (ktCO2e)"};
        createHeader(sheet, headerStyle, headers);
        DataFormat dataFormat = sheet.getWorkbook().createDataFormat();
        int rowIdx = 1;
        for (int i = 0; i < data.size(); i++) {
            DailySpreadMitigation item = data.get(i);
            Row r = sheet.createRow(rowIdx++);
            r.setHeightInPoints(18);
            boolean isAlternate = i % 2 == 1;
            CellStyle cellStyle = isAlternate ? alternateDataStyle : dataStyle;
            CellStyle numStyle = isAlternate ? createAlternateNumberStyle(sheet.getWorkbook()) : numberStyle;

            Cell yearCell = r.createCell(0);
            yearCell.setCellValue(item.getYear());
            CellStyle yearCellStyle = sheet.getWorkbook().createCellStyle();
            yearCellStyle.cloneStyleFrom(cellStyle);
            yearCellStyle.setAlignment(HorizontalAlignment.CENTER);
            yearCellStyle.setDataFormat(dataFormat.getFormat("0"));
            yearCell.setCellStyle(yearCellStyle);

            r.createCell(1).setCellValue(item.getMitigatedCh4EmissionsKilotonnes() != null ? item.getMitigatedCh4EmissionsKilotonnes() : 0.0);
            r.getCell(1).setCellStyle(numStyle);
        }
        autoSizeWithLimits(sheet, headers.length);
    }

    private void createHeader(Sheet sheet, CellStyle headerStyle, String[] headers) {
        Row headerRow = sheet.createRow(0);
        headerRow.setHeightInPoints(22);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private void autoSizeWithLimits(Sheet sheet, int columns) {
        for (int i = 0; i < columns; i++) {
            sheet.autoSizeColumn(i);
            int currentWidth = sheet.getColumnWidth(i);
            int minWidth = 2500;
            int maxWidth = 20000;
            if (currentWidth < minWidth) {
                sheet.setColumnWidth(i, minWidth);
            } else if (currentWidth > maxWidth) {
                sheet.setColumnWidth(i, maxWidth);
            }
        }
    }
}

