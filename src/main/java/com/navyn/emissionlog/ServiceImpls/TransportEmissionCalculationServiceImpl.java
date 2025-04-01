package com.navyn.emissionlog.ServiceImpls;

import com.navyn.emissionlog.Models.Activity;
import com.navyn.emissionlog.Models.ActivityData.FuelData;
import com.navyn.emissionlog.Models.Fuel;
import com.navyn.emissionlog.Services.EmissionCalculationService;
import org.springframework.stereotype.Service;

@Service
public class TransportEmissionCalculationServiceImpl implements EmissionCalculationService {
    @Override
    public void calculateEmissions(Fuel fuel, Activity activity, FuelData fuelData, String unit, Double rawAmount) {
        //implementation
    }
}
