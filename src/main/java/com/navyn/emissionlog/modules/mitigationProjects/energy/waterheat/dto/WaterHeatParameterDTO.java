package com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WaterHeatParameterDTO {

    @NotNull(message = "Delta temperature is required")
    @Positive(message = "Delta temperature must be positive")
    private Integer deltaTemperature;

    @NotNull(message = "Specific heat water is required")
    @Positive(message = "Specific heat water must be positive")
    private Integer specificHeatWater;

    @NotNull(message = "Grid emission factor is required")
    @Positive(message = "Grid emission factor must be positive")
    private Double gridEmissionFactor; // tCO2/MWh
}

