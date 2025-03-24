package com.navyn.emissionlog.Payload.Requests;

import com.navyn.emissionlog.Enums.Emissions;
import com.navyn.emissionlog.Enums.FuelTypes;
import lombok.Data;

@Data
public class CreateFuelDto {
    private FuelTypes fuelTypes;
    private String fuel;
    private Emissions emission;
    private Double lowerHeatingValue = 0.0;
    private Double energyBasis = 0.0;
    private Double massBasis = 0.0;
    private Double fuelDensityLiquids = 0.0;
    private Double fuelDensityGases=0.0;
    private Double liquidBasis=0.0;
    private Double gasBasis= 0.0;
}