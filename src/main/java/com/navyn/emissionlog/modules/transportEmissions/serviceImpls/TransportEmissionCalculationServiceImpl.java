package com.navyn.emissionlog.modules.transportEmissions.serviceImpls;

import com.navyn.emissionlog.Enums.*;
import com.navyn.emissionlog.modules.activities.models.Activity;
import com.navyn.emissionlog.modules.fuel.FuelData;
import com.navyn.emissionlog.modules.activities.models.VehicleData;
import com.navyn.emissionlog.modules.fuel.Fuel;
import com.navyn.emissionlog.modules.transportEmissions.models.TransportFuelEmissionFactors;
import com.navyn.emissionlog.modules.transportEmissions.models.TransportVehicleDataEmissionFactors;
import com.navyn.emissionlog.Repositories.ActivityRepository;
import com.navyn.emissionlog.modules.fuel.repositories.FuelDataRepository;
import com.navyn.emissionlog.modules.transportEmissions.repositories.TransportVehicleDataEmissionFactorsRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransportEmissionCalculationServiceImpl{

    private final ActivityRepository activityRepository;
    private final FuelDataRepository fuelDataRepository;
    private final TransportVehicleDataEmissionFactorsRepository transportVehicleDataEmissionFactorsRepository;

    public void calculateEmissionsByFuel(TransportFuelEmissionFactors factor, Fuel fuel, Activity activity, FuelData fuelData, String unit, Double rawAmount) {
//        Calculate the amount of fuel in the SI unit of provided metric
        Double fuelAmountInSI = convertToSIUnit(rawAmount, fuelData.getMetric(), unit);

//        Set the amount of fuel in the SI unit
        fuelData.setAmount_in_SI_Unit(fuelAmountInSI);
        fuelDataRepository.save(fuelData);

        applyEmissionFactor(activity,factor, fuelAmountInSI);
    }

    public void calculateEmissionsByVehicleData(Activity activity, VehicleData vehicleData, Fuel fuel, RegionGroup regionGroup, MobileActivityDataType mobileActivityDataType) {
        TransportVehicleDataEmissionFactors factor = transportVehicleDataEmissionFactorsRepository.findByVehicleAndFuelAndRegionGroup(vehicleData.getVehicle(), fuel, regionGroup);
        activity.setBioCO2Emissions(0.0);
        if(activity.getCH4Emissions() != 0.0)
            activity.setCH4Emissions(vehicleData.getDistanceTravelled_m() * factor.getCH4EmissionFactor());
        if(activity.getFossilCO2Emissions() != 0.0)
            activity.setFossilCO2Emissions(vehicleData.getDistanceTravelled_m() * factor.getCO2EmissionFactor());
        if(activity.getN2OEmissions() != 0.0)
            activity.setN2OEmissions(vehicleData.getDistanceTravelled_m() * factor.getN2OEmissionFactor());
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

        if(factor.getN2OEmissionFactor() != null){
            activity.setN2OEmissions(factor.getN2OEmissionFactor() * fuelAmountInSI);
        }
        else{
            activity.setN2OEmissions(0.0);
        }
        if(factor.getCH4EmissionFactor() != null){
            activity.setCH4Emissions(factor.getCH4EmissionFactor() * fuelAmountInSI);
        }
        else{
            activity.setCH4Emissions(0.0);
        }
        if(factor.getFossilCO2EmissionFactor() != null){
            activity.setFossilCO2Emissions(factor.getFossilCO2EmissionFactor() * fuelAmountInSI);
        }
        else{
            activity.setFossilCO2Emissions(0.0);
        }
        if(factor.getBiogenicCO2EmissionFactor() != null){
            activity.setBioCO2Emissions(factor.getBiogenicCO2EmissionFactor() * fuelAmountInSI);
        }
        else{
            activity.setBioCO2Emissions(0.0);
        }
        activityRepository.save(activity);
    }


}
