package com.navyn.emissionlog.ServiceImpls;

import com.navyn.emissionlog.Enums.*;
import com.navyn.emissionlog.Models.Activity;
import com.navyn.emissionlog.Models.ActivityData.FuelData;
import com.navyn.emissionlog.Models.Fuel;
import com.navyn.emissionlog.Models.StationaryEmissionFactors;
import com.navyn.emissionlog.Repositories.ActivityRepository;
import com.navyn.emissionlog.Repositories.FuelDataRepository;
import com.navyn.emissionlog.Services.EmissionCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StationaryEmissionCalculationServiceImpl implements EmissionCalculationService {

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    FuelDataRepository fuelDataRepository;

    /**
     * Calculates the emissions of a stationary activity
     * @param fuel The fuel used in the activity
     * @param activity The activity to calculate the emissions for
     * @param fuelData The data of the fuel used
     * @param unit The unit of the fuel
     * @param rawAmount The amount of fuel
     */
    @Override
    public void calculateEmissions(Fuel fuel, Activity activity, FuelData fuelData, String unit, Double rawAmount) {

        //Calculate the amount of fuel in the SI unit of provided metric
        Double fuelAmountInSI = convertToSIUnit(rawAmount, fuelData.getMetric(), unit);

        //Set the amount of fuel in the SI unit
        fuelData.setAmount_in_SI_Unit(fuelAmountInSI);
        fuelDataRepository.save(fuelData);

        //Get the emission factors of the fuel
        List<StationaryEmissionFactors> factors = fuel.getStationaryEmissionFactorsList();

        //Apply the emission factors to the activity
        for(StationaryEmissionFactors factor : factors) {
            applyEmissionFactor(activity, factor, fuelAmountInSI, fuelData.getFuelState());
        }
    }

    /**
     * Converts the amount of fuel to the SI unit
     * @param amount The amount of fuel
     * @param metric The metric of the fuel
     * @param unit The unit of the fuel
     * @return The amount of fuel in the SI unit
     */
    private Double convertToSIUnit(Double amount, Metrics metric, String unit) {
        switch(metric) {
            case MASS:
                return MassUnits.valueOf(unit).toKilograms(amount);
            case ENERGY:
                return EnergyUnits.valueOf(unit).toKWh(amount);
            case VOLUME:
                return VolumeUnits.valueOf(unit).toLiters(amount);
            default:
                return 0.0;
        }
    }

    /**
     * Applies the emission factor to the activity
     * @param activity The activity to apply the emission factor to
     * @param factor The emission factor to apply
     * @param amount_in_SI The amount of fuel in SI_unit
     * @param state The state of the fuel
     */
    private void applyEmissionFactor(Activity activity, StationaryEmissionFactors factor, Double amount_in_SI, FuelStates state) {
        Double factorValue;

        switch(state) {
            case GASEOUS: factorValue = factor.getGasBasis(); break;
            case LIQUID: factorValue = factor.getLiquidBasis(); break;
            case SOLID: factorValue = factor.getMassBasis(); break;
            default: factorValue = factor.getEnergyBasis(); break;
        }

        switch (factor.getEmmission()) {
            case CH4:
                activity.setCH4Emissions(amount_in_SI * factorValue);
                break;
            case CO2:
                if (state == FuelStates.BIOMASS) {
                    activity.setBiomassCO2Emissions(amount_in_SI * factorValue);
                } else {
                    activity.setFossilCO2Emissions(amount_in_SI * factorValue);
                }
                break;
            default: // N2O
                activity.setN2OEmissions(amount_in_SI * factorValue);
                break;
        }
    }
}