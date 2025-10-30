package com.navyn.emissionlog.modules.activities;

import com.navyn.emissionlog.Enums.*;
import com.navyn.emissionlog.Enums.Fuel.FuelTypes;
import com.navyn.emissionlog.Enums.Transport.MobileActivityDataType;
import com.navyn.emissionlog.Enums.Transport.TransportModes;
import com.navyn.emissionlog.Enums.Transport.TransportType;
import com.navyn.emissionlog.modules.activities.models.*;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.*;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.DirectLandEmissions.SyntheticFertilizerEmissions;
import com.navyn.emissionlog.modules.agricultureEmissions.models.Livestock.EntericFermentationEmissions;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.DirectLandEmissions.AnimalManureAndCompostEmissions;
import com.navyn.emissionlog.modules.agricultureEmissions.repositories.AgriculturalLand.AquacultureEmissionsRepository;
import com.navyn.emissionlog.modules.agricultureEmissions.repositories.AgriculturalLand.DirectLandEmissions.AnimalManureAndCompostEmissionsRepository;
import com.navyn.emissionlog.modules.agricultureEmissions.repositories.AgriculturalLand.DirectLandEmissions.SyntheticFertilizerEmissionsRepository;
import com.navyn.emissionlog.modules.agricultureEmissions.repositories.AgriculturalLand.LimingEmissionsRepository;
import com.navyn.emissionlog.modules.agricultureEmissions.repositories.AgriculturalLand.RiceCultivationEmissionsRepository;
import com.navyn.emissionlog.modules.agricultureEmissions.repositories.AgriculturalLand.UreaEmissionsRepository;
import com.navyn.emissionlog.modules.agricultureEmissions.repositories.Livestock.EntericFermentationEmissionsRepository;
import com.navyn.emissionlog.modules.vehicles.VehicleRepository;
import com.navyn.emissionlog.modules.wasteEmissions.models.WasteDataAbstract;
import com.navyn.emissionlog.modules.LandUseEmissions.models.*;
import com.navyn.emissionlog.modules.LandUseEmissions.Repositories.*;
import com.navyn.emissionlog.modules.mitigationProjects.wetlandParks.models.WetlandParksMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.settlementTrees.models.SettlementTreesMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.streetTrees.models.StreetTreesMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.greenFences.models.GreenFencesMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.cropRotation.models.CropRotationMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.zeroTillage.models.ZeroTillageMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.protectiveForest.models.ProtectiveForestMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.improvedMMS.models.ImprovedMMSMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.wetlandParks.repositories.WetlandParksMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.settlementTrees.repositories.SettlementTreesMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.streetTrees.repositories.StreetTreesMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.greenFences.repositories.GreenFencesMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.cropRotation.repositories.CropRotationMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.zeroTillage.repositories.ZeroTillageMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.protectiveForest.repositories.ProtectiveForestMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.improvedMMS.repositories.ImprovedMMSMitigationRepository;
import com.navyn.emissionlog.modules.activities.dtos.*;
import com.navyn.emissionlog.modules.fuel.Fuel;
import com.navyn.emissionlog.modules.fuel.FuelData;
import com.navyn.emissionlog.modules.transportEmissions.models.TransportFuelEmissionFactors;
import com.navyn.emissionlog.utils.DashboardData;
import com.navyn.emissionlog.modules.stationaryEmissions.serviceImpls.StationaryEmissionCalculationServiceImpl;
import com.navyn.emissionlog.modules.transportEmissions.serviceImpls.TransportEmissionCalculationServiceImpl;
import com.navyn.emissionlog.modules.transportEmissions.services.TransportFuelEmissionFactorsService;
import com.navyn.emissionlog.modules.vehicles.Vehicle;
import com.navyn.emissionlog.utils.Specifications.ActivitySpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.navyn.emissionlog.modules.activities.repositories.ActivityRepository;
import com.navyn.emissionlog.modules.activities.repositories.ActivityDataRepository;
import com.navyn.emissionlog.modules.regions.RegionRepository;
import com.navyn.emissionlog.modules.fuel.repositories.FuelRepository;
import com.navyn.emissionlog.modules.wasteEmissions.WasteDataRepository;
import com.navyn.emissionlog.modules.fuel.repositories.FuelDataRepository;
import com.navyn.emissionlog.modules.vehicles.VehicleDataRepository;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.util.*;

import static com.navyn.emissionlog.utils.Specifications.ActivitySpecifications.*;
import static java.util.stream.Collectors.groupingBy;

@Service
@RequiredArgsConstructor
public class ActivityServiceImpl implements ActivityService {

    private final StationaryEmissionCalculationServiceImpl stationaryEmissionCalculationService;
    private final TransportEmissionCalculationServiceImpl transportEmissionCalculationService;
    private final TransportFuelEmissionFactorsService transportFuelEmissionFactorsService;
    private final FuelRepository fuelRepository;
    private final ActivityRepository activityRepository;
    private final ActivityDataRepository activityDataRepository;
    private final RegionRepository regionRepository;
    private final VehicleRepository vehicleRepository;
    private final WasteDataRepository wasteDataAbstractRepository;
    private final AquacultureEmissionsRepository aquacultureEmissionsRepository;
    private final EntericFermentationEmissionsRepository entericFermentationEmissionsRepository;
    private final LimingEmissionsRepository limingEmissionsRepository;
    private final RiceCultivationEmissionsRepository riceCultivationEmissionsRepository;
    private final SyntheticFertilizerEmissionsRepository syntheticFertilizerEmissionsRepository;
    private final UreaEmissionsRepository ureaEmissionsRepository;
    private final FuelDataRepository fuelDataRepository;
    private final VehicleDataRepository vehicleDataRepository;
    private final AnimalManureAndCompostEmissionsRepository animalManureAndCompostEmissionsRepository;
    
    // Land Use Emissions Repositories
    private final BiomassGainRepository biomassGainRepository;
    private final DisturbanceBiomassLossRepository disturbanceBiomassLossRepository;
    private final FirewoodRemovalBiomassLossRepository firewoodRemovalBiomassLossRepository;
    private final HarvestedBiomassLossRepository harvestedBiomassLossRepository;
    private final RewettedMineralWetlandsRepository rewettedMineralWetlandsRepository;
    
    // Mitigation Projects Repositories
    private final WetlandParksMitigationRepository wetlandParksMitigationRepository;
    private final SettlementTreesMitigationRepository settlementTreesMitigationRepository;
    private final StreetTreesMitigationRepository streetTreesMitigationRepository;
    private final GreenFencesMitigationRepository greenFencesMitigationRepository;
    private final CropRotationMitigationRepository cropRotationMitigationRepository;
    private final ZeroTillageMitigationRepository zeroTillageMitigationRepository;
    private final ProtectiveForestMitigationRepository protectiveForestMitigationRepository;
    private final ImprovedMMSMitigationRepository improvedMMSMitigationRepository;

