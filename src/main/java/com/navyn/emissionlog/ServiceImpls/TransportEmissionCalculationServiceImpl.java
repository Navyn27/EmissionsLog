package com.navyn.emissionlog.ServiceImpls;

import com.navyn.emissionlog.Models.Activity;
import com.navyn.emissionlog.Models.ActivityData.FuelData;
import com.navyn.emissionlog.Models.Fuel;
import com.navyn.emissionlog.Repositories.ActivityRepository;
import com.navyn.emissionlog.Repositories.FuelDataRepository;
import com.navyn.emissionlog.Services.EmissionCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransportEmissionCalculationServiceImpl implements EmissionCalculationService {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private FuelDataRepository fuelDataRepository;

    @Override
    public void calculateEmissions(Fuel fuel, Activity activity, FuelData fuelData, String unit, Double rawAmount) {
        //Calculate the amount of fuel in the SI unit of provided metric
//        Double fuelAmountInSI = convertToSIUnit(rawAmount, fuelData.getMetric(), unit);

        //Set the amount of fuel in the SI unit
//        fuelData.setAmount_in_SI_Unit(fuelAmountInSI);
//        fuelDataRepository.save(fuelData);

        //Get the emission factors of the fuel
        //List<StationaryEmissionFactors> factors = fuel.getStationaryEmissionFactorsList();

        //Apply the emission factors to the activity
        //for(StationaryEmissionFactors factor : factors) {
        //    applyEmissionFactor(activity, factor, fuelAmountInSI, fuelData.getFuelState());
        //}
    }

//    private convertToSIUnity(Double amount, Metrics metric, String unit) {
        //Convert the amount of fuel to the SI unit
        //This will depend on the metric and unit of the fuel
        //For example, if the metric is "LITERS" and the unit is "GALLONS", convert gallons to liters
        //Return the converted amount
//    }


    //find the fuel by the following criteria

    //-regionGroup
    //-vehicleEngineType
    //-transportType
    //-fuel

    //convert the amount of fuel to the SI unit
    //apply the emission factors to the activity


}
