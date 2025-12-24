package com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class WasteToWtEParameterDto {

    @NotNull(message = "Net Emission Factor is required")
    @Positive(message = "Net Emission Factor must be positive")
    private Double netEmissionFactor; // tCO2eq/t
}

