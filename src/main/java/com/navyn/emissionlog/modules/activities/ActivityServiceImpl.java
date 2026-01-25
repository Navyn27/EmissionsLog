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
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.models.WetlandParksMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.settlementTrees.models.SettlementTreesMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.streetTrees.models.StreetTreesMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.greenFences.models.GreenFencesMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.models.CropRotationMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.zeroTillage.models.ZeroTillageMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.protectiveForest.models.ProtectiveForestMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.repositories.WetlandParksMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.settlementTrees.repositories.SettlementTreesMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.streetTrees.repositories.StreetTreesMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.greenFences.repositories.GreenFencesMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.repositories.CropRotationMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.zeroTillage.repositories.ZeroTillageMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.protectiveForest.repositories.ProtectiveForestMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.models.ManureCoveringMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.repository.ManureCoveringMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.models.AddingStrawMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.repository.AddingStrawMitigationRepository;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.dailySpread.models.DailySpreadMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.dailySpread.repository.DailySpreadMitigationRepository;
import com.navyn.emissionlog.modules.activities.dtos.*;
import com.navyn.emissionlog.modules.fuel.Fuel;
import com.navyn.emissionlog.modules.fuel.FuelData;
import com.navyn.emissionlog.modules.regions.Region;
import com.navyn.emissionlog.modules.transportEmissions.models.TransportFuelEmissionFactors;
import com.navyn.emissionlog.utils.DashboardData;
import com.navyn.emissionlog.modules.stationaryEmissions.serviceImpls.StationaryEmissionCalculationServiceImpl;
import com.navyn.emissionlog.modules.transportEmissions.serviceImpls.TransportEmissionCalculationServiceImpl;
import com.navyn.emissionlog.modules.transportEmissions.services.TransportFuelEmissionFactorsService;
import com.navyn.emissionlog.modules.vehicles.Vehicle;
import com.navyn.emissionlog.utils.Specifications.ActivitySpecifications;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.navyn.emissionlog.modules.activities.repositories.ActivityRepository;
import com.navyn.emissionlog.modules.activities.repositories.ActivityDataRepository;
import com.navyn.emissionlog.modules.regions.Region;
import com.navyn.emissionlog.modules.regions.RegionRepository;
import jakarta.transaction.Transactional;
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
    private final ManureCoveringMitigationRepository manureCoveringMitigationRepository;
    private final AddingStrawMitigationRepository addingStrawMitigationRepository;
    private final DailySpreadMitigationRepository dailySpreadMitigationRepository;

    @Override
    public Activity createStationaryActivity(CreateStationaryActivityDto activity) {
        try {
            Fuel fuel = fuelRepository.findById(activity.getFuel()).orElseThrow(() -> new EntityNotFoundException("Fuel with ID " + activity.getFuel() + " not found."));

            Region region = regionRepository.findById(activity.getRegion()).orElseThrow(() -> new EntityNotFoundException("Region with ID " + activity.getRegion() + " not found."));

            // Create FuelData
            FuelData fuelData = createFuelData(activity, fuel);

            // Create ActivityData
            ActivityData stationaryActivityData = new StationaryActivityData();
            stationaryActivityData.setActivityType(ActivityTypes.STATIONARY);
            stationaryActivityData.setFuelData(fuelData);
            stationaryActivityData = activityDataRepository.save(stationaryActivityData);

            // Create Activity
            Activity activity1 = new Activity();
            activity1.setSector(activity.getSector());
            activity1.setScope(Scopes.SCOPE_1);
            activity1.setRegion(region);
            activity1.setActivityData(stationaryActivityData);
            activity1.setActivityYear(activity.getActivityYear());

            // calculate emissions
            stationaryEmissionCalculationService.calculateEmissions(fuel, activity1, fuelData, activity.getFuelUnit(), activity.getFuelAmount());

            return activityRepository.save(activity1);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating stationary activity: " + e.getMessage(), e);
        }
    }

    @Override
    public Activity updateStationaryActivity(UUID id, UpdateStationaryActivityDto activityDto) {
        Activity activity = activityRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Activity with ID " + id + " not found"));

        if (activity.getActivityData() == null || activity.getActivityData().getActivityType() != ActivityTypes.STATIONARY) {
            throw new IllegalArgumentException("Activity with ID " + id + " is not a stationary activity.");
        }

        Fuel fuel = fuelRepository.findById(activityDto.getFuel()).orElseThrow(() -> new EntityNotFoundException("Fuel with ID " + activityDto.getFuel() + " not found."));

        Region region = regionRepository.findById(activityDto.getRegion()).orElseThrow(() -> new EntityNotFoundException("Region with ID " + activityDto.getRegion() + " not found."));

        FuelData fuelData = activity.getActivityData().getFuelData();
        if (fuelData == null) {
            throw new IllegalStateException("FuelData is missing for activity with ID " + id);
        }

        updateFuelData(fuelData, activityDto, fuel);

        activity.setSector(activityDto.getSector());
        activity.setActivityYear(activityDto.getActivityYear());
        activity.setRegion(region);

        stationaryEmissionCalculationService.calculateEmissions(fuel, activity, fuelData, activityDto.getFuelUnit(), activityDto.getFuelAmount());

        return activityRepository.save(activity);
    }

    @Override
    public void deleteStationaryActivity(UUID id) {
        // Find and validate the activity exists
        Activity activity = activityRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Activity with ID " + id + " not found"));

        // Validate it's a stationary activity
        if (activity.getActivityData() == null || activity.getActivityData().getActivityType() != ActivityTypes.STATIONARY) {
            throw new IllegalArgumentException("Activity with ID " + id + " is not a stationary activity.");
        }

        // Get related entities before detaching
        ActivityData activityData = activity.getActivityData();
        FuelData fuelData = activityData.getFuelData();

        // Detach relationships by setting them to null
        // First, detach FuelData from ActivityData
        if (fuelData != null) {
            activityData.setFuelData(null);
            activityDataRepository.save(activityData);
        }

        // Detach ActivityData from Activity
        activity.setActivityData(null);
        activityRepository.save(activity);

        // Now safely delete entities in correct order
        // Delete FuelData first (now detached from ActivityData)
        if (fuelData != null) {
            fuelDataRepository.delete(fuelData);
        }
        // Delete ActivityData (now detached from Activity)
        activityDataRepository.delete(activityData);
        // Finally, delete the Activity
        activityRepository.delete(activity);
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
    @Transactional
    public Activity createTransportActivityByFuel(CreateTransportActivityByFuelDto activityDto) {
        // Validate required fields
        if (activityDto.getFuel() == null) {
            throw new IllegalArgumentException("Fuel is required");
        }
        if (activityDto.getRegion() == null) {
            throw new IllegalArgumentException("Region is required");
        }
        if (activityDto.getRegionGroup() == null) {
            throw new IllegalArgumentException("Region group is required");
        }
        if (activityDto.getFuelAmount() == null || activityDto.getFuelAmount() <= 0) {
            throw new IllegalArgumentException("Fuel amount must be greater than zero");
        }
        if (activityDto.getFuelUnit() == null || activityDto.getFuelUnit().isEmpty()) {
            throw new IllegalArgumentException("Fuel unit is required");
        }

        // Validate fuel exists
        Optional<Fuel> fuel = fuelRepository.findById(activityDto.getFuel());
        if (fuel.isEmpty()) {
            throw new IllegalArgumentException("Fuel is not recorded");
        }

        // Validate region exists
        Optional<Region> region = regionRepository.findById(activityDto.getRegion());
        if (region.isEmpty()) {
            throw new IllegalArgumentException("Region is not recorded");
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
        activity.setRegion(region.get());
        activity.setActivityData(transportActivityData);
        activity.setActivityYear(activityDto.getActivityYear());

        // find the emissions factors with wildcard support for ANY values
        Optional<TransportFuelEmissionFactors> transportEmissionFactorsList = transportFuelEmissionFactorsService.findBestMatchWithWildcardSupport(fuel.get(), activityDto.getRegionGroup(), activityDto.getTransportType(), activityDto.getVehicleType());

        if (transportEmissionFactorsList.isEmpty()) {
            // Emission factors not found - set emissions to zero and continue
            setZeroEmissions(activity);
            System.out.println("WARNING: Transport Emission Factors not found for specified fuel, region, transport type, and vehicle type combination. Activity saved with zero emissions.");
        } else {
            // Calculate emissions BEFORE saving activity
            transportEmissionCalculationService.calculateEmissionsByFuel(transportEmissionFactorsList.get(), fuel.get(), activity, fuelData, activityDto.getFuelUnit(), activityDto.getFuelAmount());
        }

        return activityRepository.save(activity);
    }

    @Override
    @Transactional
    public Activity createTransportActivityByVehicleData(CreateTransportActivityByVehicleDataDto activityDto) {
        // Validate required fields
        if (activityDto.getFuel() == null) {
            throw new IllegalArgumentException("Fuel is required");
        }
        if (activityDto.getVehicle() == null) {
            throw new IllegalArgumentException("Vehicle is required");
        }
        if (activityDto.getRegion() == null) {
            throw new IllegalArgumentException("Region is required");
        }
        if (activityDto.getRegionGroup() == null) {
            throw new IllegalArgumentException("Region group is required");
        }
        if (activityDto.getMobileActivityDataType() == null) {
            throw new IllegalArgumentException("Mobile activity data type is required");
        }

        // Validate fuel exists
        Optional<Fuel> fuel = fuelRepository.findById(activityDto.getFuel());
        if (fuel.isEmpty()) {
            throw new IllegalArgumentException("Fuel is not recorded");
        }

        Optional<Vehicle> vehicle = vehicleRepository.findById(activityDto.getVehicle());
        if (vehicle.isEmpty()) {
            throw new IllegalArgumentException("Vehicle is not recorded");
        }

        // Validate region exists
        Optional<Region> region = regionRepository.findById(activityDto.getRegion());
        if (region.isEmpty()) {
            throw new IllegalArgumentException("Region is not recorded");
        }

        // Create Vehicle Data with validation
        VehicleData vehicleData = createTransportVehicleData(activityDto, vehicle.get());

        // Create Fuel Data
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
        activity.setRegion(region.get());
        activity.setActivityData(transportActivityData);
        activity.setActivityYear(activityDto.getActivityYear());

        // Calculate emissions BEFORE saving activity
        if (activityDto.getMobileActivityDataType() == MobileActivityDataType.VEHICLE_DISTANCE) {
            // Try to calculate vehicle emissions (will set to zero if factors not found)
            transportEmissionCalculationService.calculateEmissionsByVehicleData(activity, vehicleData, fuel.get(), activityDto.getRegionGroup(), activityDto.getMobileActivityDataType());
        } else {
            // Validate fuel-related fields for non-vehicle-distance calculations
            if (activityDto.getFuelAmount() == null || activityDto.getFuelAmount() <= 0) {
                throw new IllegalArgumentException("Fuel amount must be greater than zero");
            }
            if (activityDto.getFuelUnit() == null || activityDto.getFuelUnit().isEmpty()) {
                throw new IllegalArgumentException("Fuel unit is required");
            }

            // Use flexible wildcard-aware matching to support ANY values
            Optional<TransportFuelEmissionFactors> transportEmissionFactorsList = transportFuelEmissionFactorsService.findBestMatchWithWildcardSupport(fuel.get(), activityDto.getRegionGroup(), activityDto.getTransportType(), activityDto.getVehicleType());

            if (transportEmissionFactorsList.isEmpty()) {
                throw new IllegalArgumentException("Transport Emission Factors not found for specified fuel, region, transport type, and vehicle type combination");
            }
            transportEmissionCalculationService.calculateEmissionsByFuel(transportEmissionFactorsList.get(), fuel.get(), activity, fuelData, activityDto.getFuelUnit(), activityDto.getFuelAmount());
            transportEmissionCalculationService.calculateEmissionsByVehicleData(activity, vehicleData, fuel.get(), activityDto.getRegionGroup(), activityDto.getMobileActivityDataType());
        }

        return activityRepository.save(activity);
    }

    @Override
    public Activity updateTransportActivityByFuel(UUID id, UpdateTransportActivityByFuelDto activityDto) {
        // Find and validate the activity exists
        Activity activity = activityRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Activity with ID " + id + " not found"));

        // Validate it's a transport activity
        if (activity.getActivityData() == null || activity.getActivityData().getActivityType() != ActivityTypes.TRANSPORT) {
            throw new IllegalArgumentException("Activity with ID " + id + " is not a transport activity.");
        }

        // Validate it's a fuel-based transport activity (no vehicle data)
        TransportActivityData transportActivityData = (TransportActivityData) activity.getActivityData();
        if (transportActivityData.getVehicleData() != null) {
            throw new IllegalArgumentException("Activity with ID " + id + " is a vehicle data transport activity, not a fuel-based one.");
        }

        // Validate fuel exists
        Optional<Fuel> fuel = fuelRepository.findById(activityDto.getFuel());
        if (fuel.isEmpty()) {
            throw new EntityNotFoundException("Fuel with ID " + activityDto.getFuel() + " not found.");
        }

        // Validate region exists
        Optional<Region> region = regionRepository.findById(activityDto.getRegion());
        if (region.isEmpty()) {
            throw new EntityNotFoundException("Region with ID " + activityDto.getRegion() + " not found.");
        }

        // Get existing FuelData
        FuelData fuelData = transportActivityData.getFuelData();
        if (fuelData == null) {
            throw new IllegalStateException("FuelData is missing for activity with ID " + id);
        }

        // Update FuelData
        fuelData.setFuel(fuel.get());
        fuelData.setFuelState(activityDto.getFuelState());
        fuelData.setMetric(activityDto.getMetric());
        fuelDataRepository.save(fuelData);

        // Update TransportActivityData
        transportActivityData.setModeOfTransport(activityDto.getTransportMode());
        transportActivityData.setTransportType(activityDto.getTransportType());
        activityDataRepository.save(transportActivityData);

        // Update Activity
        activity.setSector(activityDto.getSector());
        activity.setScope(activityDto.getScope());
        activity.setActivityYear(activityDto.getActivityYear());
        activity.setRegion(region.get());

        // Find emission factors
        Optional<TransportFuelEmissionFactors> transportEmissionFactorsList = transportFuelEmissionFactorsService.findBestMatchWithWildcardSupport(fuel.get(), activityDto.getRegionGroup(), activityDto.getTransportType(), activityDto.getVehicleType());

        if (transportEmissionFactorsList.isEmpty()) {
            // Emission factors not found - set emissions to zero and continue
            setZeroEmissions(activity);
            System.out.println("WARNING: Transport Emission Factors not found for specified fuel, region, transport type, and vehicle type combination. Activity updated with zero emissions.");
        } else {
            // Recalculate emissions
            transportEmissionCalculationService.calculateEmissionsByFuel(transportEmissionFactorsList.get(), fuel.get(), activity, fuelData, activityDto.getFuelUnit(), activityDto.getFuelAmount());
        }

        return activityRepository.save(activity);
    }

    @Override
    public Activity updateTransportActivityByVehicleData(UUID id, UpdateTransportActivityByVehicleDataDto activityDto) {
        // Find and validate the activity exists
        Activity activity = activityRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Activity with ID " + id + " not found"));

        // Validate it's a transport activity
        if (activity.getActivityData() == null || activity.getActivityData().getActivityType() != ActivityTypes.TRANSPORT) {
            throw new IllegalArgumentException("Activity with ID " + id + " is not a transport activity.");
        }

        // Validate it's a vehicle data transport activity
        TransportActivityData transportActivityData = (TransportActivityData) activity.getActivityData();
        if (transportActivityData.getVehicleData() == null) {
            throw new IllegalArgumentException("Activity with ID " + id + " is a fuel-based transport activity, not a vehicle data one.");
        }

        // Validate fuel exists
        Optional<Fuel> fuel = fuelRepository.findById(activityDto.getFuel());
        if (fuel.isEmpty()) {
            throw new EntityNotFoundException("Fuel with ID " + activityDto.getFuel() + " not found.");
        }

        // Validate vehicle exists
        Optional<Vehicle> vehicle = vehicleRepository.findById(activityDto.getVehicle());
        if (vehicle.isEmpty()) {
            throw new EntityNotFoundException("Vehicle with ID " + activityDto.getVehicle() + " not found.");
        }

        // Validate region exists
        Optional<Region> region = regionRepository.findById(activityDto.getRegion());
        if (region.isEmpty()) {
            throw new EntityNotFoundException("Region with ID " + activityDto.getRegion() + " not found.");
        }

        // Get existing FuelData and VehicleData
        FuelData fuelData = transportActivityData.getFuelData();
        VehicleData vehicleData = transportActivityData.getVehicleData();
        if (fuelData == null) {
            throw new IllegalStateException("FuelData is missing for activity with ID " + id);
        }
        if (vehicleData == null) {
            throw new IllegalStateException("VehicleData is missing for activity with ID " + id);
        }

        // Update FuelData
        fuelData.setFuel(fuel.get());
        fuelData.setFuelState(activityDto.getFuelState());
        fuelData.setMetric(activityDto.getMetric());
        fuelDataRepository.save(fuelData);

        // Update VehicleData
        vehicleData.setVehicle(vehicle.get());
        if (activityDto.getDistanceUnit() != null && activityDto.getDistanceTravelled() != null) {
            vehicleData.setDistanceTravelled_m(activityDto.getDistanceUnit().toMeters(activityDto.getDistanceTravelled()));
        }
        if (activityDto.getPassengers() != null) {
            vehicleData.setPassengers(activityDto.getPassengers());
        }
        if (activityDto.getFreightWeightUnit() != null && activityDto.getFreightWeight() != null) {
            vehicleData.setFreightWeight_Kg(activityDto.getFreightWeightUnit().toKilograms(activityDto.getFreightWeight()));
        }
        vehicleDataRepository.save(vehicleData);

        // Update TransportActivityData
        transportActivityData.setModeOfTransport(activityDto.getTransportMode());
        transportActivityData.setTransportType(activityDto.getTransportType());
        activityDataRepository.save(transportActivityData);

        // Update Activity
        activity.setSector(activityDto.getSector());
        activity.setScope(activityDto.getScope());
        activity.setActivityYear(activityDto.getActivityYear());
        activity.setRegion(region.get());

        // Recalculate emissions based on mobile activity data type
        if (activityDto.getMobileActivityDataType() == MobileActivityDataType.VEHICLE_DISTANCE) {
            // Try to calculate vehicle emissions (will set to zero if factors not found)
            transportEmissionCalculationService.calculateEmissionsByVehicleData(activity, vehicleData, fuel.get(), activityDto.getRegionGroup(), activityDto.getMobileActivityDataType());
        } else {
            // Use flexible wildcard-aware matching to support ANY values
            Optional<TransportFuelEmissionFactors> transportEmissionFactorsList = transportFuelEmissionFactorsService.findBestMatchWithWildcardSupport(fuel.get(), activityDto.getRegionGroup(), activityDto.getTransportType(), activityDto.getVehicleType());

            if (transportEmissionFactorsList.isEmpty()) {
                // Emission factors not found - set emissions to zero and continue
                setZeroEmissions(activity);
                System.out.println("WARNING: Transport Emission Factors not found for specified fuel, region, transport type, and vehicle type combination. Activity updated with zero emissions.");
            } else {
                transportEmissionCalculationService.calculateEmissionsByFuel(transportEmissionFactorsList.get(), fuel.get(), activity, fuelData, activityDto.getFuelUnit(), activityDto.getFuelAmount());
            }
            // Try to calculate vehicle emissions (will set to zero if factors not found)
            transportEmissionCalculationService.calculateEmissionsByVehicleData(activity, vehicleData, fuel.get(), activityDto.getRegionGroup(), activityDto.getMobileActivityDataType());
        }

        return activityRepository.save(activity);
    }

    @Override
    public void deleteTransportActivity(UUID id) {
        // Find and validate the activity exists
        Activity activity = activityRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Activity with ID " + id + " not found"));

        // Validate it's a transport activity
        if (activity.getActivityData() == null || activity.getActivityData().getActivityType() != ActivityTypes.TRANSPORT) {
            throw new IllegalArgumentException("Activity with ID " + id + " is not a transport activity.");
        }

        // Get related entities before detaching
        TransportActivityData transportActivityData = (TransportActivityData) activity.getActivityData();
        FuelData fuelData = transportActivityData.getFuelData();
        VehicleData vehicleData = transportActivityData.getVehicleData();

        // Detach relationships by setting them to null
        // First, detach FuelData from TransportActivityData
        if (fuelData != null) {
            transportActivityData.setFuelData(null);
            activityDataRepository.save(transportActivityData);
        }

        // Detach VehicleData from TransportActivityData (if exists)
        if (vehicleData != null) {
            transportActivityData.setVehicleData(null);
            activityDataRepository.save(transportActivityData);
        }

        // Detach TransportActivityData from Activity
        activity.setActivityData(null);
        activityRepository.save(activity);

        // Now safely delete entities in correct order
        // Delete VehicleData first (if exists, now detached from TransportActivityData)
        if (vehicleData != null) {
            vehicleDataRepository.delete(vehicleData);
        }
        // Delete FuelData (now detached from TransportActivityData)
        if (fuelData != null) {
            fuelDataRepository.delete(fuelData);
        }
        // Delete TransportActivityData (now detached from Activity)
        activityDataRepository.delete(transportActivityData);
        // Finally, delete the Activity
        activityRepository.delete(activity);
    }

    @Override
    public List<Activity> getStationaryActivities(UUID region, Sectors sector, UUID fuel, FuelTypes fuelType, Integer year) {

        Specification<Activity> spec = Specification.where(isStationaryActivity()).and(hasSector(sector)).and(hasFuel(fuel)).and(hasFuelType(fuelType)).and(hasYear(year)).and(hasRegion(region));

        return activityRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "activityYear"));
    }

    @Override
    public List<Activity> getTransportActivities(TransportModes transportMode, UUID region, TransportType transportType, UUID fuel, FuelTypes fuelType, UUID vehicle, Scopes scope, Integer year) {

        Specification<Activity> spec = Specification.where(isTransportActivity()).and(hasTransportMode(transportMode)).and(hasTransportType(transportType)).and(hasYear(year)).and(ActivitySpecifications.hasRegion(region)).and(ActivitySpecifications.hasFuel(fuel)).and(ActivitySpecifications.hasFuelType(fuelType)).and(hasVehicle(vehicle)).and(hasScope(scope));

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
            List<ManureCoveringMitigation> manureCovering = manureCoveringMitigationRepository.findAll();
            List<AddingStrawMitigation> addingStraw = addingStrawMitigationRepository.findAll();
            List<DailySpreadMitigation> dailySpread = dailySpreadMitigationRepository.findAll();

            return calculateDashboardData(activities, wasteActivities, aquacultureEmissions, entericFermentationEmissions, limingEmissions, animalManureAndCompostEmissions, riceCultivationEmissions, syntheticFertilizerEmissions, ureaEmissions, biomassGains, disturbanceLosses, firewoodLosses, harvestedLosses, rewettedWetlands, wetlandParks, settlementTrees, streetTrees, greenFences, cropRotation, zeroTillage, protectiveForest, manureCovering, addingStraw, dailySpread);
        } catch (Exception e) {
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

            // Land Use Emissions - Use year range queries for better performance
            int startYear = startDate.getYear();
            int endYear = endDate.getYear();

            List<BiomassGain> biomassGains = biomassGainRepository.findByYearRange(startYear, endYear);
            List<DisturbanceBiomassLoss> disturbanceLosses = disturbanceBiomassLossRepository.findByYearRange(startYear, endYear);
            List<FirewoodRemovalBiomassLoss> firewoodLosses = firewoodRemovalBiomassLossRepository.findByYearRange(startYear, endYear);
            List<HarvestedBiomassLoss> harvestedLosses = harvestedBiomassLossRepository.findByYearRange(startYear, endYear);
            List<RewettedMineralWetlands> rewettedWetlands = rewettedMineralWetlandsRepository.findByYearRange(startYear, endYear);

            // Mitigation Projects - Use year range queries for better performance
            List<WetlandParksMitigation> wetlandParks = wetlandParksMitigationRepository.findByYearRange(startYear, endYear);
            List<SettlementTreesMitigation> settlementTrees = settlementTreesMitigationRepository.findByYearRange(startYear, endYear);
            List<StreetTreesMitigation> streetTrees = streetTreesMitigationRepository.findByYearRange(startYear, endYear);
            List<GreenFencesMitigation> greenFences = greenFencesMitigationRepository.findByYearRange(startYear, endYear);
            List<CropRotationMitigation> cropRotation = cropRotationMitigationRepository.findByYearRange(startYear, endYear);
            List<ZeroTillageMitigation> zeroTillage = zeroTillageMitigationRepository.findByYearRange(startYear, endYear);
            List<ProtectiveForestMitigation> protectiveForest = protectiveForestMitigationRepository.findByYearRange(startYear, endYear);
            List<ManureCoveringMitigation> manureCovering = manureCoveringMitigationRepository.findByYearRange(startYear, endYear);
            List<AddingStrawMitigation> addingStraw = addingStrawMitigationRepository.findByYearRange(startYear, endYear);
            List<DailySpreadMitigation> dailySpread = dailySpreadMitigationRepository.findByYearRange(startYear, endYear);

            DashboardData dashboardData = calculateDashboardData(activities, wasteActivities, aquacultureEmissions, entericFermentationEmissions, limingEmissions, animalManureAndCompostEmissions, riceCultivationEmissions, syntheticFertilizerEmissions, ureaEmissions, biomassGains, disturbanceLosses, firewoodLosses, harvestedLosses, rewettedWetlands, wetlandParks, settlementTrees, streetTrees, greenFences, cropRotation, zeroTillage, protectiveForest, manureCovering, addingStraw, dailySpread);
            dashboardData.setStartingDate(startDate.toString());
            dashboardData.setEndingDate(endDate.toString());
            return dashboardData;
        } catch (Exception e) {
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
        List<ManureCoveringMitigation> manureCovering = manureCoveringMitigationRepository.findAll();
        List<AddingStrawMitigation> addingStraw = addingStrawMitigationRepository.findAll();
        List<DailySpreadMitigation> dailySpread = dailySpreadMitigationRepository.findAll();

        // Group all data by year
        Map<Integer, List<Activity>> groupedActivities = activities.stream().filter(a -> a.getYear() >= startingYear && a.getYear() <= endingYear).collect(groupingBy(Activity::getYear));
        Map<Integer, List<WasteDataAbstract>> groupedWaste = wasteActivities.stream().filter(w -> w.getYear() >= startingYear && w.getYear() <= endingYear).collect(groupingBy(WasteDataAbstract::getYear));
        Map<Integer, List<AquacultureEmissions>> groupedAquaculture = aquacultureEmissions.stream().filter(a -> a.getYear() >= startingYear && a.getYear() <= endingYear).collect(groupingBy(AquacultureEmissions::getYear));
        Map<Integer, List<EntericFermentationEmissions>> groupedEnteric = entericFermentationEmissions.stream().filter(a -> a.getYear() >= startingYear && a.getYear() <= endingYear).collect(groupingBy(EntericFermentationEmissions::getYear));
        Map<Integer, List<LimingEmissions>> groupedLiming = limingEmissions.stream().filter(a -> a.getYear() >= startingYear && a.getYear() <= endingYear).collect(groupingBy(LimingEmissions::getYear));
        Map<Integer, List<AnimalManureAndCompostEmissions>> groupedManure = animalManureAndCompostEmissions.stream().filter(a -> a.getYear() >= startingYear && a.getYear() <= endingYear).collect(groupingBy(AnimalManureAndCompostEmissions::getYear));
        Map<Integer, List<RiceCultivationEmissions>> groupedRice = riceCultivationEmissions.stream().filter(a -> a.getYear() >= startingYear && a.getYear() <= endingYear).collect(groupingBy(RiceCultivationEmissions::getYear));
        Map<Integer, List<SyntheticFertilizerEmissions>> groupedFertilizer = syntheticFertilizerEmissions.stream().filter(a -> a.getYear() >= startingYear && a.getYear() <= endingYear).collect(groupingBy(SyntheticFertilizerEmissions::getYear));
        Map<Integer, List<UreaEmissions>> groupedUrea = ureaEmissions.stream().filter(a -> a.getYear() >= startingYear && a.getYear() <= endingYear).collect(groupingBy(UreaEmissions::getYear));
        Map<Integer, List<BiomassGain>> groupedBiomassGains = biomassGains.stream().filter(a -> a.getYear() >= startingYear && a.getYear() <= endingYear).collect(groupingBy(BiomassGain::getYear));
        Map<Integer, List<DisturbanceBiomassLoss>> groupedDisturbance = disturbanceLosses.stream().filter(a -> a.getYear() >= startingYear && a.getYear() <= endingYear).collect(groupingBy(DisturbanceBiomassLoss::getYear));
        Map<Integer, List<FirewoodRemovalBiomassLoss>> groupedFirewood = firewoodLosses.stream().filter(a -> a.getYear() >= startingYear && a.getYear() <= endingYear).collect(groupingBy(FirewoodRemovalBiomassLoss::getYear));
        Map<Integer, List<HarvestedBiomassLoss>> groupedHarvested = harvestedLosses.stream().filter(a -> a.getYear() >= startingYear && a.getYear() <= endingYear).collect(groupingBy(HarvestedBiomassLoss::getYear));
        Map<Integer, List<RewettedMineralWetlands>> groupedWetlands = rewettedWetlands.stream().filter(a -> a.getYear() >= startingYear && a.getYear() <= endingYear).collect(groupingBy(RewettedMineralWetlands::getYear));
        Map<Integer, List<WetlandParksMitigation>> groupedWetlandParks = wetlandParks.stream().filter(a -> a.getYear() >= startingYear && a.getYear() <= endingYear).collect(groupingBy(WetlandParksMitigation::getYear));
        Map<Integer, List<SettlementTreesMitigation>> groupedSettlement = settlementTrees.stream().filter(a -> a.getYear() >= startingYear && a.getYear() <= endingYear).collect(groupingBy(SettlementTreesMitigation::getYear));
        Map<Integer, List<StreetTreesMitigation>> groupedStreet = streetTrees.stream().filter(a -> a.getYear() >= startingYear && a.getYear() <= endingYear).collect(groupingBy(StreetTreesMitigation::getYear));
        Map<Integer, List<GreenFencesMitigation>> groupedFences = greenFences.stream().filter(a -> a.getYear() >= startingYear && a.getYear() <= endingYear).collect(groupingBy(GreenFencesMitigation::getYear));
        Map<Integer, List<CropRotationMitigation>> groupedCrop = cropRotation.stream().filter(a -> a.getYear() >= startingYear && a.getYear() <= endingYear).collect(groupingBy(CropRotationMitigation::getYear));
        Map<Integer, List<ZeroTillageMitigation>> groupedTillage = zeroTillage.stream().filter(a -> a.getYear() >= startingYear && a.getYear() <= endingYear).collect(groupingBy(ZeroTillageMitigation::getYear));
        Map<Integer, List<ProtectiveForestMitigation>> groupedForest = protectiveForest.stream().filter(a -> a.getYear() >= startingYear && a.getYear() <= endingYear).collect(groupingBy(ProtectiveForestMitigation::getYear));
        Map<Integer, List<ManureCoveringMitigation>> groupedManureCovering = manureCovering.stream().filter(a -> a.getYear() >= startingYear && a.getYear() <= endingYear).collect(groupingBy(ManureCoveringMitigation::getYear));
        Map<Integer, List<AddingStrawMitigation>> groupedAddingStraw = addingStraw.stream().filter(a -> a.getYear() >= startingYear && a.getYear() <= endingYear).collect(groupingBy(AddingStrawMitigation::getYear));
        Map<Integer, List<DailySpreadMitigation>> groupedDailySpread = dailySpread.stream().filter(a -> a.getYear() >= startingYear && a.getYear() <= endingYear).collect(groupingBy(DailySpreadMitigation::getYear));

        // Create aggregated dashboard data for each year
        List<DashboardData> dashboardDataList = new ArrayList<>();
        for (int year = startingYear; year <= endingYear; year++) {

            DashboardData data = calculateDashboardData(groupedActivities.getOrDefault(year, List.of()), groupedWaste.getOrDefault(year, List.of()), groupedAquaculture.getOrDefault(year, List.of()), groupedEnteric.getOrDefault(year, List.of()), groupedLiming.getOrDefault(year, List.of()), groupedManure.getOrDefault(year, List.of()), groupedRice.getOrDefault(year, List.of()), groupedFertilizer.getOrDefault(year, List.of()), groupedUrea.getOrDefault(year, List.of()), groupedBiomassGains.getOrDefault(year, List.of()), groupedDisturbance.getOrDefault(year, List.of()), groupedFirewood.getOrDefault(year, List.of()), groupedHarvested.getOrDefault(year, List.of()), groupedWetlands.getOrDefault(year, List.of()), groupedWetlandParks.getOrDefault(year, List.of()), groupedSettlement.getOrDefault(year, List.of()), groupedStreet.getOrDefault(year, List.of()), groupedFences.getOrDefault(year, List.of()), groupedCrop.getOrDefault(year, List.of()), groupedTillage.getOrDefault(year, List.of()), groupedForest.getOrDefault(year, List.of()), groupedManureCovering.getOrDefault(year, List.of()), groupedAddingStraw.getOrDefault(year, List.of()), groupedDailySpread.getOrDefault(year, List.of()));

            data.setStartingDate(LocalDateTime.of(year, 1, 1, 0, 0).toString());
            data.setEndingDate(LocalDateTime.of(year, 12, 31, 23, 59).toString());
            data.setYear(Year.of(year));
            dashboardDataList.add(data);
        }

        return dashboardDataList;
    }

    @Override
    public List<DashboardData> getDashboardGraphDataByMonth(Integer year) {

        // Fetch all data sources for the year - filter by year
        List<Activity> activities = activityRepository.findAll().stream()
                .filter(a -> a.getYear() == year).toList();
        List<WasteDataAbstract> wasteData = wasteDataAbstractRepository.findAll().stream()
                .filter(w -> w.getYear() == year).toList();

        // Agriculture data (annual granularity only)
        List<AquacultureEmissions> aquacultureEmissions = aquacultureEmissionsRepository.findByYearRange(year, year);
        List<EntericFermentationEmissions> entericFermentationEmissions = entericFermentationEmissionsRepository.findByYearRange(year, year);
        List<LimingEmissions> limingEmissions = limingEmissionsRepository.findByYearRange(year, year);
        List<AnimalManureAndCompostEmissions> animalManureAndCompostEmissions = animalManureAndCompostEmissionsRepository.findByYearRange(year, year);
        List<RiceCultivationEmissions> riceCultivationEmissions = riceCultivationEmissionsRepository.findByYearRange(year, year);
        List<SyntheticFertilizerEmissions> syntheticFertilizerEmissions = syntheticFertilizerEmissionsRepository.findByYearRange(year, year);
        List<UreaEmissions> ureaEmissions = ureaEmissionsRepository.findByYearRange(year, year);

        // Land use data (annual granularity only) - use year range queries
        List<BiomassGain> biomassGains = biomassGainRepository.findByYearRange(year, year);
        List<DisturbanceBiomassLoss> disturbanceLosses = disturbanceBiomassLossRepository.findByYearRange(year, year);
        List<FirewoodRemovalBiomassLoss> firewoodLosses = firewoodRemovalBiomassLossRepository.findByYearRange(year, year);
        List<HarvestedBiomassLoss> harvestedLosses = harvestedBiomassLossRepository.findByYearRange(year, year);
        List<RewettedMineralWetlands> rewettedWetlands = rewettedMineralWetlandsRepository.findByYearRange(year, year);

        // Mitigation data (annual granularity only) - use year range queries
        List<WetlandParksMitigation> wetlandParks = wetlandParksMitigationRepository.findByYearRange(year, year);
        List<SettlementTreesMitigation> settlementTrees = settlementTreesMitigationRepository.findByYearRange(year, year);
        List<StreetTreesMitigation> streetTrees = streetTreesMitigationRepository.findByYearRange(year, year);
        List<GreenFencesMitigation> greenFences = greenFencesMitigationRepository.findByYearRange(year, year);
        List<CropRotationMitigation> cropRotation = cropRotationMitigationRepository.findByYearRange(year, year);
        List<ZeroTillageMitigation> zeroTillage = zeroTillageMitigationRepository.findByYearRange(year, year);
        List<ProtectiveForestMitigation> protectiveForest = protectiveForestMitigationRepository.findByYearRange(year, year);
        List<ManureCoveringMitigation> manureCovering = manureCoveringMitigationRepository.findByYearRange(year, year);
        List<AddingStrawMitigation> addingStraw = addingStrawMitigationRepository.findByYearRange(year, year);
        List<DailySpreadMitigation> dailySpread = dailySpreadMitigationRepository.findByYearRange(year, year);

        // Calculate the FULL YEAR totals for annual data (agriculture, land use, mitigation)
        // This will be divided by 12 and distributed evenly across months
        DashboardData annualOnlyData = calculateDashboardData(
                List.of(), List.of(), // No activities/waste - those have monthly data
                aquacultureEmissions, entericFermentationEmissions, limingEmissions,
                animalManureAndCompostEmissions, riceCultivationEmissions,
                syntheticFertilizerEmissions, ureaEmissions,
                biomassGains, disturbanceLosses, firewoodLosses, harvestedLosses, rewettedWetlands,
                wetlandParks, settlementTrees, streetTrees, greenFences,
                cropRotation, zeroTillage, protectiveForest, manureCovering, addingStraw, dailySpread
        );

        // Calculate monthly portions (1/12 of annual data)
        double monthlyN2O = annualOnlyData.getTotalN2OEmissions() / 12.0;
        double monthlyFossilCO2 = annualOnlyData.getTotalFossilCO2Emissions() / 12.0;
        double monthlyBioCO2 = annualOnlyData.getTotalBioCO2Emissions() / 12.0;
        double monthlyCH4 = annualOnlyData.getTotalCH4Emissions() / 12.0;
        double monthlyLandUse = annualOnlyData.getTotalLandUseEmissions() / 12.0;
        double monthlyMitigation = annualOnlyData.getTotalMitigationKtCO2e() / 12.0;

        // Group monthly data (activities and waste) by year-month
        Map<YearMonth, List<Activity>> activitiesByMonth = activities.stream()
                .collect(groupingBy(activity -> YearMonth.from(activity.getActivityYear())));
        Map<YearMonth, List<WasteDataAbstract>> wasteByMonth = wasteData.stream()
                .collect(groupingBy(waste -> YearMonth.from(waste.getActivityYear())));

        // Create aggregated dashboard data for each month
        List<DashboardData> dashboardDataList = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            YearMonth ym = YearMonth.of(year, month);

            // Calculate monthly data from activities and waste only
            DashboardData monthlyData = calculateDashboardActivityData(activitiesByMonth.getOrDefault(ym, List.of()));
            DashboardData wasteMonthlyData = calculateWasteDashboardData(wasteByMonth.getOrDefault(ym, List.of()));

            // Combine activity and waste data
            monthlyData.setTotalCH4Emissions(monthlyData.getTotalCH4Emissions() + wasteMonthlyData.getTotalCH4Emissions());
            monthlyData.setTotalN2OEmissions(monthlyData.getTotalN2OEmissions() + wasteMonthlyData.getTotalN2OEmissions());
            monthlyData.setTotalFossilCO2Emissions(monthlyData.getTotalFossilCO2Emissions() + wasteMonthlyData.getTotalFossilCO2Emissions());
            monthlyData.setTotalBioCO2Emissions(monthlyData.getTotalBioCO2Emissions() + wasteMonthlyData.getTotalBioCO2Emissions());

            // Add 1/12 of annual data (agriculture, land use, mitigation) to each month
            monthlyData.setTotalN2OEmissions(monthlyData.getTotalN2OEmissions() + monthlyN2O);
            monthlyData.setTotalFossilCO2Emissions(monthlyData.getTotalFossilCO2Emissions() + monthlyFossilCO2);
            monthlyData.setTotalBioCO2Emissions(monthlyData.getTotalBioCO2Emissions() + monthlyBioCO2);
            monthlyData.setTotalCH4Emissions(monthlyData.getTotalCH4Emissions() + monthlyCH4);
            monthlyData.setTotalLandUseEmissions(monthlyLandUse);
            monthlyData.setTotalMitigationKtCO2e(monthlyMitigation);

            // Recalculate CO2Eq with all emissions
            double totalCO2Eq = monthlyData.getTotalFossilCO2Emissions()
                    + monthlyData.getTotalBioCO2Emissions()
                    + monthlyData.getTotalCH4Emissions() * GWP.CH4.getValue()
                    + monthlyData.getTotalN2OEmissions() * GWP.N2O.getValue()
                    + monthlyData.getTotalLandUseEmissions();
            monthlyData.setTotalCO2EqEmissions(totalCO2Eq);

            // Calculate net emissions
            monthlyData.setNetEmissionsKtCO2e(totalCO2Eq - monthlyMitigation);

            monthlyData.setStartingDate(LocalDateTime.of(year, month, 1, 0, 0).toString());
            monthlyData.setEndingDate(LocalDateTime.of(year, month, ym.lengthOfMonth(), 23, 59).toString());
            monthlyData.setMonth(ym.getMonth());
            dashboardDataList.add(monthlyData);
        }

        return dashboardDataList;
    }

    private DashboardData generateDashboardGraphTime_DataPoint(List<Activity> activities, List<WasteDataAbstract> wasteData) {
        DashboardData activityDashboardData = calculateDashboardActivityData(activities);
        DashboardData wasteDashboardData = calculateWasteDashboardData(wasteData);

        activityDashboardData.setTotalCH4Emissions(activityDashboardData.getTotalCH4Emissions() + wasteDashboardData.getTotalCH4Emissions());
        activityDashboardData.setTotalN2OEmissions(activityDashboardData.getTotalN2OEmissions() + wasteDashboardData.getTotalN2OEmissions());
        activityDashboardData.setTotalFossilCO2Emissions(activityDashboardData.getTotalFossilCO2Emissions() + wasteDashboardData.getTotalFossilCO2Emissions());
        activityDashboardData.setTotalBioCO2Emissions(activityDashboardData.getTotalBioCO2Emissions() + wasteDashboardData.getTotalBioCO2Emissions());
        activityDashboardData.setTotalCO2EqEmissions(activityDashboardData.getTotalCO2EqEmissions() + wasteDashboardData.getTotalCO2EqEmissions());
        return activityDashboardData;
    }

    private DashboardData calculateDashboardData(List<Activity> activities, List<WasteDataAbstract> wasteData, List<AquacultureEmissions> aquacultureEmissions, List<EntericFermentationEmissions> entericFermentationEmissions, List<LimingEmissions> limingEmissions, List<AnimalManureAndCompostEmissions> animalManureAndCompostEmissions, List<RiceCultivationEmissions> riceCultivationEmissions, List<SyntheticFertilizerEmissions> syntheticFertilizerEmissions, List<UreaEmissions> ureaEmissions, List<BiomassGain> biomassGains, List<DisturbanceBiomassLoss> disturbanceLosses, List<FirewoodRemovalBiomassLoss> firewoodLosses, List<HarvestedBiomassLoss> harvestedLosses, List<RewettedMineralWetlands> rewettedWetlands, List<WetlandParksMitigation> wetlandParks, List<SettlementTreesMitigation> settlementTrees, List<StreetTreesMitigation> streetTrees, List<GreenFencesMitigation> greenFences, List<CropRotationMitigation> cropRotation, List<ZeroTillageMitigation> zeroTillage, List<ProtectiveForestMitigation> protectiveForest, List<ManureCoveringMitigation> manureCovering, List<AddingStrawMitigation> addingStraw, List<DailySpreadMitigation> dailySpread) {

        DashboardData dashboardData = new DashboardData();

        // === ACTIVITIES ===
        for (Activity activity : activities) {
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
        // BUG FIX: Was using getTotalBioCO2Emissions() instead of
        // getTotalBioCO2Emissions() to accumulate
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
        for (ManureCoveringMitigation mitigation : manureCovering) {
            if (mitigation.getMitigatedN2oEmissionsKilotonnes() != null) {
                totalMitigation += mitigation.getMitigatedN2oEmissionsKilotonnes();
            }
        }
        for (AddingStrawMitigation mitigation : addingStraw) {
            if (mitigation.getMitigatedCh4EmissionsKilotonnes() != null) {
                totalMitigation += mitigation.getMitigatedCh4EmissionsKilotonnes();
            }
        }
        for (DailySpreadMitigation mitigation : dailySpread) {
            if (mitigation.getMitigatedCh4EmissionsKilotonnes() != null) {
                totalMitigation += mitigation.getMitigatedCh4EmissionsKilotonnes();
            }
        }

        dashboardData.setTotalMitigationKtCO2e(totalMitigation);

        // === CALCULATE TOTALS ===
        dashboardData.setTotalCO2EqEmissions(dashboardData.getTotalFossilCO2Emissions() + dashboardData.getTotalBioCO2Emissions() + dashboardData.getTotalCH4Emissions() * GWP.CH4.getValue() + dashboardData.getTotalN2OEmissions() * GWP.N2O.getValue() + dashboardData.getTotalLandUseEmissions());

        // Calculate Net Emissions (Gross - Mitigation)
        // Both totalCO2EqEmissions and totalMitigation are already in Kt
        Double grossEmissionsKt = dashboardData.getTotalCO2EqEmissions();
        dashboardData.setNetEmissionsKtCO2e(grossEmissionsKt - totalMitigation);

        return dashboardData;
    }

    private DashboardData calculateDashboardActivityData(List<Activity> activities) {
        DashboardData dashboardData = new DashboardData();

        if (activities == null) {
            return dashboardData;
        }

        for (Activity activity : activities) {
            dashboardData.setTotalCH4Emissions(dashboardData.getTotalCH4Emissions() + activity.getCH4Emissions());
            dashboardData.setTotalN2OEmissions(dashboardData.getTotalN2OEmissions() + activity.getN2OEmissions());
            dashboardData.setTotalFossilCO2Emissions(dashboardData.getTotalFossilCO2Emissions() + activity.getFossilCO2Emissions());
            dashboardData.setTotalBioCO2Emissions(dashboardData.getTotalBioCO2Emissions() + activity.getBioCO2Emissions());
        }
        dashboardData.setTotalCO2EqEmissions(dashboardData.getTotalFossilCO2Emissions() + dashboardData.getTotalBioCO2Emissions() + dashboardData.getTotalCH4Emissions() * GWP.CH4.getValue() + dashboardData.getTotalN2OEmissions() * GWP.N2O.getValue());
        return dashboardData;
    }

    private DashboardData calculateWasteDashboardData(List<WasteDataAbstract> wasteActivities) {
        DashboardData dashboardData = new DashboardData();

        if (wasteActivities == null || wasteActivities.isEmpty()) {
            return dashboardData;
        }

        for (WasteDataAbstract waste : wasteActivities) {
            dashboardData.setTotalCH4Emissions(dashboardData.getTotalCH4Emissions() + waste.getCH4Emissions());
            dashboardData.setTotalN2OEmissions(dashboardData.getTotalN2OEmissions() + waste.getN2OEmissions());
            dashboardData.setTotalFossilCO2Emissions(dashboardData.getTotalFossilCO2Emissions() + waste.getFossilCO2Emissions());
            dashboardData.setTotalBioCO2Emissions(dashboardData.getTotalBioCO2Emissions() + waste.getBioCO2Emissions());
        }
        dashboardData.setTotalCO2EqEmissions(dashboardData.getTotalFossilCO2Emissions() + dashboardData.getTotalBioCO2Emissions() + dashboardData.getTotalCH4Emissions() * GWP.CH4.getValue() + dashboardData.getTotalN2OEmissions() * GWP.N2O.getValue());
        return dashboardData;
    }

    // create FuelData
    private FuelData createFuelData(CreateStationaryActivityDto dto, Fuel fuel) {
        FuelData fuelData = new FuelData();
        fuelData.setFuel(fuel);
        fuelData.setFuelState(dto.getFuelState());
        fuelData.setMetric(dto.getMetric());
        fuelData.setAmount_in_SI_Unit(0.0);
        return fuelDataRepository.save(fuelData);
    }

    private void updateFuelData(FuelData fuelData, UpdateStationaryActivityDto dto, Fuel fuel) {
        fuelData.setFuel(fuel);
        fuelData.setFuelState(dto.getFuelState());
        fuelData.setMetric(dto.getMetric());
        fuelDataRepository.save(fuelData);
    }

    // Create VehicleData
    private VehicleData createTransportVehicleData(CreateTransportActivityByVehicleDataDto dto, Vehicle vehicle) {
        VehicleData vehicleData = new VehicleData();
        vehicleData.setVehicle(vehicle);

        // Validate and set distance with null checks
        if (dto.getDistanceUnit() != null && dto.getDistanceTravelled() != null) {
            if (dto.getDistanceTravelled() < 0) {
                throw new IllegalArgumentException("Distance travelled cannot be negative");
            }
            vehicleData.setDistanceTravelled_m(dto.getDistanceUnit().toMeters(dto.getDistanceTravelled()));
        } else {
            vehicleData.setDistanceTravelled_m(0.0);
        }

        // Set passengers (can be null)
        vehicleData.setPassengers(dto.getPassengers() != null ? dto.getPassengers() : 0);

        // Validate and set freight weight with null checks
        if (dto.getFreightWeightUnit() != null && dto.getFreightWeight() != null) {
            if (dto.getFreightWeight() < 0) {
                throw new IllegalArgumentException("Freight weight cannot be negative");
            }
            vehicleData.setFreightWeight_Kg(dto.getFreightWeightUnit().toKilograms(dto.getFreightWeight()));
        } else {
            vehicleData.setFreightWeight_Kg(0.0);
        }

        return vehicleDataRepository.save(vehicleData);
    }

    // ============= MINI DASHBOARDS =============

    @Override
    public DashboardData getTransportDashboardSummary(Integer startingYear, Integer endingYear) {
        List<Activity> transportActivities = activityRepository.findByActivityData_ActivityTypeOrderByActivityYearDesc(ActivityTypes.TRANSPORT);

        if (startingYear != null && endingYear != null) {
            transportActivities = transportActivities.stream().filter(a -> a.getYear() >= startingYear && a.getYear() <= endingYear).toList();
        }

        return calculateDashboardActivityData(transportActivities);
    }

    @Override
    public List<DashboardData> getTransportDashboardGraph(Integer startingYear, Integer endingYear) {
        List<Activity> transportActivities = activityRepository.findByActivityData_ActivityTypeOrderByActivityYearDesc(ActivityTypes.TRANSPORT);

        // Default to last 5 years if not specified
        if (startingYear == null || endingYear == null) {
            int currentYear = LocalDateTime.now().getYear();
            startingYear = currentYear - 4;
            endingYear = currentYear;
        }

        // Filter by year range
        final int finalStartYear = startingYear;
        final int finalEndYear = endingYear;
        transportActivities = transportActivities.stream().filter(a -> a.getYear() >= finalStartYear && a.getYear() <= finalEndYear).toList();

        // Group by year
        Map<Integer, List<Activity>> groupedByYear = transportActivities.stream().collect(groupingBy(Activity::getYear));

        // Create dashboard data for each year
        List<DashboardData> dashboardDataList = new ArrayList<>();
        for (int year = startingYear; year <= endingYear; year++) {
            List<Activity> yearActivities = groupedByYear.getOrDefault(year, List.of());
            DashboardData data = calculateDashboardActivityData(yearActivities);
            data.setStartingDate(LocalDateTime.of(year, 1, 1, 0, 0).toString());
            data.setEndingDate(LocalDateTime.of(year, 12, 31, 23, 59).toString());
            data.setYear(Year.of(year));
            dashboardDataList.add(data);
        }

        return dashboardDataList;
    }

    @Override
    public DashboardData getStationaryDashboardSummary(Integer startingYear, Integer endingYear) {
        List<Activity> stationaryActivities = activityRepository.findByActivityData_ActivityTypeOrderByActivityYearDesc(ActivityTypes.STATIONARY);

        if (startingYear != null && endingYear != null) {
            stationaryActivities = stationaryActivities.stream().filter(a -> a.getYear() >= startingYear && a.getYear() <= endingYear).toList();
        }

        return calculateDashboardActivityData(stationaryActivities);
    }

    @Override
    public List<DashboardData> getStationaryDashboardGraph(Integer startingYear, Integer endingYear) {
        List<Activity> stationaryActivities = activityRepository.findByActivityData_ActivityTypeOrderByActivityYearDesc(ActivityTypes.STATIONARY);

        // Default to last 5 years if not specified
        if (startingYear == null || endingYear == null) {
            int currentYear = LocalDateTime.now().getYear();
            startingYear = currentYear - 4;
            endingYear = currentYear;
        }

        // Filter by year range
        final int finalStartYear = startingYear;
        final int finalEndYear = endingYear;
        stationaryActivities = stationaryActivities.stream().filter(a -> a.getYear() >= finalStartYear && a.getYear() <= finalEndYear).toList();

        // Group by year
        Map<Integer, List<Activity>> groupedByYear = stationaryActivities.stream().collect(groupingBy(Activity::getYear));

        // Create dashboard data for each year
        List<DashboardData> dashboardDataList = new ArrayList<>();
        for (int year = startingYear; year <= endingYear; year++) {
            List<Activity> yearActivities = groupedByYear.getOrDefault(year, List.of());
            DashboardData data = calculateDashboardActivityData(yearActivities);
            data.setStartingDate(LocalDateTime.of(year, 1, 1, 0, 0).toString());
            data.setEndingDate(LocalDateTime.of(year, 12, 31, 23, 59).toString());
            data.setYear(Year.of(year));
            dashboardDataList.add(data);
        }

        return dashboardDataList;
    }

    /**
     * Sets all emissions to zero for an activity when emission factors are not found
     */
    private void setZeroEmissions(Activity activity) {
        activity.setCH4Emissions(0.0);
        activity.setFossilCO2Emissions(0.0);
        activity.setBioCO2Emissions(0.0);
        activity.setN2OEmissions(0.0);
    }
}