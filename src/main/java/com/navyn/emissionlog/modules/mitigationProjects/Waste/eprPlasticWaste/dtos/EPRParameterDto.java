package com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class EPRParameterDto {

    @NotNull(message = "Recycling Rate (without EPR) is required")
    @Positive(message = "Recycling Rate (without EPR) must be positive")
    private Double recyclingRateWithoutEPR; // percentage as decimal (e.g., 0.03 for 3%)

    @NotNull(message = "Recycling Rate (with EPR) is required")
    @Positive(message = "Recycling Rate (with EPR) must be positive")
    private Double recyclingRateWithEPR; // percentage as decimal (e.g., 0.15 for 15%)

    @NotNull(message = "Emission Factor is required")
    @Positive(message = "Emission Factor must be positive")
    private Double emissionFactor; // tCO2eq
}

