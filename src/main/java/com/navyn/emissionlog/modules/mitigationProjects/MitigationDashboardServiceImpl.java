package com.navyn.emissionlog.modules.mitigationProjects;

import com.navyn.emissionlog.modules.mitigationProjects.dtos.MitigationDashboardSummaryDto;
import com.navyn.emissionlog.modules.mitigationProjects.dtos.MitigationDashboardYearDto;
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
import com.navyn.emissionlog.modules.mitigationProjects.energy.rooftop.model.RoofTopMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.energy.rooftop.repository.IRoofTopMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.models.StoveMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.repository.StoveMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.model.LightBulb;
import com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.repository.ILightBulbRepository;
import com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.models.AvoidedElectricityProduction;
import com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.repository.AvoidedElectricityProductionRepository;
import com.navyn.emissionlog.modules.mitigationProjects.IPPU.model.IPPUMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.IPPU.repository.IIPPURepository;
import com.navyn.emissionlog.modules.transportScenarios.modalShift.models.ModalShiftMitigation;
import com.navyn.emissionlog.modules.transportScenarios.modalShift.repository.ModalShiftMitigationRepository;
import com.navyn.emissionlog.modules.transportScenarios.ElectricVehicle.models.ElectricVehicleMitigation;
import com.navyn.emissionlog.modules.transportScenarios.ElectricVehicle.repository.ElectricVehicleMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.enums.ESector;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.models.BAU;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.repositories.BAURepository;
import lombok.RequiredArgsConstructor;
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
import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class MitigationDashboardServiceImpl implements MitigationDashboardService {

    private static final double GCO2E_TO_KCO2E = 1000.0; // Conversion factor: GgCO2e to ktCO2e
    private static final double TCO2E_TO_KCO2E = 1000.0; // Conversion factor: tCO2e to ktCO2e

    // AFOLU repositories
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

    // WASTE repositories
    private final WasteToEnergyMitigationRepository wasteToEnergyRepository;
    private final LandfillGasUtilizationMitigationRepository landfillGasUtilizationRepository;
    private final MBTCompostingMitigationRepository mbtCompostingRepository;
    private final EPRPlasticWasteMitigationRepository eprPlasticWasteRepository;
    private final KigaliFSTPMitigationRepository kigaliFSTPRepository;
    private final KigaliWWTPMitigationRepository kigaliWWTPRepository;
    private final ISWMMitigationRepository iswmRepository;

    // ENERGY repositories
    private final IRoofTopMitigationRepository rooftopRepository;
    private final StoveMitigationRepository cookstoveRepository;
    private final ILightBulbRepository lightbulbRepository;
    private final AvoidedElectricityProductionRepository waterheatRepository;

    // IPPU repository
    private final IIPPURepository iippuRepository;

    // TRANSPORT repositories
    private final ModalShiftMitigationRepository modalShiftMitigationRepository;
    private final ElectricVehicleMitigationRepository electricVehicleMitigationRepository;

    // BAU repository
    private final BAURepository bauRepository;

    /**
     * Helper method to filter by year range
     */
    private <T> List<T> filterByYear(List<T> list, Function<T, Integer> yearExtractor, int startYear, int endYear) {
        return list.stream()
                .filter(item -> {
                    Integer year = yearExtractor.apply(item);
                    return year != null && year >= startYear && year <= endYear;
                })
                .collect(Collectors.toList());
    }

    /**
     * Helper method to sum double values from a list
     */
    private <T> double sumDouble(List<T> list, Function<T, Double> valueExtractor) {
        return list.stream()
                .map(valueExtractor)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    /**
     * Convert tCO2e to ktCO2e
     */
    private double convertTCO2eToKtCO2e(double tCO2e) {
        return tCO2e / TCO2E_TO_KCO2E;
    }

    /**
     * Convert GgCO2e to ktCO2e
     */
    private double convertGCO2eToKtCO2e(double gCO2e) {
        return gCO2e * GCO2E_TO_KCO2E;
    }

    /**
     * Sum projectEmission for cookstove records in a year
     * projectEmission is already in ktCO2e, no conversion needed
     */
    private double getCookstoveTotalForYear(List<StoveMitigation> cookstoveData, int year) {
        return cookstoveData.stream()
                .filter(s -> s.getYear() == year)
                .mapToDouble(s -> s.getProjectEmission() != null ? s.getProjectEmission() : 0.0)
                .sum();
    }

    /**
     * Calculate AFOLU mitigation for a year
     */
    private double calculateAFOLUMitigationForYear(int year,
                                                   Map<Integer, List<WetlandParksMitigation>> wetlandParksByYear,
                                                   Map<Integer, List<SettlementTreesMitigation>> settlementTreesByYear,
                                                   Map<Integer, List<StreetTreesMitigation>> streetTreesByYear,
                                                   Map<Integer, List<GreenFencesMitigation>> greenFencesByYear,
                                                   Map<Integer, List<CropRotationMitigation>> cropRotationByYear,
                                                   Map<Integer, List<ZeroTillageMitigation>> zeroTillageByYear,
                                                   Map<Integer, List<ProtectiveForestMitigation>> protectiveForestByYear,
                                                   Map<Integer, List<ManureCoveringMitigation>> manureCoveringByYear,
                                                   Map<Integer, List<AddingStrawMitigation>> addingStrawByYear,
                                                   Map<Integer, List<DailySpreadMitigation>> dailySpreadByYear) {

        double wetlandParks = wetlandParksByYear.getOrDefault(year, List.of()).stream()
                .filter(w -> w.getMitigatedEmissionsKtCO2e() != null)
                .mapToDouble(WetlandParksMitigation::getMitigatedEmissionsKtCO2e)
                .sum();

        double settlementTrees = sumDouble(settlementTreesByYear.getOrDefault(year, List.of()),
                SettlementTreesMitigation::getMitigatedEmissionsKtCO2e);

        double streetTrees = sumDouble(streetTreesByYear.getOrDefault(year, List.of()),
                StreetTreesMitigation::getMitigatedEmissionsKtCO2e);

        double greenFences = sumDouble(greenFencesByYear.getOrDefault(year, List.of()),
                GreenFencesMitigation::getMitigatedEmissionsKtCO2e);

        double cropRotation = sumDouble(cropRotationByYear.getOrDefault(year, List.of()),
                CropRotationMitigation::getMitigatedEmissionsKtCO2e);

        double zeroTillage = sumDouble(zeroTillageByYear.getOrDefault(year, List.of()),
                ZeroTillageMitigation::getGhgEmissionsSavings);

        double protectiveForest = protectiveForestByYear.getOrDefault(year, List.of()).stream()
                .filter(p -> p.getMitigatedEmissionsKtCO2e() != null)
                .mapToDouble(ProtectiveForestMitigation::getMitigatedEmissionsKtCO2e)
                .sum();

        double manureCovering = sumDouble(manureCoveringByYear.getOrDefault(year, List.of()),
                ManureCoveringMitigation::getMitigatedN2oEmissionsKilotonnes);

        double addingStraw = sumDouble(addingStrawByYear.getOrDefault(year, List.of()),
                AddingStrawMitigation::getMitigatedCh4EmissionsKilotonnes);

        double dailySpread = sumDouble(dailySpreadByYear.getOrDefault(year, List.of()),
                DailySpreadMitigation::getMitigatedCh4EmissionsKilotonnes);

        return wetlandParks + settlementTrees + streetTrees + greenFences + cropRotation
                + zeroTillage + protectiveForest + manureCovering + addingStraw + dailySpread;
    }

    /**
     * Calculate WASTE mitigation for a year
     */
    private double calculateWasteMitigationForYear(int year,
                                                   Map<Integer, List<WasteToEnergyMitigation>> wasteToEnergyByYear,
                                                   Map<Integer, List<LandfillGasUtilizationMitigation>> landfillByYear,
                                                   Map<Integer, List<MBTCompostingMitigation>> mbtByYear,
                                                   Map<Integer, List<EPRPlasticWasteMitigation>> eprByYear,
                                                   Map<Integer, List<KigaliFSTPMitigation>> fstpByYear,
                                                   Map<Integer, List<KigaliWWTPMitigation>> wwtpByYear,
                                                   Map<Integer, List<ISWMMitigation>> iswmByYear) {

        double wasteToEnergy = sumDouble(wasteToEnergyByYear.getOrDefault(year, List.of()),
                WasteToEnergyMitigation::getGhgReductionKilotonnes);

        double landfill = sumDouble(landfillByYear.getOrDefault(year, List.of()),
                LandfillGasUtilizationMitigation::getProjectReductionEmissions);

        double mbt = sumDouble(mbtByYear.getOrDefault(year, List.of()),
                MBTCompostingMitigation::getEstimatedGhgReductionKilotonnesPerYear);

        double epr = sumDouble(eprByYear.getOrDefault(year, List.of()),
                EPRPlasticWasteMitigation::getGhgReductionKilotonnes);

        double fstp = sumDouble(fstpByYear.getOrDefault(year, List.of()),
                KigaliFSTPMitigation::getAnnualEmissionsReductionKilotonnes);

        double wwtp = sumDouble(wwtpByYear.getOrDefault(year, List.of()),
                KigaliWWTPMitigation::getAnnualEmissionsReductionKilotonnes);

        double iswm = sumDouble(iswmByYear.getOrDefault(year, List.of()),
                ISWMMitigation::getNetAnnualReduction);

        return wasteToEnergy + landfill + mbt + epr + fstp + wwtp + iswm;
    }

    /**
     * Calculate ENERGY mitigation for a year
     */
    private double calculateEnergyMitigationForYear(int year,
                                                    Map<Integer, List<StoveMitigation>> cookstoveByYear,
                                                    Map<Integer, List<RoofTopMitigation>> rooftopByYear,
                                                    Map<Integer, List<LightBulb>> lightbulbByYear,
                                                    Map<Integer, List<AvoidedElectricityProduction>> waterheatByYear) {

        double cookstoveTotal = cookstoveByYear.getOrDefault(year, List.of()).stream()
                .mapToDouble(s -> s.getProjectEmission() != null ? s.getProjectEmission() : 0.0)
                .sum();

        double rooftopTotal = sumDouble(rooftopByYear.getOrDefault(year, List.of()),
                RoofTopMitigation::getNetGhGMitigationAchieved);

        double lightbulbTotal = sumDouble(lightbulbByYear.getOrDefault(year, List.of()),
                LightBulb::getNetGhGMitigationAchieved);

        double waterheatTotal = sumDouble(waterheatByYear.getOrDefault(year, List.of()),
                w -> w.getNetGhGMitigation() != null ? w.getNetGhGMitigation() : 0.0);

        return cookstoveTotal + rooftopTotal + lightbulbTotal + waterheatTotal;
    }

    /**
     * Calculate IPPU mitigation for a year
     */
    private double calculateIPPUMitigationForYear(int year,
                                                  Map<Integer, List<IPPUMitigation>> ippuByYear) {
        return ippuByYear.getOrDefault(year, List.of()).stream()
                .mapToDouble(m -> m.getReducedEmissionInKtCO2e() != 0 ? m.getReducedEmissionInKtCO2e() : 0.0)
                .sum();
    }

    /**
     * Calculate TRANSPORT mitigation for a year
     */
    private double calculateTransportMitigationForYear(int year,
                                                       Map<Integer, List<ModalShiftMitigation>> modalShiftByYear,
                                                       Map<Integer, List<ElectricVehicleMitigation>> electricVehicleByYear) {

        double modalShift = sumDouble(modalShiftByYear.getOrDefault(year, List.of()),
                m -> m.getTotalProjectEmission() != null ? convertGCO2eToKtCO2e(m.getTotalProjectEmission()) : 0.0);

        double electricVehicle = sumDouble(electricVehicleByYear.getOrDefault(year, List.of()),
                e -> e.getTotalProjectEmission() != null ? convertGCO2eToKtCO2e(e.getTotalProjectEmission()) : 0.0);

        return modalShift + electricVehicle;
    }

    /**
     * Calculate TRANSPORT BAU for a year (from Modal Shift and Electric Vehicle entities)
     */
    private double calculateTransportBAUForYear(int year,
                                                Map<Integer, List<ModalShiftMitigation>> modalShiftByYear,
                                                Map<Integer, List<ElectricVehicleMitigation>> electricVehicleByYear) {
        double modalShiftBAU = sumDouble(modalShiftByYear.getOrDefault(year, List.of()),
                m -> m.getBauOfShift() != null ? convertGCO2eToKtCO2e(m.getBauOfShift()) : 0.0);
        
        double electricVehicleBAU = sumDouble(electricVehicleByYear.getOrDefault(year, List.of()),
                e -> e.getBau() != null ? convertGCO2eToKtCO2e(e.getBau()) : 0.0);
        
        return modalShiftBAU + electricVehicleBAU;
    }

    /**
     * Get BAU for a year (sum across all sectors including transport)
     */
    private double getBAUForYear(int year) {
        double bauSum = 0.0;
        // Sum BAU from BAU table (ENERGY, IPPU, WASTE, AFOLU sectors)
        for (ESector sector : ESector.values()) {
            Optional<BAU> bau = bauRepository.findByYearAndSector(year, sector);
            if (bau.isPresent() && bau.get().getValue() != null) {
                bauSum += bau.get().getValue();
            }
        }
        // Note: Transport BAU is stored in entities, not BAU table
        // It will be added separately when transport data is available
        return bauSum;
    }

    /**
     * Get total BAU for a year including transport
     */
    private double getTotalBAUForYear(int year,
                                      Map<Integer, List<ModalShiftMitigation>> modalShiftByYear,
                                      Map<Integer, List<ElectricVehicleMitigation>> electricVehicleByYear) {
        double bauFromTable = getBAUForYear(year);
        double transportBAU = calculateTransportBAUForYear(year, modalShiftByYear, electricVehicleByYear);
        return bauFromTable + transportBAU;
    }

    @Override
    @Transactional(readOnly = true)
    public MitigationDashboardSummaryDto getMitigationDashboardSummary(Integer startingYear, Integer endingYear) {
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
        List<LandfillGasUtilizationMitigation> landfill = landfillGasUtilizationRepository.findAll();
        List<MBTCompostingMitigation> mbt = mbtCompostingRepository.findAll();
        List<EPRPlasticWasteMitigation> epr = eprPlasticWasteRepository.findAll();
        List<KigaliFSTPMitigation> fstp = kigaliFSTPRepository.findAll();
        List<KigaliWWTPMitigation> wwtp = kigaliWWTPRepository.findAll();
        List<ISWMMitigation> iswm = iswmRepository.findAll();

        List<RoofTopMitigation> rooftop = rooftopRepository.findAll();
        List<StoveMitigation> cookstove = cookstoveRepository.findAll();
        List<LightBulb> lightbulb = lightbulbRepository.findAll();
        List<AvoidedElectricityProduction> waterheat = waterheatRepository.findAll();

        List<IPPUMitigation> ippu = iippuRepository.findAll();

        List<ModalShiftMitigation> modalShift = modalShiftMitigationRepository.findAll();
        List<ElectricVehicleMitigation> electricVehicle = electricVehicleMitigationRepository.findAll();

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

            wasteToEnergy = filterByYear(wasteToEnergy, WasteToEnergyMitigation::getYear, startingYear, endingYear);
            landfill = filterByYear(landfill, LandfillGasUtilizationMitigation::getYear, startingYear, endingYear);
            mbt = filterByYear(mbt, MBTCompostingMitigation::getYear, startingYear, endingYear);
            epr = filterByYear(epr, EPRPlasticWasteMitigation::getYear, startingYear, endingYear);
            fstp = filterByYear(fstp, KigaliFSTPMitigation::getYear, startingYear, endingYear);
            wwtp = filterByYear(wwtp, KigaliWWTPMitigation::getYear, startingYear, endingYear);
            iswm = filterByYear(iswm, ISWMMitigation::getYear, startingYear, endingYear);

            rooftop = filterByYear(rooftop, RoofTopMitigation::getYear, startingYear, endingYear);
            cookstove = filterByYear(cookstove, StoveMitigation::getYear, startingYear, endingYear);
            lightbulb = filterByYear(lightbulb, LightBulb::getYear, startingYear, endingYear);
            waterheat = filterByYear(waterheat, AvoidedElectricityProduction::getYear, startingYear, endingYear);

            ippu = filterByYear(ippu, IPPUMitigation::getYear, startingYear, endingYear);

            modalShift = filterByYear(modalShift, ModalShiftMitigation::getYear, startingYear, endingYear);
            electricVehicle = filterByYear(electricVehicle, ElectricVehicleMitigation::getYear, startingYear, endingYear);
        }

        // Aggregate AFOLU
        double wetlandParksTotal = wetlandParks.stream()
                .filter(w -> w.getMitigatedEmissionsKtCO2e() != null)
                .mapToDouble(WetlandParksMitigation::getMitigatedEmissionsKtCO2e)
                .sum();
        double settlementTreesTotal = sumDouble(settlementTrees, SettlementTreesMitigation::getMitigatedEmissionsKtCO2e);
        double streetTreesTotal = sumDouble(streetTrees, StreetTreesMitigation::getMitigatedEmissionsKtCO2e);
        double greenFencesTotal = sumDouble(greenFences, GreenFencesMitigation::getMitigatedEmissionsKtCO2e);
        double cropRotationTotal = sumDouble(cropRotation, CropRotationMitigation::getMitigatedEmissionsKtCO2e);
        double zeroTillageTotal = sumDouble(zeroTillage, ZeroTillageMitigation::getGhgEmissionsSavings);
        double protectiveForestTotal = protectiveForest.stream()
                .filter(p -> p.getMitigatedEmissionsKtCO2e() != null)
                .mapToDouble(ProtectiveForestMitigation::getMitigatedEmissionsKtCO2e)
                .sum();
        double manureCoveringTotal = sumDouble(manureCovering, ManureCoveringMitigation::getMitigatedN2oEmissionsKilotonnes);
        double addingStrawTotal = sumDouble(addingStraw, AddingStrawMitigation::getMitigatedCh4EmissionsKilotonnes);
        double dailySpreadTotal = sumDouble(dailySpread, DailySpreadMitigation::getMitigatedCh4EmissionsKilotonnes);
        double afoluTotal = wetlandParksTotal + settlementTreesTotal + streetTreesTotal + greenFencesTotal
                + cropRotationTotal + zeroTillageTotal + protectiveForestTotal + manureCoveringTotal
                + addingStrawTotal + dailySpreadTotal;

        // Aggregate WASTE
        double wasteToEnergyTotal = sumDouble(wasteToEnergy, WasteToEnergyMitigation::getGhgReductionKilotonnes);
        double landfillTotal = sumDouble(landfill, LandfillGasUtilizationMitigation::getProjectReductionEmissions);
        double mbtTotal = sumDouble(mbt, MBTCompostingMitigation::getEstimatedGhgReductionKilotonnesPerYear);
        double eprTotal = sumDouble(epr, EPRPlasticWasteMitigation::getGhgReductionKilotonnes);
        double fstpTotal = sumDouble(fstp, KigaliFSTPMitigation::getAnnualEmissionsReductionKilotonnes);
        double wwtpTotal = sumDouble(wwtp, KigaliWWTPMitigation::getAnnualEmissionsReductionKilotonnes);
        double iswmTotal = sumDouble(iswm, ISWMMitigation::getNetAnnualReduction);
        double wasteTotal = wasteToEnergyTotal + landfillTotal + mbtTotal + eprTotal + fstpTotal + wwtpTotal + iswmTotal;

        // Aggregate ENERGY (cookstove projectEmission already in ktCO2e, rooftop and lightbulb need conversion)
        double cookstoveTotal = cookstove.stream()
                .mapToDouble(s -> s.getProjectEmission() != null ? s.getProjectEmission() : 0.0)
                .sum();
        double rooftopTotal = rooftop.stream()
                .mapToDouble(r -> convertTCO2eToKtCO2e(r.getNetGhGMitigationAchieved()))
                .sum();
        double lightbulbTotal = lightbulb.stream()
                .mapToDouble(l -> convertTCO2eToKtCO2e(l.getNetGhGMitigationAchieved()))
                .sum();
        double waterheatTotal = waterheat.stream()
                .mapToDouble(w -> w.getNetGhGMitigation() != null ? convertTCO2eToKtCO2e(w.getNetGhGMitigation()) : 0.0)
                .sum();
        double energyTotal = cookstoveTotal + rooftopTotal + lightbulbTotal + waterheatTotal;

        // Aggregate IPPU
        double ippuTotal = ippu.stream()
                .mapToDouble(m -> m.getReducedEmissionInKtCO2e() != 0 ? m.getReducedEmissionInKtCO2e() : 0.0)
                .sum();

        // Aggregate TRANSPORT
        double modalShiftTotal = sumDouble(modalShift, m -> m.getTotalProjectEmission() != null ? convertGCO2eToKtCO2e(m.getTotalProjectEmission()) : 0.0);
        double electricVehicleTotal = sumDouble(electricVehicle, e -> e.getTotalProjectEmission() != null ? convertGCO2eToKtCO2e(e.getTotalProjectEmission()) : 0.0);
        double transportTotal = modalShiftTotal + electricVehicleTotal;

        // Calculate total mitigation
        double totalMitigation = afoluTotal + wasteTotal + energyTotal + ippuTotal + transportTotal;

        // Calculate total BAU (including transport BAU from entities)
        double totalBAU = 0.0;
        if (startingYear != null && endingYear != null) {
            // Group transport data by year for BAU calculation
            Map<Integer, List<ModalShiftMitigation>> modalShiftByYear = modalShift.stream()
                    .collect(Collectors.groupingBy(ModalShiftMitigation::getYear));
            Map<Integer, List<ElectricVehicleMitigation>> electricVehicleByYear = electricVehicle.stream()
                    .collect(Collectors.groupingBy(ElectricVehicleMitigation::getYear));
            
            for (int year = startingYear; year <= endingYear; year++) {
                totalBAU += getTotalBAUForYear(year, modalShiftByYear, electricVehicleByYear);
            }
        } else {
            // Get all BAU records from table
            List<BAU> allBAUs = bauRepository.findAll();
            double bauFromTable = allBAUs.stream()
                    .filter(b -> b.getValue() != null)
                    .mapToDouble(BAU::getValue)
                    .sum();
            
            // Add transport BAU from entities
            double transportBAU = sumDouble(modalShift, m -> m.getBauOfShift() != null ? convertGCO2eToKtCO2e(m.getBauOfShift()) : 0.0)
                    + sumDouble(electricVehicle, e -> e.getBau() != null ? convertGCO2eToKtCO2e(e.getBau()) : 0.0);
            
            totalBAU = bauFromTable + transportBAU;
        }

        // Calculate mitigation scenario
        double mitigationScenario = totalBAU - totalMitigation;

        MitigationDashboardSummaryDto dto = new MitigationDashboardSummaryDto();
        dto.setStartingYear(startingYear);
        dto.setEndingYear(endingYear);
        dto.setTotalBAUKtCO2e(totalBAU);
        dto.setTotalMitigationKtCO2e(totalMitigation);
        dto.setMitigationScenarioKtCO2e(mitigationScenario);
        dto.setAfoluMitigationKtCO2e(afoluTotal);
        dto.setWasteMitigationKtCO2e(wasteTotal);
        dto.setEnergyMitigationKtCO2e(energyTotal);
        dto.setIppuMitigationKtCO2e(ippuTotal);
        dto.setTransportMitigationKtCO2e(transportTotal);

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MitigationDashboardYearDto> getMitigationDashboardGraph(Integer startingYear, Integer endingYear) {
        int currentYear = LocalDateTime.now().getYear();
        int start = startingYear != null ? startingYear : currentYear - 4;
        int end = endingYear != null ? endingYear : currentYear;

        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
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
        List<LandfillGasUtilizationMitigation> landfill = landfillGasUtilizationRepository.findAll();
        List<MBTCompostingMitigation> mbt = mbtCompostingRepository.findAll();
        List<EPRPlasticWasteMitigation> epr = eprPlasticWasteRepository.findAll();
        List<KigaliFSTPMitigation> fstp = kigaliFSTPRepository.findAll();
        List<KigaliWWTPMitigation> wwtp = kigaliWWTPRepository.findAll();
        List<ISWMMitigation> iswm = iswmRepository.findAll();

        List<RoofTopMitigation> rooftop = rooftopRepository.findAll();
        List<StoveMitigation> cookstove = cookstoveRepository.findAll();
        List<LightBulb> lightbulb = lightbulbRepository.findAll();
        List<AvoidedElectricityProduction> waterheat = waterheatRepository.findAll();

        List<IPPUMitigation> ippu = iippuRepository.findAll();

        List<ModalShiftMitigation> modalShift = modalShiftMitigationRepository.findAll();
        List<ElectricVehicleMitigation> electricVehicle = electricVehicleMitigationRepository.findAll();

        // Group by year
        Map<Integer, List<WetlandParksMitigation>> wetlandParksByYear = wetlandParks.stream()
                .collect(Collectors.groupingBy(WetlandParksMitigation::getYear));
        Map<Integer, List<SettlementTreesMitigation>> settlementTreesByYear = settlementTrees.stream()
                .collect(Collectors.groupingBy(SettlementTreesMitigation::getYear));
        Map<Integer, List<StreetTreesMitigation>> streetTreesByYear = streetTrees.stream()
                .collect(Collectors.groupingBy(StreetTreesMitigation::getYear));
        Map<Integer, List<GreenFencesMitigation>> greenFencesByYear = greenFences.stream()
                .collect(Collectors.groupingBy(GreenFencesMitigation::getYear));
        Map<Integer, List<CropRotationMitigation>> cropRotationByYear = cropRotation.stream()
                .collect(Collectors.groupingBy(CropRotationMitigation::getYear));
        Map<Integer, List<ZeroTillageMitigation>> zeroTillageByYear = zeroTillage.stream()
                .collect(Collectors.groupingBy(ZeroTillageMitigation::getYear));
        Map<Integer, List<ProtectiveForestMitigation>> protectiveForestByYear = protectiveForest.stream()
                .collect(Collectors.groupingBy(ProtectiveForestMitigation::getYear));
        Map<Integer, List<ManureCoveringMitigation>> manureCoveringByYear = manureCovering.stream()
                .collect(Collectors.groupingBy(ManureCoveringMitigation::getYear));
        Map<Integer, List<AddingStrawMitigation>> addingStrawByYear = addingStraw.stream()
                .collect(Collectors.groupingBy(AddingStrawMitigation::getYear));
        Map<Integer, List<DailySpreadMitigation>> dailySpreadByYear = dailySpread.stream()
                .collect(Collectors.groupingBy(DailySpreadMitigation::getYear));

        Map<Integer, List<WasteToEnergyMitigation>> wasteToEnergyByYear = wasteToEnergy.stream()
                .collect(Collectors.groupingBy(WasteToEnergyMitigation::getYear));
        Map<Integer, List<LandfillGasUtilizationMitigation>> landfillByYear = landfill.stream()
                .collect(Collectors.groupingBy(LandfillGasUtilizationMitigation::getYear));
        Map<Integer, List<MBTCompostingMitigation>> mbtByYear = mbt.stream()
                .collect(Collectors.groupingBy(MBTCompostingMitigation::getYear));
        Map<Integer, List<EPRPlasticWasteMitigation>> eprByYear = epr.stream()
                .collect(Collectors.groupingBy(EPRPlasticWasteMitigation::getYear));
        Map<Integer, List<KigaliFSTPMitigation>> fstpByYear = fstp.stream()
                .collect(Collectors.groupingBy(KigaliFSTPMitigation::getYear));
        Map<Integer, List<KigaliWWTPMitigation>> wwtpByYear = wwtp.stream()
                .collect(Collectors.groupingBy(KigaliWWTPMitigation::getYear));
        Map<Integer, List<ISWMMitigation>> iswmByYear = iswm.stream()
                .collect(Collectors.groupingBy(ISWMMitigation::getYear));

        Map<Integer, List<StoveMitigation>> cookstoveByYear = cookstove.stream()
                .collect(Collectors.groupingBy(StoveMitigation::getYear));
        Map<Integer, List<RoofTopMitigation>> rooftopByYear = rooftop.stream()
                .collect(Collectors.groupingBy(RoofTopMitigation::getYear));
        Map<Integer, List<LightBulb>> lightbulbByYear = lightbulb.stream()
                .collect(Collectors.groupingBy(LightBulb::getYear));
        Map<Integer, List<AvoidedElectricityProduction>> waterheatByYear = waterheat.stream()
                .collect(Collectors.groupingBy(AvoidedElectricityProduction::getYear));

        Map<Integer, List<IPPUMitigation>> ippuByYear = ippu.stream()
                .collect(Collectors.groupingBy(IPPUMitigation::getYear));

        Map<Integer, List<ModalShiftMitigation>> modalShiftByYear = modalShift.stream()
                .collect(Collectors.groupingBy(ModalShiftMitigation::getYear));
        Map<Integer, List<ElectricVehicleMitigation>> electricVehicleByYear = electricVehicle.stream()
                .collect(Collectors.groupingBy(ElectricVehicleMitigation::getYear));

        // Create dashboard data for each year
        List<MitigationDashboardYearDto> dashboardDataList = new ArrayList<>();
        for (int year = start; year <= end; year++) {
            // Get BAU including transport BAU from entities
            double bauForYear = getTotalBAUForYear(year, modalShiftByYear, electricVehicleByYear);
            double afoluForYear = calculateAFOLUMitigationForYear(year, wetlandParksByYear, settlementTreesByYear,
                    streetTreesByYear, greenFencesByYear, cropRotationByYear, zeroTillageByYear,
                    protectiveForestByYear, manureCoveringByYear, addingStrawByYear, dailySpreadByYear);
            double wasteForYear = calculateWasteMitigationForYear(year, wasteToEnergyByYear, landfillByYear,
                    mbtByYear, eprByYear, fstpByYear, wwtpByYear, iswmByYear);
            double energyForYear = calculateEnergyMitigationForYear(year, cookstoveByYear, rooftopByYear,
                    lightbulbByYear, waterheatByYear);
            double ippuForYear = calculateIPPUMitigationForYear(year, ippuByYear);
            double transportForYear = calculateTransportMitigationForYear(year, modalShiftByYear, electricVehicleByYear);

            double totalMitigationForYear = afoluForYear + wasteForYear + energyForYear + ippuForYear + transportForYear;
            double mitigationScenarioForYear = bauForYear - totalMitigationForYear;

            MitigationDashboardYearDto dto = new MitigationDashboardYearDto();
            dto.setYear(year);
            dto.setBauScenarioKtCO2e(bauForYear);
            dto.setAfoluMitigationKtCO2e(afoluForYear);
            dto.setWasteMitigationKtCO2e(wasteForYear);
            dto.setEnergyMitigationKtCO2e(energyForYear);
            dto.setIppuMitigationKtCO2e(ippuForYear);
            dto.setTransportMitigationKtCO2e(transportForYear);
            dto.setTotalMitigationKtCO2e(totalMitigationForYear);
            dto.setMitigationScenarioKtCO2e(mitigationScenarioForYear);

            dashboardDataList.add(dto);
        }

        return dashboardDataList;
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportMitigationDashboard(Integer startingYear, Integer endingYear) {
        List<MitigationDashboardYearDto> graphData = getMitigationDashboardGraph(startingYear, endingYear);

        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            CreationHelper creationHelper = workbook.getCreationHelper();

            // Create styles
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle numberStyle = createNumberStyle(workbook);
            CellStyle bauStyle = createBAUStyle(workbook);
            CellStyle mitigationStyle = createMitigationStyle(workbook);
            CellStyle totalStyle = createTotalStyle(workbook);

            // Create data sheet
            XSSFSheet dataSheet = workbook.createSheet("Data");
            buildDataSheet(dataSheet, titleStyle, headerStyle, dataStyle, numberStyle,
                    bauStyle, mitigationStyle, totalStyle, graphData, creationHelper);

            // Create chart sheet
            XSSFSheet chartSheet = workbook.createSheet("Chart");
            buildChartSheet(chartSheet, titleStyle, dataSheet, graphData, creationHelper);

            workbook.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate mitigation dashboard export", e);
        }
    }

    private CellStyle createTitleStyle(Workbook workbook) {
        XSSFCellStyle style = (XSSFCellStyle) workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14); // Normal size
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        XSSFCellStyle style = (XSSFCellStyle) workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        font.setColor(IndexedColors.WHITE.getIndex());
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
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createNumberStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        DataFormat dataFormat = workbook.createDataFormat();
        style.setDataFormat(dataFormat.getFormat("#,##0.00"));
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }

    private CellStyle createBAUStyle(Workbook workbook) {
        XSSFCellStyle style = (XSSFCellStyle) createNumberStyle(workbook);
        style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle createMitigationStyle(Workbook workbook) {
        XSSFCellStyle style = (XSSFCellStyle) createNumberStyle(workbook);
        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle createTotalStyle(Workbook workbook) {
        XSSFCellStyle style = (XSSFCellStyle) createNumberStyle(workbook);
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.PINK.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private void buildDataSheet(XSSFSheet sheet, CellStyle titleStyle, CellStyle headerStyle,
                                CellStyle dataStyle, CellStyle numberStyle,
                                CellStyle bauStyle, CellStyle mitigationStyle, CellStyle totalStyle,
                                List<MitigationDashboardYearDto> data, CreationHelper creationHelper) {
        int rowIdx = 0;

        // Title
        Row titleRow = sheet.createRow(rowIdx++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("BAU Scenario vs Mitigated Emission Scenario");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 9));
        titleRow.setHeightInPoints(30);

        rowIdx++; // Blank row

        // Headers
        Row headerRow = sheet.createRow(rowIdx++);
        String[] headers = {
                "Year",
                "BAU Scenario (ktCO2eq)",
                "Mitigated Emissions by all Projects in Stationary Energy (ktCO2eq)",
                "Mitigated Emissions by all Projects in Transport Sector (ktCO2eq)",
                "Mitigated Emissions by all Projects in Waste Sector (ktCO2eq)",
                "Mitigated Emissions by all Projects in IPPU Sector (ktCO2eq)",
                "Mitigated Emissions by all Projects in AFOLU Sector (ktCO2eq)",
                "Total Mitigated Emissions (ktCO2eq)",
                "Mitigation Scenario (ktCO2eq)"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Data rows
        int dataStartRow = rowIdx;
        for (MitigationDashboardYearDto dto : data) {
            Row row = sheet.createRow(rowIdx++);

            // Year
            Cell yearCell = row.createCell(0);
            yearCell.setCellValue(dto.getYear());
            yearCell.setCellStyle(dataStyle);

            // BAU Scenario (green)
            Cell bauCell = row.createCell(1);
            bauCell.setCellValue(dto.getBauScenarioKtCO2e() != null ? dto.getBauScenarioKtCO2e() : 0.0);
            bauCell.setCellStyle(bauStyle);

            // Energy Mitigation (yellow)
            Cell energyCell = row.createCell(2);
            energyCell.setCellValue(dto.getEnergyMitigationKtCO2e() != null ? dto.getEnergyMitigationKtCO2e() : 0.0);
            energyCell.setCellStyle(mitigationStyle);

            // Transport Mitigation (yellow)
            Cell transportCell = row.createCell(3);
            transportCell.setCellValue(dto.getTransportMitigationKtCO2e() != null ? dto.getTransportMitigationKtCO2e() : 0.0);
            transportCell.setCellStyle(mitigationStyle);

            // Waste Mitigation (yellow)
            Cell wasteCell = row.createCell(4);
            wasteCell.setCellValue(dto.getWasteMitigationKtCO2e() != null ? dto.getWasteMitigationKtCO2e() : 0.0);
            wasteCell.setCellStyle(mitigationStyle);

            // IPPU Mitigation (yellow)
            Cell ippuCell = row.createCell(5);
            ippuCell.setCellValue(dto.getIppuMitigationKtCO2e() != null ? dto.getIppuMitigationKtCO2e() : 0.0);
            ippuCell.setCellStyle(mitigationStyle);

            // AFOLU Mitigation (yellow)
            Cell afoluCell = row.createCell(6);
            afoluCell.setCellValue(dto.getAfoluMitigationKtCO2e() != null ? dto.getAfoluMitigationKtCO2e() : 0.0);
            afoluCell.setCellStyle(mitigationStyle);

            // Total Mitigation (pink)
            Cell totalMitCell = row.createCell(7);
            totalMitCell.setCellValue(dto.getTotalMitigationKtCO2e() != null ? dto.getTotalMitigationKtCO2e() : 0.0);
            totalMitCell.setCellStyle(totalStyle);

            // Mitigation Scenario (pink)
            Cell scenarioCell = row.createCell(8);
            scenarioCell.setCellValue(dto.getMitigationScenarioKtCO2e() != null ? dto.getMitigationScenarioKtCO2e() : 0.0);
            scenarioCell.setCellStyle(totalStyle);
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
            if (sheet.getColumnWidth(i) > 15000) {
                sheet.setColumnWidth(i, 15000);
            }
        }
    }

    private void buildChartSheet(XSSFSheet chartSheet, CellStyle titleStyle, XSSFSheet dataSheet,
                                 List<MitigationDashboardYearDto> data, CreationHelper creationHelper) {
        int rowIdx = 0;

        // Set small column widths for compact chart
        chartSheet.setColumnWidth(0, 6000); // ~47 pixels - small and compact
        chartSheet.setColumnWidth(1, 6000);
        chartSheet.setColumnWidth(2, 6000);

        // Title
        Row titleRow = chartSheet.createRow(rowIdx++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("GHG emissions (ktCO2eq)");
        titleCell.setCellStyle(titleStyle);
        // Merge across 3 columns - enough to show full title
        chartSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));
        titleRow.setHeightInPoints(25); // Normal height

        // Add blank row for spacing
        chartSheet.createRow(rowIdx++).setHeightInPoints(5);

        // Create chart - start from row 2 (after title and blank row)
        int chartStartRow = rowIdx;
        XSSFDrawing drawing = chartSheet.createDrawingPatriarch();
        // Anchor: col1, row1, col2, row2, startCol, startRow, endCol, endRow
        // Make chart small and compact (2 columns wide, 15 rows tall) for tight year spacing
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 0, chartStartRow, 2, chartStartRow + 15);
        XSSFChart chart = drawing.createChart(anchor);
        chart.setTitleText("GHG emissions (ktCO2eq)");
        chart.setTitleOverlay(false);

        XDDFChartLegend legend = chart.getOrAddLegend();
        legend.setPosition(LegendPosition.BOTTOM);

        XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
        bottomAxis.setTitle("Year");
        XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
        leftAxis.setTitle("GHG emissions (ktCO2eq)");
        leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

        // Prepare data - data starts at row 2 (after title and blank row)
        int dataStartRow = 2;
        int dataEndRow = dataStartRow + data.size() - 1;

        // Year labels
        String[] yearLabels = data.stream()
                .map(dto -> String.valueOf(dto.getYear()))
                .toArray(String[]::new);
        XDDFCategoryDataSource categories = XDDFDataSourcesFactory.fromArray(yearLabels, null);

        // BAU Scenario series (column 1)
        CellRangeAddress bauRange = new CellRangeAddress(dataStartRow, dataEndRow, 1, 1);
        XDDFNumericalDataSource<Double> bauValues = XDDFDataSourcesFactory
                .fromNumericCellRange(dataSheet, bauRange);

        // Mitigation Scenario series (column 8)
        CellRangeAddress scenarioRange = new CellRangeAddress(dataStartRow, dataEndRow, 8, 8);
        XDDFNumericalDataSource<Double> scenarioValues = XDDFDataSourcesFactory
                .fromNumericCellRange(dataSheet, scenarioRange);

        // Create line chart
        XDDFChartData lineData = chart.createData(ChartTypes.LINE, bottomAxis, leftAxis);
        XDDFLineChartData line = (XDDFLineChartData) lineData;

        XDDFLineChartData.Series bauSeries = (XDDFLineChartData.Series) line.addSeries(categories, bauValues);
        bauSeries.setTitle("BAU Scenario (ktCO2eq)", null);

        XDDFLineChartData.Series scenarioSeries = (XDDFLineChartData.Series) line.addSeries(categories, scenarioValues);
        scenarioSeries.setTitle("Mitigation Scenario (ktCO2eq)", null);

        chart.plot(lineData);
    }
}

