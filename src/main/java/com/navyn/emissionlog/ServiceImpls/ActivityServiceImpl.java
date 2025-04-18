package com.navyn.emissionlog.ServiceImpls;

import com.navyn.emissionlog.Enums.*;
import com.navyn.emissionlog.Models.*;
import com.navyn.emissionlog.Models.ActivityData.*;
import com.navyn.emissionlog.Payload.Requests.CreateTransportActivityByFuelDto;
import com.navyn.emissionlog.Payload.Requests.CreateTransportActivityByVehicleDataDto;
import com.navyn.emissionlog.Payload.Requests.CreateStationaryActivityDto;
import com.navyn.emissionlog.Repositories.*;
import com.navyn.emissionlog.Services.ActivityService;
import com.navyn.emissionlog.Services.TransportFuelEmissionFactorsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private FuelRepository fuelRepository;

    @Autowired
    private ActivityDataRepository activityDataRepository;

    @Autowired
    private FuelDataRepository fuelDataRepository;

    @Autowired
    private StationaryEmissionCalculationServiceImpl stationaryEmissionCalculationService;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private TransportEmissionCalculationServiceImpl transportEmissionCalculationService;

    @Autowired
    private TransportFuelEmissionFactorsService transportFuelEmissionFactorsService;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private VehicleDataRepository vehicleDataRepository;

    @Override
    public Activity createStationaryActivity(CreateStationaryActivityDto activity) {
        Optional<Fuel> fuel = fuelRepository.findById(activity.getFuel());

        if(fuel.isEmpty()){
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
        activity1.setScope(activity.getScope());
        activity1.setRegion(regionRepository.findById(activity.getRegion()).get());
        activity1.setActivityData(stationaryActivityData);
        activity1.setActivityYear(activity.getActivityYear());
        activity1.setActivityData(stationaryActivityData);

        //calculate emissions
        stationaryEmissionCalculationService.calculateEmissions(fuel.get(), activity1, fuelData, activity.getFuelUnit(), activity.getFuelAmount());

        return activityRepository.save(activity1);
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

        //find the emissions factors
        Optional<TransportFuelEmissionFactors> transportEmissionFactorsList = transportFuelEmissionFactorsService.findByFuelAndRegionGroupAndTransportTypeAndVehicleEngineType(fuel.get(), activityDto.getRegionGroup(), activityDto.getTransportType(), activityDto.getVehicleType());

        if(transportEmissionFactorsList.isEmpty()){
            throw new IllegalArgumentException("Transport Emission Factors not found for specified region");
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
            Optional<TransportFuelEmissionFactors> transportEmissionFactorsList = transportFuelEmissionFactorsService.findByFuelAndRegionGroupAndTransportTypeAndVehicleEngineType(fuel.get(), activityDto.getRegionGroup(), activityDto.getTransportType(), activityDto.getVehicleType());

            if(transportEmissionFactorsList.isEmpty()){
                throw new IllegalArgumentException("Transport Emission Factors not found for specified region");
            }
            transportEmissionCalculationService.calculateEmissionsByFuel(transportEmissionFactorsList.get(), fuel.get(), activity, fuelData,
                    activityDto.getFuelUnit(), activityDto.getFuelAmount());
            transportEmissionCalculationService.calculateEmissionsByVehicleData(activity, vehicleData, fuel.get(), activityDto.getRegionGroup(), activityDto.getMobileActivityDataType());
        }

        return activityRepository.save(activity);
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