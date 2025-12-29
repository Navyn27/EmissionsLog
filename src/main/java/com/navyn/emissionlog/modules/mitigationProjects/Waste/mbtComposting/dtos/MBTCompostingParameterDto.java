package com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class MBTCompostingParameterDto {
    
    @NotNull(message = "Emission Factor is required")
    @Positive(message = "Emission Factor must be positive")
    private Double emissionFactor; // tCO2eq/ton
}

