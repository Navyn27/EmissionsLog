package com.navyn.emissionlog.Payload.Requests;

import com.navyn.emissionlog.Enums.Emissions;
import com.navyn.emissionlog.Enums.FuelTypes;
import lombok.Data;

@Data
public class CreateFuelDto {
    private FuelTypes fuelTypes;
    private String fuelDescription;
    private String fuel;
    private Double lowerHeatingValue = 0.0;
    private Double fuelDensityLiquids = 0.0;
    private Double fuelDensityGases=0.0;
}