package com.navyn.emissionlog.ServiceImpls;

import com.navyn.emissionlog.Enums.EnergyUnits;
import com.navyn.emissionlog.Enums.MassUnits;
import com.navyn.emissionlog.Enums.VolumeUnits;
import com.navyn.emissionlog.Models.Activity;
import com.navyn.emissionlog.Models.EmissionFactors;
import com.navyn.emissionlog.Models.Fuel;
import com.navyn.emissionlog.Payload.Requests.CreateActivityDto;
import com.navyn.emissionlog.Repositories.ActivityRepository;
import com.navyn.emissionlog.Repositories.FuelRepository;
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

    @Override
    public Activity createActivity(CreateActivityDto activity) {
        Optional<Fuel> fuel = fuelRepository.findById(activity.getFuel());

        if(fuel.isEmpty()){
            throw new IllegalArgumentException("Fuel is not recorded");
        }

        List<EmissionFactors> emissionFactorsList = fuel.get().getEmissionFactorsList();
        Activity activity1 = new Activity();
        Double fuelAmountInSI = 0.0;
        activity1.setSector(activity.getSector());
        activity1.setFuelState(activity.getFuelState());
        activity1.setFuel(fuel.get());
        activity1.setMetric(activity.getMetric());
        activity1.setScope(activity.getScope());
        String unit = activity.getFuelUnit();

        switch(activity.getMetric()){
            case MASS:
                MassUnits massUnit = MassUnits.valueOf(unit);
                fuelAmountInSI = massUnit.toKilograms(activity.getFuelAmount());
                break;
            case ENERGY:
                EnergyUnits energyUnit = EnergyUnits.valueOf(unit);
                fuelAmountInSI = energyUnit.toKWh(activity.getFuelAmount());
                break;
            case VOLUME:
                VolumeUnits volumeUnit = VolumeUnits.valueOf(unit);
                fuelAmountInSI = volumeUnit.toLiters(activity.getFuelAmount());
                break;
            default:
                fuelAmountInSI = 0.0;
        }
        activity1.setFuelAmount(fuelAmountInSI);

        for(EmissionFactors emissionFactor : emissionFactorsList){
            switch(activity.getFuelState()){
                case GASEOUS:
                    switch (emissionFactor.getEmmission()){
                        case CH4:
                            activity1.setCH4Emissions(fuelAmountInSI*emissionFactor.getGasBasis());
                            break;
                        case CO2:
                            activity1.setFossilCO2Emisions(fuelAmountInSI*emissionFactor.getGasBasis());
                            break;
                        default:
                            activity1.setN2OEmissions(fuelAmountInSI*emissionFactor.getGasBasis());
                            break;
                    }
                case LIQUID:
                    switch (emissionFactor.getEmmission()) {
                        case CH4:
                            activity1.setCH4Emissions(fuelAmountInSI * emissionFactor.getLiquidBasis());
                            break;
                        case CO2:
                            activity1.setFossilCO2Emisions(fuelAmountInSI * emissionFactor.getLiquidBasis());
                            break;
                        default:
                            activity1.setN2OEmissions(fuelAmountInSI  * emissionFactor.getLiquidBasis());
                            break;
                    }
                case SOLID:
                    switch (emissionFactor.getEmmission()) {
                        case CH4:
                            activity1.setCH4Emissions(fuelAmountInSI * emissionFactor.getMassBasis());
                            break;
                        case CO2:
                            activity1.setFossilCO2Emisions(fuelAmountInSI * emissionFactor.getMassBasis());
                            break;
                        default:
                            activity1.setN2OEmissions(fuelAmountInSI * emissionFactor.getMassBasis());
                            break;
                    }
                default:
                    switch (emissionFactor.getEmmission()) {
                        case CH4:
                            activity1.setCH4Emissions(fuelAmountInSI * emissionFactor.getEnergyBasis());
                        case CO2:
                            activity1.setFossilCO2Emisions(fuelAmountInSI * emissionFactor.getEnergyBasis());
                        default:
                            activity1.setN2OEmissions(fuelAmountInSI * emissionFactor.getEnergyBasis());
                    }
            }
        }

        return activityRepository.save(activity1);
    }

    @Override
    public Activity updateActivity(UUID id, Activity activity) {
        Fuel fuel = activity.getFuel();
        Optional<Activity> existingActivity = activityRepository.findById(id);
        if (existingActivity.isEmpty()) {
            throw new IllegalArgumentException("Activity not found");
        }

        Activity updatedActivity = existingActivity.get();
        updatedActivity.setSector(activity.getSector());
        updatedActivity.setFuel(fuel);
        updatedActivity.setFuelState(activity.getFuelState());
        updatedActivity.setFuelAmount(activity.getFuelAmount());
        updatedActivity.setMetric(activity.getMetric());
        updatedActivity.setCH4Emissions(activity.getCH4Emissions());
        updatedActivity.setFossilCO2Emisions(activity.getFossilCO2Emisions());
        updatedActivity.setBiomassCO2Emissions(activity.getBiomassCO2Emissions());
        updatedActivity.setN2OEmissions(activity.getN2OEmissions());

        return activityRepository.save(updatedActivity);
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
}