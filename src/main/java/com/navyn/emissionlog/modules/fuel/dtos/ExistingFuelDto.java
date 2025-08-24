package com.navyn.emissionlog.modules.fuel.dtos;

import com.navyn.emissionlog.Enums.Fuel.FuelTypes;
import lombok.Data;

@Data
public class ExistingFuelDto {
    FuelTypes fuelType;
    String fuel;
    Double lowerHeatingValue;
    Double liquidDensity;
    Double gasDensity;
}
