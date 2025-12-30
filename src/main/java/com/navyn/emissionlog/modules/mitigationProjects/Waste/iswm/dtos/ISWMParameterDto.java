package com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ISWMParameterDto {

    @NotNull(message = "Degradable Organic Fraction is required")
    @Positive(message = "Degradable Organic Fraction must be positive")
    private Double degradableOrganicFraction; // percentage (0-100)

    @NotNull(message = "Landfill Avoidance is required")
    @Positive(message = "Landfill Avoidance must be positive")
    private Double landfillAvoidance; // kgCO₂e/tonne

    @NotNull(message = "Composting Emission Factor is required")
    @Positive(message = "Composting Emission Factor must be positive")
    private Double compostingEF; // kgCO₂e/tonne of DOF
}

