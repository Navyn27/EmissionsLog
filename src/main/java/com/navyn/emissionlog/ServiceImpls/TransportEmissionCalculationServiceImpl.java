package com.navyn.emissionlog.ServiceImpls;

import com.navyn.emissionlog.Enums.EnergyUnits;
import com.navyn.emissionlog.Enums.MassUnits;
import com.navyn.emissionlog.Enums.Metrics;
import com.navyn.emissionlog.Enums.VolumeUnits;
import com.navyn.emissionlog.Models.Activity;
import com.navyn.emissionlog.Models.ActivityData.FuelData;
import com.navyn.emissionlog.Models.Fuel;
import com.navyn.emissionlog.Models.TransportFuelEmissionFactors;
import com.navyn.emissionlog.Repositories.ActivityRepository;
import com.navyn.emissionlog.Repositories.FuelDataRepository;
import com.navyn.emissionlog.Services.EmissionCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransportEmissionCalculationServiceImpl{

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private FuelDataRepository fuelDataRepository;

    public void calculateEmissions(TransportFuelEmissionFactors factor, Fuel fuel, Activity activity, FuelData fuelData, String unit, Double rawAmount) {
//        Calculate the amount of fuel in the SI unit of provided metric
        Double fuelAmountInSI = convertToSIUnit(rawAmount, fuelData.getMetric(), unit);

//        Set the amount of fuel in the SI unit
        fuelData.setAmount_in_SI_Unit(fuelAmountInSI);
        fuelDataRepository.save(fuelData);

        applyEmissionFactor(activity,factor, fuelAmountInSI);
    }

    private Double convertToSIUnit(Double amount, Metrics metric, String unit) {
        switch(metric) {
            case Metrics.VOLUME:
                return VolumeUnits.valueOf(unit).toLiters(amount);
            case Metrics.MASS:
                return MassUnits.valueOf(unit).toKilograms(amount);
            case Metrics.ENERGY:
                return EnergyUnits.valueOf(unit).toKWh(amount);
            default:
                throw new IllegalArgumentException("Invalid metric: " + metric);
        }
    }

    private void applyEmissionFactor(Activity activity, TransportFuelEmissionFactors factor, Double fuelAmountInSI){
        //apply the emission factor to the activity
        //calculate the emissions based on the fuel amount and the emission factor
        //save the emissions to the activity

        activity.setN2OEmissions(factor.getN2OEmissionFactor() * fuelAmountInSI);
        activity.setCH4Emissions(factor.getCH4EmissionFactor() * fuelAmountInSI);
        activity.setFossilCO2Emissions(factor.getFossilCO2EmissionFactor() * fuelAmountInSI);
        activity.setBioCO2Emissions(factor.getBiogenicCO2EmissionFactor() * fuelAmountInSI);

        activityRepository.save(activity);
    }

}