    @Override
    public Activity createStationaryActivity(CreateStationaryActivityDto activity) {
        try {
            Optional<Fuel> fuel = fuelRepository.findById(activity.getFuel());

            if (fuel.isEmpty()) {
                throw new IllegalArgumentException("Fuel is not recorded");
            }

            //Create FuelData
            FuelData fuelData = createFuelData(activity, fuel.get());

            //Create ActivityData
            ActivityData stationaryActivityData = new StationaryActivityData();
            stationaryActivityData.setActivityType(ActivityTypes.STATIONARY);
            stationaryActivityData.setFuelData(fuelData);
            stationaryActivityData = activityDataRepository.save(stationaryActivityData);

            //Create Activity
            Activity activity1 = new Activity();
            activity1.setSector(activity.getSector());
            activity1.setScope(Scopes.SCOPE_1);
            activity1.setRegion(regionRepository.findById(activity.getRegion()).get());
            activity1.setActivityData(stationaryActivityData);
            activity1.setActivityYear(activity.getActivityYear());
            activity1.setActivityData(stationaryActivityData);

            //calculate emissions
            stationaryEmissionCalculationService.calculateEmissions(fuel.get(), activity1, fuelData, activity.getFuelUnit(), activity.getFuelAmount());

            return activityRepository.save(activity1);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating stationary activity");
        }
    }

    @Override
    public void deleteActivity(UUID id) {
        activityRepository.deleteById(id);
    }

    @Override
    public Activity getActivityById(UUID id) {
        return activityRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Activity not found"));
    }

    @Override
    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

    @Override
    public Activity createTransportActivityByFuel(CreateTransportActivityByFuelDto activityDto) {
        // Validate fuel exists
        Optional<Fuel> fuel = fuelRepository.findById(activityDto.getFuel());
        if(fuel.isEmpty()){
            throw new IllegalArgumentException("Fuel is not recorded");
        }

        // Create FuelData
        FuelData fuelData = createFuelData(activityDto, fuel.get());

        // Create ActivityData
        TransportActivityData transportActivityData = new TransportActivityData();
        transportActivityData.setActivityType(ActivityTypes.TRANSPORT);
        transportActivityData.setFuelData(fuelData);
        transportActivityData.setModeOfTransport(activityDto.getTransportMode());
        transportActivityData.setTransportType(activityDto.getTransportType());
        transportActivityData = activityDataRepository.save(transportActivityData);

        // Create Activity
        Activity activity = new Activity();
        activity.setSector(activityDto.getSector());
        activity.setScope(activityDto.getScope());
        activity.setRegion(regionRepository.findById(activityDto.getRegion()).get());
        activity.setActivityData(transportActivityData);
        activity.setActivityYear(activityDto.getActivityYear());

        //find the emissions factors with wildcard support for ANY values
        Optional<TransportFuelEmissionFactors> transportEmissionFactorsList = transportFuelEmissionFactorsService.findBestMatchWithWildcardSupport(fuel.get(), activityDto.getRegionGroup(), activityDto.getTransportType(), activityDto.getVehicleType());

        if(transportEmissionFactorsList.isEmpty()){
            throw new IllegalArgumentException("Transport Emission Factors not found for specified fuel, region, transport type, and vehicle type combination");
        }

        // Calculate emissions
        transportEmissionCalculationService.calculateEmissionsByFuel(transportEmissionFactorsList.get(), fuel.get(), activity, fuelData,
             activityDto.getFuelUnit(), activityDto.getFuelAmount());

        return activityRepository.save(activity);
    }

    @Override
    public Activity createTransportActivityByVehicleData(CreateTransportActivityByVehicleDataDto activityDto) {
        // Validate fuel exists
        Optional<Fuel> fuel = fuelRepository.findById(activityDto.getFuel());
        if(fuel.isEmpty()){
            throw new IllegalArgumentException("Fuel is not recorded");
        }

        Optional<Vehicle> vehicle = vehicleRepository.findById(activityDto.getVehicle());
        if(vehicle.isEmpty()){
            throw new IllegalArgumentException("Vehicle is not recorded");
        }

        // Create Vehicle Data
        VehicleData vehicleData = createTransportVehicleData(activityDto,vehicle.get());

        //Create Fuel Data
        FuelData fuelData = createFuelData(activityDto, fuel.get());

        // Create ActivityData
        TransportActivityData transportActivityData = new TransportActivityData();
        transportActivityData.setActivityType(ActivityTypes.TRANSPORT);
        transportActivityData.setFuelData(fuelData);
        transportActivityData.setModeOfTransport(activityDto.getTransportMode());
        transportActivityData.setTransportType(activityDto.getTransportType());
        transportActivityData.setVehicleData(vehicleData);
        transportActivityData = activityDataRepository.save(transportActivityData);

        // Create Activity
        Activity activity = new Activity();
        activity.setSector(activityDto.getSector());
        activity.setScope(activityDto.getScope());
        activity.setRegion(regionRepository.findById(activityDto.getRegion()).get());
        activity.setActivityData(transportActivityData);
        activity.setActivityYear(activityDto.getActivityYear());

        // Calculate emissions
        if(activityDto.getMobileActivityDataType() == MobileActivityDataType.VEHICLE_DISTANCE) {
            transportEmissionCalculationService.calculateEmissionsByVehicleData(activity, vehicleData, fuel.get(), activityDto.getRegionGroup(), activityDto.getMobileActivityDataType());
        }
        else{
            // Use flexible wildcard-aware matching to support ANY values
            Optional<TransportFuelEmissionFactors> transportEmissionFactorsList = transportFuelEmissionFactorsService.findBestMatchWithWildcardSupport(fuel.get(), activityDto.getRegionGroup(), activityDto.getTransportType(), activityDto.getVehicleType());

            if(transportEmissionFactorsList.isEmpty()){
                throw new IllegalArgumentException("Transport Emission Factors not found for specified fuel, region, transport type, and vehicle type combination");
            }
            transportEmissionCalculationService.calculateEmissionsByFuel(transportEmissionFactorsList.get(), fuel.get(), activity, fuelData,
                    activityDto.getFuelUnit(), activityDto.getFuelAmount());
            transportEmissionCalculationService.calculateEmissionsByVehicleData(activity, vehicleData, fuel.get(), activityDto.getRegionGroup(), activityDto.getMobileActivityDataType());
        }

        return activityRepository.save(activity);
    }

