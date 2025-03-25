package com.navyn.emissionlog.Services;

import com.navyn.emissionlog.Enums.*;
import com.navyn.emissionlog.Models.Activity;
import com.navyn.emissionlog.Models.ActivityData.FuelData;
import com.navyn.emissionlog.Models.Fuel;

public interface EmissionCalculationService {
    void calculateEmissions(Fuel fuel, Activity activity, FuelData fuelData, String unit, Double rawAmount);
}