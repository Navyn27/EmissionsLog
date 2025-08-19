package com.navyn.emissionlog.modules.stationaryEmissions.services;

import com.navyn.emissionlog.modules.activities.models.Activity;
import com.navyn.emissionlog.modules.fuel.FuelData;
import com.navyn.emissionlog.modules.fuel.Fuel;

public interface StationaryEmissionsCalculationService {
    void calculateEmissions(Fuel fuel, Activity activity, FuelData fuelData, String unit, Double rawAmount);
}