    @Override
    public List<Activity> getStationaryActivities(UUID region, Sectors sector, UUID fuel, FuelTypes fuelType, Integer year) {

        Specification<Activity> spec = Specification
                .where(isStationaryActivity())
                .and(hasSector(sector))
                .and(hasFuel(fuel))
                .and(hasFuelType(fuelType))
                .and(hasYear(year))
                .and(hasRegion(region));

        return activityRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "activityYear"));
    }

    @Override
    public List<Activity> getTransportActivities(TransportModes transportMode, UUID region, TransportType transportType, UUID fuel, FuelTypes fuelType, UUID vehicle, Scopes scope, Integer year) {

        Specification<Activity> spec = Specification
                .where(isTransportActivity())
                .and(hasTransportMode(transportMode))
                .and(hasTransportType(transportType))
                .and(hasYear(year))
                .and(ActivitySpecifications.hasRegion(region))
                .and(ActivitySpecifications.hasFuel(fuel))
                .and(ActivitySpecifications.hasFuelType(fuelType))
                .and(hasVehicle(vehicle))
                .and(hasScope(scope));

        return activityRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "activityYear"));
    }

    @Override
    public DashboardData getDashboardData() {
        try {
            List<Activity> activities = activityRepository.findAll();
            List<WasteDataAbstract> wasteActivities = wasteDataAbstractRepository.findAll();
            List<AquacultureEmissions> aquacultureEmissions = aquacultureEmissionsRepository.findAll();
            List<EntericFermentationEmissions> entericFermentationEmissions = entericFermentationEmissionsRepository.findAll();
            List<LimingEmissions> limingEmissions = limingEmissionsRepository.findAll();
            List<AnimalManureAndCompostEmissions> animalManureAndCompostEmissions = animalManureAndCompostEmissionsRepository.findAll();
            List<RiceCultivationEmissions> riceCultivationEmissions = riceCultivationEmissionsRepository.findAll();
            List<SyntheticFertilizerEmissions> syntheticFertilizerEmissions = syntheticFertilizerEmissionsRepository.findAll();
            List<UreaEmissions> ureaEmissions = ureaEmissionsRepository.findAll();
            
            // Land Use Emissions
            List<BiomassGain> biomassGains = biomassGainRepository.findAll();
            List<DisturbanceBiomassLoss> disturbanceLosses = disturbanceBiomassLossRepository.findAll();
            List<FirewoodRemovalBiomassLoss> firewoodLosses = firewoodRemovalBiomassLossRepository.findAll();
            List<HarvestedBiomassLoss> harvestedLosses = harvestedBiomassLossRepository.findAll();
            List<RewettedMineralWetlands> rewettedWetlands = rewettedMineralWetlandsRepository.findAll();
            
            // Mitigation Projects
            List<WetlandParksMitigation> wetlandParks = wetlandParksMitigationRepository.findAll();
            List<SettlementTreesMitigation> settlementTrees = settlementTreesMitigationRepository.findAll();
            List<StreetTreesMitigation> streetTrees = streetTreesMitigationRepository.findAll();
            List<GreenFencesMitigation> greenFences = greenFencesMitigationRepository.findAll();
            List<CropRotationMitigation> cropRotation = cropRotationMitigationRepository.findAll();
            List<ZeroTillageMitigation> zeroTillage = zeroTillageMitigationRepository.findAll();
            List<ProtectiveForestMitigation> protectiveForest = protectiveForestMitigationRepository.findAll();
            List<ImprovedMMSMitigation> improvedMMS = improvedMMSMitigationRepository.findAll();
            
            return calculateDashboardData(activities, wasteActivities,
                    aquacultureEmissions, entericFermentationEmissions, limingEmissions,
                    animalManureAndCompostEmissions, riceCultivationEmissions, syntheticFertilizerEmissions, ureaEmissions,
                    biomassGains, disturbanceLosses, firewoodLosses, harvestedLosses, rewettedWetlands,
                    wetlandParks, settlementTrees, streetTrees, greenFences, cropRotation, zeroTillage, protectiveForest, improvedMMS);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public DashboardData getDashboardData(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            List<Activity> activities = activityRepository.findAllByActivityYearBetweenOrderByActivityYearDesc(startDate, endDate);
            List<WasteDataAbstract> wasteActivities = wasteDataAbstractRepository.findByActivityYearBetweenOrderByYearDesc(startDate, endDate);
            List<AquacultureEmissions> aquacultureEmissions = aquacultureEmissionsRepository.findByYearRange(startDate.getYear(), endDate.getYear());
            List<EntericFermentationEmissions> entericFermentationEmissions = entericFermentationEmissionsRepository.findByYearRange(startDate.getYear(), endDate.getYear());
            List<LimingEmissions> limingEmissions = limingEmissionsRepository.findByYearRange(startDate.getYear(), endDate.getYear());
            List<AnimalManureAndCompostEmissions> animalManureAndCompostEmissions = animalManureAndCompostEmissionsRepository.findByYearRange(startDate.getYear(), endDate.getYear());
            List<RiceCultivationEmissions> riceCultivationEmissions = riceCultivationEmissionsRepository.findByYearRange(startDate.getYear(), endDate.getYear());
            List<SyntheticFertilizerEmissions> syntheticFertilizerEmissions = syntheticFertilizerEmissionsRepository.findByYearRange(startDate.getYear(), endDate.getYear());
            List<UreaEmissions> ureaEmissions = ureaEmissionsRepository.findByYearRange(startDate.getYear(), endDate.getYear());
            
            // Land Use Emissions - TODO: Add findByYearRange methods to repositories for optimization
            List<BiomassGain> biomassGains = biomassGainRepository.findAll();
            List<DisturbanceBiomassLoss> disturbanceLosses = disturbanceBiomassLossRepository.findAll();
            List<FirewoodRemovalBiomassLoss> firewoodLosses = firewoodRemovalBiomassLossRepository.findAll();
            List<HarvestedBiomassLoss> harvestedLosses = harvestedBiomassLossRepository.findAll();
            List<RewettedMineralWetlands> rewettedWetlands = rewettedMineralWetlandsRepository.findAll();
            
            // Mitigation Projects - TODO: Add findByYearRange methods to repositories for optimization
            List<WetlandParksMitigation> wetlandParks = wetlandParksMitigationRepository.findAll();
            List<SettlementTreesMitigation> settlementTrees = settlementTreesMitigationRepository.findAll();
            List<StreetTreesMitigation> streetTrees = streetTreesMitigationRepository.findAll();
            List<GreenFencesMitigation> greenFences = greenFencesMitigationRepository.findAll();
            List<CropRotationMitigation> cropRotation = cropRotationMitigationRepository.findAll();
            List<ZeroTillageMitigation> zeroTillage = zeroTillageMitigationRepository.findAll();
            List<ProtectiveForestMitigation> protectiveForest = protectiveForestMitigationRepository.findAll();
            List<ImprovedMMSMitigation> improvedMMS = improvedMMSMitigationRepository.findAll();
            
            DashboardData dashboardData = calculateDashboardData(activities, wasteActivities,
                    aquacultureEmissions, entericFermentationEmissions, limingEmissions,
                    animalManureAndCompostEmissions, riceCultivationEmissions, syntheticFertilizerEmissions, ureaEmissions,
                    biomassGains, disturbanceLosses, firewoodLosses, harvestedLosses, rewettedWetlands,
                    wetlandParks, settlementTrees, streetTrees, greenFences, cropRotation, zeroTillage, protectiveForest, improvedMMS);
            dashboardData.setStartingDate(startDate.toString());
            dashboardData.setEndingDate(endDate.toString());
            return dashboardData;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<DashboardData> getDashboardGraphDataByYear(Integer startingYear, Integer endingYear) {
        // Fetch all data sources
        List<Activity> activities = activityRepository.findAll();
        List<WasteDataAbstract> wasteActivities = wasteDataAbstractRepository.findAll();
        List<AquacultureEmissions> aquacultureEmissions = aquacultureEmissionsRepository.findAll();
        List<EntericFermentationEmissions> entericFermentationEmissions = entericFermentationEmissionsRepository.findAll();
        List<LimingEmissions> limingEmissions = limingEmissionsRepository.findAll();
        List<AnimalManureAndCompostEmissions> animalManureAndCompostEmissions = animalManureAndCompostEmissionsRepository.findAll();
        List<RiceCultivationEmissions> riceCultivationEmissions = riceCultivationEmissionsRepository.findAll();
        List<SyntheticFertilizerEmissions> syntheticFertilizerEmissions = syntheticFertilizerEmissionsRepository.findAll();
        List<UreaEmissions> ureaEmissions = ureaEmissionsRepository.findAll();
        List<BiomassGain> biomassGains = biomassGainRepository.findAll();
        List<DisturbanceBiomassLoss> disturbanceLosses = disturbanceBiomassLossRepository.findAll();
        List<FirewoodRemovalBiomassLoss> firewoodLosses = firewoodRemovalBiomassLossRepository.findAll();
        List<HarvestedBiomassLoss> harvestedLosses = harvestedBiomassLossRepository.findAll();
        List<RewettedMineralWetlands> rewettedWetlands = rewettedMineralWetlandsRepository.findAll();
        List<WetlandParksMitigation> wetlandParks = wetlandParksMitigationRepository.findAll();
        List<SettlementTreesMitigation> settlementTrees = settlementTreesMitigationRepository.findAll();
        List<StreetTreesMitigation> streetTrees = streetTreesMitigationRepository.findAll();
        List<GreenFencesMitigation> greenFences = greenFencesMitigationRepository.findAll();
        List<CropRotationMitigation> cropRotation = cropRotationMitigationRepository.findAll();
        List<ZeroTillageMitigation> zeroTillage = zeroTillageMitigationRepository.findAll();
        List<ProtectiveForestMitigation> protectiveForest = protectiveForestMitigationRepository.findAll();
        List<ImprovedMMSMitigation> improvedMMS = improvedMMSMitigationRepository.findAll();

        // Group all data by year
        Map<Integer, List<Activity>> groupedActivities = activities.stream()
                .filter(a -> a.getYear() >= startingYear && a.getYear() <= endingYear)
                .collect(groupingBy(Activity::getYear));
        Map<Integer, List<WasteDataAbstract>> groupedWaste = wasteActivities.stream()
                .filter(w -> w.getYear() >= startingYear && w.getYear() <= endingYear)
                .collect(groupingBy(WasteDataAbstract::getYear));
        Map<Integer, List<AquacultureEmissions>> groupedAquaculture = aquacultureEmissions.stream()
                .filter(a -> a.getYear() >= startingYear && a.getYear() <= endingYear)
                .collect(groupingBy(AquacultureEmissions::getYear));
        Map<Integer, List<EntericFermentationEmissions>> groupedEnteric = entericFermentationEmissions.stream()
                .filter(a -> a.getYear() >= startingYear && a.getYear() <= endingYear)
                .collect(groupingBy(EntericFermentationEmissions::getYear));
        Map<Integer, List<LimingEmissions>> groupedLiming = limingEmissions.stream()
                .filter(a -> a.getYear() >= startingYear && a.getYear() <= endingYear)
                .collect(groupingBy(LimingEmissions::getYear));
        Map<Integer, List<AnimalManureAndCompostEmissions>> groupedManure = animalManureAndCompostEmissions.stream()
                .filter(a -> a.getYear() >= startingYear && a.getYear() <= endingYear)
                .collect(groupingBy(AnimalManureAndCompostEmissions::getYear));
        Map<Integer, List<RiceCultivationEmissions>> groupedRice = riceCultivationEmissions.stream()
                .filter(a -> a.getYear() >= startingYear && a.getYear() <= endingYear)
                .collect(groupingBy(RiceCultivationEmissions::getYear));
        Map<Integer, List<SyntheticFertilizerEmissions>> groupedFertilizer = syntheticFertilizerEmissions.stream()
                .filter(a -> a.getYear() >= startingYear && a.getYear() <= endingYear)
                .collect(groupingBy(SyntheticFertilizerEmissions::getYear));
        Map<Integer, List<UreaEmissions>> groupedUrea = ureaEmissions.stream()
                .filter(a -> a.getYear() >= startingYear && a.getYear() <= endingYear)
                .collect(groupingBy(UreaEmissions::getYear));
        Map<Integer, List<BiomassGain>> groupedBiomassGains = biomassGains.stream()
                .filter(a -> a.getYear() >= startingYear && a.getYear() <= endingYear)
                .collect(groupingBy(BiomassGain::getYear));
        Map<Integer, List<DisturbanceBiomassLoss>> groupedDisturbance = disturbanceLosses.stream()
                .filter(a -> a.getYear() >= startingYear && a.getYear() <= endingYear)
                .collect(groupingBy(DisturbanceBiomassLoss::getYear));
        Map<Integer, List<FirewoodRemovalBiomassLoss>> groupedFirewood = firewoodLosses.stream()
                .filter(a -> a.getYear() >= startingYear && a.getYear() <= endingYear)
                .collect(groupingBy(FirewoodRemovalBiomassLoss::getYear));
        Map<Integer, List<HarvestedBiomassLoss>> groupedHarvested = harvestedLosses.stream()
                .filter(a -> a.getYear() >= startingYear && a.getYear() <= endingYear)
                .collect(groupingBy(HarvestedBiomassLoss::getYear));
        Map<Integer, List<RewettedMineralWetlands>> groupedWetlands = rewettedWetlands.stream()
                .filter(a -> a.getYear() >= startingYear && a.getYear() <= endingYear)
                .collect(groupingBy(RewettedMineralWetlands::getYear));
        Map<Integer, List<WetlandParksMitigation>> groupedWetlandParks = wetlandParks.stream()
                .filter(a -> a.getYear() >= startingYear && a.getYear() <= endingYear)
                .collect(groupingBy(WetlandParksMitigation::getYear));
        Map<Integer, List<SettlementTreesMitigation>> groupedSettlement = settlementTrees.stream()
                .filter(a -> a.getYear() >= startingYear && a.getYear() <= endingYear)
                .collect(groupingBy(SettlementTreesMitigation::getYear));
        Map<Integer, List<StreetTreesMitigation>> groupedStreet = streetTrees.stream()
                .filter(a -> a.getYear() >= startingYear && a.getYear() <= endingYear)
                .collect(groupingBy(StreetTreesMitigation::getYear));
        Map<Integer, List<GreenFencesMitigation>> groupedFences = greenFences.stream()
                .filter(a -> a.getYear() >= startingYear && a.getYear() <= endingYear)
                .collect(groupingBy(GreenFencesMitigation::getYear));
        Map<Integer, List<CropRotationMitigation>> groupedCrop = cropRotation.stream()
                .filter(a -> a.getYear() >= startingYear && a.getYear() <= endingYear)
                .collect(groupingBy(CropRotationMitigation::getYear));
        Map<Integer, List<ZeroTillageMitigation>> groupedTillage = zeroTillage.stream()
                .filter(a -> a.getYear() >= startingYear && a.getYear() <= endingYear)
                .collect(groupingBy(ZeroTillageMitigation::getYear));
        Map<Integer, List<ProtectiveForestMitigation>> groupedForest = protectiveForest.stream()
                .filter(a -> a.getYear() >= startingYear && a.getYear() <= endingYear)
                .collect(groupingBy(ProtectiveForestMitigation::getYear));
        Map<Integer, List<ImprovedMMSMitigation>> groupedMMS = improvedMMS.stream()
                .filter(a -> a.getYear() >= startingYear && a.getYear() <= endingYear)
                .collect(groupingBy(ImprovedMMSMitigation::getYear));

        // Create aggregated dashboard data for each year
        List<DashboardData> dashboardDataList = new ArrayList<>();
        for (int year = startingYear; year <= endingYear; year++) {

            DashboardData data = calculateDashboardData(
                    groupedActivities.getOrDefault(year, List.of()),
                    groupedWaste.getOrDefault(year, List.of()),
                    groupedAquaculture.getOrDefault(year, List.of()),
                    groupedEnteric.getOrDefault(year, List.of()),
                    groupedLiming.getOrDefault(year, List.of()),
                    groupedManure.getOrDefault(year, List.of()),
                    groupedRice.getOrDefault(year, List.of()),
                    groupedFertilizer.getOrDefault(year, List.of()),
                    groupedUrea.getOrDefault(year, List.of()),
                    groupedBiomassGains.getOrDefault(year, List.of()),
                    groupedDisturbance.getOrDefault(year, List.of()),
                    groupedFirewood.getOrDefault(year, List.of()),
                    groupedHarvested.getOrDefault(year, List.of()),
                    groupedWetlands.getOrDefault(year, List.of()),
                    groupedWetlandParks.getOrDefault(year, List.of()),
                    groupedSettlement.getOrDefault(year, List.of()),
                    groupedStreet.getOrDefault(year, List.of()),
                    groupedFences.getOrDefault(year, List.of()),
                    groupedCrop.getOrDefault(year, List.of()),
                    groupedTillage.getOrDefault(year, List.of()),
                    groupedForest.getOrDefault(year, List.of()),
                    groupedMMS.getOrDefault(year, List.of())
            );
            
            data.setStartingDate(LocalDateTime.of(year, 1, 1, 0, 0).toString());
            data.setEndingDate(LocalDateTime.of(year, 12, 31 , 23, 59).toString());
            data.setYear(Year.of(year));
            dashboardDataList.add(data);
        }

        return dashboardDataList;
    }

    @Override
    public List<DashboardData> getDashboardGraphDataByMonth(Integer year) {

        // Fetch all data sources for the year
        List<Activity> activities = activityRepository.findAll();
        List<WasteDataAbstract> wasteData = wasteDataAbstractRepository.findAll();
        List<AquacultureEmissions> aquacultureEmissions = aquacultureEmissionsRepository.findAll();
        List<EntericFermentationEmissions> entericFermentationEmissions = entericFermentationEmissionsRepository.findAll();
        List<LimingEmissions> limingEmissions = limingEmissionsRepository.findAll();
        List<AnimalManureAndCompostEmissions> animalManureAndCompostEmissions = animalManureAndCompostEmissionsRepository.findAll();
        List<RiceCultivationEmissions> riceCultivationEmissions = riceCultivationEmissionsRepository.findAll();
        List<SyntheticFertilizerEmissions> syntheticFertilizerEmissions = syntheticFertilizerEmissionsRepository.findAll();
        List<UreaEmissions> ureaEmissions = ureaEmissionsRepository.findAll();
        List<BiomassGain> biomassGains = biomassGainRepository.findAll();
        List<DisturbanceBiomassLoss> disturbanceLosses = disturbanceBiomassLossRepository.findAll();
        List<FirewoodRemovalBiomassLoss> firewoodLosses = firewoodRemovalBiomassLossRepository.findAll();
        List<HarvestedBiomassLoss> harvestedLosses = harvestedBiomassLossRepository.findAll();
        List<RewettedMineralWetlands> rewettedWetlands = rewettedMineralWetlandsRepository.findAll();
        List<WetlandParksMitigation> wetlandParks = wetlandParksMitigationRepository.findAll();
        List<SettlementTreesMitigation> settlementTrees = settlementTreesMitigationRepository.findAll();
        List<StreetTreesMitigation> streetTrees = streetTreesMitigationRepository.findAll();
        List<GreenFencesMitigation> greenFences = greenFencesMitigationRepository.findAll();
        List<CropRotationMitigation> cropRotation = cropRotationMitigationRepository.findAll();
        List<ZeroTillageMitigation> zeroTillage = zeroTillageMitigationRepository.findAll();
        List<ProtectiveForestMitigation> protectiveForest = protectiveForestMitigationRepository.findAll();
        List<ImprovedMMSMitigation> improvedMMS = improvedMMSMitigationRepository.findAll();

        // Group all data by year-month
        Map<YearMonth, List<Activity>> activitiesByMonth = activities.stream()
                .filter(a -> a.getYear() == year)
                .collect(groupingBy(activity -> YearMonth.from(activity.getActivityYear())));

        Map<YearMonth, List<WasteDataAbstract>> wasteByMonth = wasteData.stream()
                .filter(w -> w.getYear() == year)
                .collect(groupingBy(waste -> YearMonth.from(waste.getActivityYear())));

        // Agriculture data grouped by month (use year field)
        Map<Integer, List<AquacultureEmissions>> aquacultureByMonth = aquacultureEmissions.stream()
                .filter(a -> a.getYear() == year).collect(groupingBy(a -> 1)); // All in first month for yearly data
        Map<Integer, List<EntericFermentationEmissions>> entericByMonth = entericFermentationEmissions.stream()
                .filter(a -> a.getYear() == year).collect(groupingBy(a -> 1));
        Map<Integer, List<LimingEmissions>> limingByMonth = limingEmissions.stream()
                .filter(a -> a.getYear() == year).collect(groupingBy(a -> 1));
        Map<Integer, List<AnimalManureAndCompostEmissions>> manureByMonth = animalManureAndCompostEmissions.stream()
                .filter(a -> a.getYear() == year).collect(groupingBy(a -> 1));
        Map<Integer, List<RiceCultivationEmissions>> riceByMonth = riceCultivationEmissions.stream()
                .filter(a -> a.getYear() == year).collect(groupingBy(a -> 1));
        Map<Integer, List<SyntheticFertilizerEmissions>> fertilizerByMonth = syntheticFertilizerEmissions.stream()
                .filter(a -> a.getYear() == year).collect(groupingBy(a -> 1));
        Map<Integer, List<UreaEmissions>> ureaByMonth = ureaEmissions.stream()
                .filter(a -> a.getYear() == year).collect(groupingBy(a -> 1));
        
        // Land use and mitigation only have year field, so we'll include them in month 1 of each year
        Map<Integer, List<BiomassGain>> biomassGainsByMonth = biomassGains.stream()
                .filter(a -> a.getYear() == year).collect(groupingBy(a -> 1));
        Map<Integer, List<DisturbanceBiomassLoss>> disturbanceByMonth = disturbanceLosses.stream()
                .filter(a -> a.getYear() == year).collect(groupingBy(a -> 1));
        Map<Integer, List<FirewoodRemovalBiomassLoss>> firewoodByMonth = firewoodLosses.stream()
                .filter(a -> a.getYear() == year).collect(groupingBy(a -> 1));
        Map<Integer, List<HarvestedBiomassLoss>> harvestedByMonth = harvestedLosses.stream()
                .filter(a -> a.getYear() == year).collect(groupingBy(a -> 1));
        Map<Integer, List<RewettedMineralWetlands>> wetlandsByMonth = rewettedWetlands.stream()
                .filter(a -> a.getYear() == year).collect(groupingBy(a -> 1));
        Map<Integer, List<WetlandParksMitigation>> wetlandParksByMonth = wetlandParks.stream()
                .filter(a -> a.getYear() == year).collect(groupingBy(a -> 1));
        Map<Integer, List<SettlementTreesMitigation>> settlementByMonth = settlementTrees.stream()
                .filter(a -> a.getYear() == year).collect(groupingBy(a -> 1));
        Map<Integer, List<StreetTreesMitigation>> streetByMonth = streetTrees.stream()
                .filter(a -> a.getYear() == year).collect(groupingBy(a -> 1));
        Map<Integer, List<GreenFencesMitigation>> fencesByMonth = greenFences.stream()
                .filter(a -> a.getYear() == year).collect(groupingBy(a -> 1));
        Map<Integer, List<CropRotationMitigation>> cropByMonth = cropRotation.stream()
                .filter(a -> a.getYear() == year).collect(groupingBy(a -> 1));
        Map<Integer, List<ZeroTillageMitigation>> tillageByMonth = zeroTillage.stream()
                .filter(a -> a.getYear() == year).collect(groupingBy(a -> 1));
        Map<Integer, List<ProtectiveForestMitigation>> forestByMonth = protectiveForest.stream()
                .filter(a -> a.getYear() == year).collect(groupingBy(a -> 1));
        Map<Integer, List<ImprovedMMSMitigation>> mmsByMonth = improvedMMS.stream()
                .filter(a -> a.getYear() == year).collect(groupingBy(a -> 1));

        // Create aggregated dashboard data for each month
        List<DashboardData> dashboardDataList = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            YearMonth ym = YearMonth.of(year, month);

            DashboardData data = calculateDashboardData(
                    activitiesByMonth.getOrDefault(ym, List.of()),
                    wasteByMonth.getOrDefault(ym, List.of()),
                    month == 1 ? aquacultureByMonth.getOrDefault(1, List.of()) : List.of(),
                    month == 1 ? entericByMonth.getOrDefault(1, List.of()) : List.of(),
                    month == 1 ? limingByMonth.getOrDefault(1, List.of()) : List.of(),
                    month == 1 ? manureByMonth.getOrDefault(1, List.of()) : List.of(),
                    month == 1 ? riceByMonth.getOrDefault(1, List.of()) : List.of(),
                    month == 1 ? fertilizerByMonth.getOrDefault(1, List.of()) : List.of(),
                    month == 1 ? ureaByMonth.getOrDefault(1, List.of()) : List.of(),
                    month == 1 ? biomassGainsByMonth.getOrDefault(1, List.of()) : List.of(),
                    month == 1 ? disturbanceByMonth.getOrDefault(1, List.of()) : List.of(),
                    month == 1 ? firewoodByMonth.getOrDefault(1, List.of()) : List.of(),
                    month == 1 ? harvestedByMonth.getOrDefault(1, List.of()) : List.of(),
                    month == 1 ? wetlandsByMonth.getOrDefault(1, List.of()) : List.of(),
                    month == 1 ? wetlandParksByMonth.getOrDefault(1, List.of()) : List.of(),
                    month == 1 ? settlementByMonth.getOrDefault(1, List.of()) : List.of(),
                    month == 1 ? streetByMonth.getOrDefault(1, List.of()) : List.of(),
                    month == 1 ? fencesByMonth.getOrDefault(1, List.of()) : List.of(),
                    month == 1 ? cropByMonth.getOrDefault(1, List.of()) : List.of(),
                    month == 1 ? tillageByMonth.getOrDefault(1, List.of()) : List.of(),
                    month == 1 ? forestByMonth.getOrDefault(1, List.of()) : List.of(),
                    month == 1 ? mmsByMonth.getOrDefault(1, List.of()) : List.of()
            );
            
            data.setStartingDate(LocalDateTime.of(year, month, 1, 0, 0).toString());
            data.setEndingDate(LocalDateTime.of(year, month, ym.lengthOfMonth() , 23, 59).toString());
            data.setMonth(ym.getMonth());
            dashboardDataList.add(data);
        }

        return dashboardDataList;
    }


    private DashboardData generateDashboardGraphTime_DataPoint(List<Activity> activities, List<WasteDataAbstract> wasteData) {
        DashboardData activityDashboardData = calculateDashboardActivityData(activities);
        DashboardData wasteDashboardData  = calculateWasteDashboardData(wasteData);

        activityDashboardData.setTotalCH4Emissions(activityDashboardData.getTotalCH4Emissions() + wasteDashboardData.getTotalCH4Emissions());
        activityDashboardData.setTotalN2OEmissions(activityDashboardData.getTotalN2OEmissions() + wasteDashboardData.getTotalN2OEmissions());
        activityDashboardData.setTotalFossilCO2Emissions(activityDashboardData.getTotalFossilCO2Emissions() + wasteDashboardData.getTotalFossilCO2Emissions());
        activityDashboardData.setTotalBioCO2Emissions(activityDashboardData.getTotalBioCO2Emissions() + wasteDashboardData.getTotalBioCO2Emissions());
        activityDashboardData.setTotalCO2EqEmissions(activityDashboardData.getTotalCO2EqEmissions() + wasteDashboardData.getTotalCO2EqEmissions());
        return activityDashboardData;
    }

    private DashboardData calculateDashboardData(
            List<Activity> activities, 
            List<WasteDataAbstract> wasteData, 
            List<AquacultureEmissions> aquacultureEmissions, 
            List<EntericFermentationEmissions> entericFermentationEmissions, 
            List<LimingEmissions> limingEmissions, 
            List<AnimalManureAndCompostEmissions> animalManureAndCompostEmissions, 
            List<RiceCultivationEmissions> riceCultivationEmissions, 
            List<SyntheticFertilizerEmissions> syntheticFertilizerEmissions, 
            List<UreaEmissions> ureaEmissions,
            List<BiomassGain> biomassGains,
            List<DisturbanceBiomassLoss> disturbanceLosses,
            List<FirewoodRemovalBiomassLoss> firewoodLosses,
            List<HarvestedBiomassLoss> harvestedLosses,
            List<RewettedMineralWetlands> rewettedWetlands,
            List<WetlandParksMitigation> wetlandParks,
            List<SettlementTreesMitigation> settlementTrees,
            List<StreetTreesMitigation> streetTrees,
            List<GreenFencesMitigation> greenFences,
            List<CropRotationMitigation> cropRotation,
            List<ZeroTillageMitigation> zeroTillage,
            List<ProtectiveForestMitigation> protectiveForest,
            List<ImprovedMMSMitigation> improvedMMS) {
        
        DashboardData dashboardData = new DashboardData();
        
        // === ACTIVITIES ===
        for(Activity activity : activities){
            dashboardData.setTotalCH4Emissions(dashboardData.getTotalCH4Emissions() + activity.getCH4Emissions());
            dashboardData.setTotalN2OEmissions(dashboardData.getTotalN2OEmissions() + activity.getN2OEmissions());
            dashboardData.setTotalFossilCO2Emissions(dashboardData.getTotalFossilCO2Emissions() + activity.getFossilCO2Emissions());
            dashboardData.setTotalBioCO2Emissions(dashboardData.getTotalBioCO2Emissions() + activity.getBioCO2Emissions());
            dashboardData.setTotalCO2EqEmissions(dashboardData.getTotalCO2EqEmissions() + activity.getCO2EqEmissions());
        }
        
        // === WASTE ===
        for (WasteDataAbstract waste : wasteData) {
            dashboardData.setTotalCH4Emissions(dashboardData.getTotalCH4Emissions() + waste.getCH4Emissions());
            dashboardData.setTotalN2OEmissions(dashboardData.getTotalN2OEmissions() + waste.getN2OEmissions());
            dashboardData.setTotalFossilCO2Emissions(dashboardData.getTotalFossilCO2Emissions() + waste.getFossilCO2Emissions());
            dashboardData.setTotalBioCO2Emissions(dashboardData.getTotalBioCO2Emissions() + waste.getBioCO2Emissions());
        }
        
        // === AGRICULTURE EMISSIONS ===
        for (AquacultureEmissions aquacultureEmission : aquacultureEmissions) {
            dashboardData.setTotalN2OEmissions(dashboardData.getTotalN2OEmissions() + aquacultureEmission.getN2OEmissions());
        }
        for (EntericFermentationEmissions entericEmission : entericFermentationEmissions) {
            dashboardData.setTotalCH4Emissions(dashboardData.getTotalCH4Emissions() + entericEmission.getCH4Emissions());
        }
        for (LimingEmissions limingEmission : limingEmissions) {
            dashboardData.setTotalBioCO2Emissions(dashboardData.getTotalBioCO2Emissions() + limingEmission.getCO2Emissions());
        }
        for (AnimalManureAndCompostEmissions manureEmission : animalManureAndCompostEmissions) {
            dashboardData.setTotalCH4Emissions(dashboardData.getTotalCH4Emissions() + manureEmission.getCH4Emissions());
            dashboardData.setTotalN2OEmissions(dashboardData.getTotalN2OEmissions() + manureEmission.getN2OEmissions());
        }
        for (RiceCultivationEmissions riceEmission : riceCultivationEmissions) {
            dashboardData.setTotalCH4Emissions(dashboardData.getTotalCH4Emissions() + riceEmission.getAnnualCH4Emissions());
        }
        for (SyntheticFertilizerEmissions syntheticFertilizerEmission : syntheticFertilizerEmissions) {
            dashboardData.setTotalN2OEmissions(dashboardData.getTotalN2OEmissions() + syntheticFertilizerEmission.getN2OEmissions());
        }
        // BUG FIX: Was using getTotalBioCO2Emissions() instead of getTotalBioCO2Emissions() to accumulate
        for (UreaEmissions ureaEmission : ureaEmissions) {
            dashboardData.setTotalBioCO2Emissions(dashboardData.getTotalBioCO2Emissions() + ureaEmission.getCO2Emissions());
        }
        
        // === LAND USE EMISSIONS ===
        Double landUseTotal = 0.0;
        
        // BiomassGain (carbon removal - negative emissions)
        for (BiomassGain gain : biomassGains) {
            landUseTotal -= gain.getCO2EqOfBiomassCarbonGained(); // Subtract because it's removal
        }
        
        // Biomass Losses (positive emissions)
        for (DisturbanceBiomassLoss loss : disturbanceLosses) {
            landUseTotal += loss.getCO2EqOfBiomassCarbonLoss();
        }
        for (FirewoodRemovalBiomassLoss loss : firewoodLosses) {
            landUseTotal += loss.getCO2EqOfBiomassCarbonLoss();
        }
        for (HarvestedBiomassLoss loss : harvestedLosses) {
            landUseTotal += loss.getCO2EqOfBiomassCarbonLoss();
        }
        
        // Rewetted Wetlands (positive emissions)
        for (RewettedMineralWetlands wetland : rewettedWetlands) {
            landUseTotal += wetland.getCO2EqEmissions();
            dashboardData.setTotalCH4Emissions(dashboardData.getTotalCH4Emissions() + wetland.getCH4Emissions());
        }
        
        dashboardData.setTotalLandUseEmissions(landUseTotal);
        
        // === MITIGATION PROJECTS (Carbon Sequestration) ===
        Double totalMitigation = 0.0;
        
        for (WetlandParksMitigation mitigation : wetlandParks) {
            if (mitigation.getMitigatedEmissionsKtCO2e() != null) {
                totalMitigation += mitigation.getMitigatedEmissionsKtCO2e();
            }
        }
        for (SettlementTreesMitigation mitigation : settlementTrees) {
            if (mitigation.getMitigatedEmissionsKtCO2e() != null) {
                totalMitigation += mitigation.getMitigatedEmissionsKtCO2e();
            }
        }
        for (StreetTreesMitigation mitigation : streetTrees) {
            if (mitigation.getMitigatedEmissionsKtCO2e() != null) {
                totalMitigation += mitigation.getMitigatedEmissionsKtCO2e();
            }
        }
        for (GreenFencesMitigation mitigation : greenFences) {
            if (mitigation.getMitigatedEmissionsKtCO2e() != null) {
                totalMitigation += mitigation.getMitigatedEmissionsKtCO2e();
            }
        }
        for (CropRotationMitigation mitigation : cropRotation) {
            if (mitigation.getMitigatedEmissionsKtCO2e() != null) {
                totalMitigation += mitigation.getMitigatedEmissionsKtCO2e();
            }
        }
        for (ZeroTillageMitigation mitigation : zeroTillage) {
            if (mitigation.getGhgEmissionsSavings() != null) {
                totalMitigation += mitigation.getGhgEmissionsSavings();
            }
        }
        for (ProtectiveForestMitigation mitigation : protectiveForest) {
            if (mitigation.getMitigatedEmissionsKtCO2e() != null) {
                totalMitigation += mitigation.getMitigatedEmissionsKtCO2e();
            }
        }
        for (ImprovedMMSMitigation mitigation : improvedMMS) {
            if (mitigation.getTotalMitigation() != null) {
                totalMitigation += mitigation.getTotalMitigation();
            }
        }
        
        dashboardData.setTotalMitigationKtCO2e(totalMitigation);
        
        // === CALCULATE TOTALS ===
        dashboardData.setTotalCO2EqEmissions(
            dashboardData.getTotalFossilCO2Emissions() + 
            dashboardData.getTotalBioCO2Emissions() + 
            dashboardData.getTotalCH4Emissions() * GWP.CH4.getValue() + 
            dashboardData.getTotalN2OEmissions() * GWP.N2O.getValue() +
            dashboardData.getTotalLandUseEmissions()
        );
        
        // Calculate Net Emissions (Gross - Mitigation)
        // Convert totalCO2EqEmissions to Kt first (divide by 1000)
        Double grossEmissionsKt = dashboardData.getTotalCO2EqEmissions() / 1000.0;
        dashboardData.setNetEmissionsKtCO2e(grossEmissionsKt - totalMitigation);
        
        return dashboardData;
    }

    private DashboardData calculateDashboardActivityData(List<Activity> activities) {
        DashboardData dashboardData = new DashboardData();

        if(activities == null) {
            return dashboardData;
        }

        for(Activity activity : activities){
            dashboardData.setTotalCH4Emissions(dashboardData.getTotalCH4Emissions() + activity.getCH4Emissions());
            dashboardData.setTotalN2OEmissions(dashboardData.getTotalN2OEmissions() + activity.getN2OEmissions());
            dashboardData.setTotalFossilCO2Emissions(dashboardData.getTotalFossilCO2Emissions() + activity.getFossilCO2Emissions());
            dashboardData.setTotalBioCO2Emissions(dashboardData.getTotalBioCO2Emissions() + activity.getBioCO2Emissions());
        }
        dashboardData.setTotalCO2EqEmissions(dashboardData.getTotalFossilCO2Emissions() + dashboardData.getTotalBioCO2Emissions() + dashboardData.getTotalCH4Emissions()*GWP.CH4.getValue() + dashboardData.getTotalN2OEmissions()*GWP.N2O.getValue());
        return dashboardData;
    }

    private DashboardData calculateWasteDashboardData(List<WasteDataAbstract> wasteActivities){
        DashboardData dashboardData = new DashboardData();

        if(wasteActivities == null || wasteActivities.isEmpty()) {
            return dashboardData;
        }

        for(WasteDataAbstract waste : wasteActivities) {
            dashboardData.setTotalCH4Emissions(dashboardData.getTotalCH4Emissions() + waste.getCH4Emissions());
            dashboardData.setTotalN2OEmissions(dashboardData.getTotalN2OEmissions() + waste.getN2OEmissions());
            dashboardData.setTotalFossilCO2Emissions(dashboardData.getTotalFossilCO2Emissions() + waste.getFossilCO2Emissions());
            dashboardData.setTotalBioCO2Emissions(dashboardData.getTotalBioCO2Emissions() + waste.getBioCO2Emissions());
        }
        dashboardData.setTotalCO2EqEmissions(dashboardData.getTotalFossilCO2Emissions() + dashboardData.getTotalBioCO2Emissions() + dashboardData.getTotalCH4Emissions()*GWP.CH4.getValue() + dashboardData.getTotalN2OEmissions()*GWP.N2O.getValue());
        return dashboardData;
    }

    //create FuelData
    private FuelData createFuelData(CreateStationaryActivityDto dto, Fuel fuel) {
        FuelData fuelData = new FuelData();
        fuelData.setFuel(fuel);
        fuelData.setFuelState(dto.getFuelState());
        fuelData.setMetric(dto.getMetric());
        fuelData.setAmount_in_SI_Unit(0.0);
        return fuelDataRepository.save(fuelData);
    }

    //Create VehicleData
    private VehicleData createTransportVehicleData(CreateTransportActivityByVehicleDataDto dto, Vehicle vehicle) {
        VehicleData vehicleData = new VehicleData();
        vehicleData.setVehicle(vehicle);
        vehicleData.setDistanceTravelled_m(dto.getDistanceUnit().toMeters(dto.getDistanceTravelled()));
        vehicleData.setPassengers(dto.getPassengers());
        vehicleData.setFreightWeight_Kg(dto.getFreightWeightUnit().toKilograms(dto.getFreightWeight()));
        return vehicleDataRepository.save(vehicleData);
    }
}