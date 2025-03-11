package com.navyn.emissionlog.Payload.Requests;

import com.navyn.emissionlog.Enums.Emissions;
import lombok.Data;

@Data
public class CreateFuelFromExcelDto {
    private String fuelType;
    private String fuel;
    private Double lowerHeatingValue = 0.0;
    private Double energyBasis = 0.0;
    private Double massBasis = 0.0;
    private Double fuelDensityLiquids = 0.0;
    private Double fuelDensityGases=0.0;
    private Double liquidBasis=0.0;
    private Double gasBasis= 0.0;
    private Emissions emission = Emissions.N2O;
}
