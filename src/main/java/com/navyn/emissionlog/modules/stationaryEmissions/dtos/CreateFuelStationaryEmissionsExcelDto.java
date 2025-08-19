package com.navyn.emissionlog.modules.stationaryEmissions.dtos;

import lombok.Data;

@Data
public class CreateFuelStationaryEmissionsExcelDto {
    private String fuelType;
    private String fuel;
    private String fuelDescription;
    private Double lowerHeatingValue = 0.0;
    private String emission;
    private Double energyBasis = 0.0;
    private Double massBasis = 0.0;
    private Double fuelDensityLiquids = 0.0;
    private Double fuelDensityGases=0.0;
    private Double liquidBasis=0.0;
    private Double gasBasis= 0.0;
}
