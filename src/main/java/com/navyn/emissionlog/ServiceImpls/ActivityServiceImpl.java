package com.navyn.emissionlog.ServiceImpls;

import com.navyn.emissionlog.Enums.*;
import com.navyn.emissionlog.Models.Activity;
import com.navyn.emissionlog.Models.ActivityData.ActivityData;
import com.navyn.emissionlog.Models.ActivityData.FuelData;
import com.navyn.emissionlog.Models.ActivityData.StationaryActivityData;
import com.navyn.emissionlog.Models.ActivityData.TransportActivityData;
import com.navyn.emissionlog.Models.StationaryEmissionFactors;
import com.navyn.emissionlog.Models.Fuel;
import com.navyn.emissionlog.Payload.Requests.CreateTransportActivityByFuelDto;
import com.navyn.emissionlog.Payload.Requests.CreateTransportActivityByVehicleDataDto;
import com.navyn.emissionlog.Payload.Requests.CreateStationaryActivityDto;
import com.navyn.emissionlog.Repositories.*;
import com.navyn.emissionlog.Services.ActivityService;
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

    @Override
    public Activity createStationaryActivity(CreateStationaryActivityDto activity) {
        Optional<Fuel> fuel = fuelRepository.findById(activity.getFuel());

        if(fuel.isEmpty()){
            throw new IllegalArgumentException("Fuel is not recorded");
        }

        //Get Stationary Emissions
        List<StationaryEmissionFactors> stationaryEmissionFactorsList = fuel.get().getStationaryEmissionFactorsList();

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
        FuelData fuelData = createTransportFuelData(activityDto, fuel.get());

        // Create ActivityData
        ActivityData transportActivityData = new TransportActivityData();
        transportActivityData.setActivityType(ActivityTypes.TRANSPORT);
        transportActivityData.setFuelData(fuelData);
        transportActivityData = activityDataRepository.save(transportActivityData);

        // Create Activity
        Activity activity = new Activity();
        activity.setSector(activityDto.getSector());
        activity.setScope(activityDto.getScope());
        activity.setRegion(regionRepository.findById(activityDto.getRegion()).get());
        activity.setActivityData(transportActivityData);
        activity.setActivityYear(activityDto.getActivityYear());

        // Calculate emissions
        transportEmissionCalculationService.calculateEmissions(fuel.get(), activity, fuelData,
             activityDto.getFuelUnit(), activityDto.getFuelAmount());

        return activityRepository.save(activity);
    }

    @Override
    public Activity createTransportActivityByVehicleData(CreateTransportActivityByVehicleDataDto actibityDto) {
        return null;
    }

    // Helper method for creating mobile fuel data
    private FuelData createTransportFuelData(CreateTransportActivityByFuelDto dto, Fuel fuel) {
        FuelData fuelData = new FuelData();
        fuelData.setFuel(fuel);
        fuelData.setFuelState(dto.getFuelState());
        fuelData.setMetric(dto.getMetric());
        fuelData.setAmount_in_SI_Unit(0.0);
        return fuelDataRepository.save(fuelData);
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
}