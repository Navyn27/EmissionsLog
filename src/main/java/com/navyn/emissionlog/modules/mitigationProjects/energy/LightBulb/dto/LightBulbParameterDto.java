package com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class LightBulbParameterDto {

    @NotNull(message = "Emission Factor is required")
    @Positive(message = " Emission Factor must be positive")
    private Double emissionFactor; // tCO2eq/t
}

