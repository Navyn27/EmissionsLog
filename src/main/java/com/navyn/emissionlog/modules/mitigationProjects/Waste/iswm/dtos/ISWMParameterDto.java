package com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class ISWMParameterDto {

    @NotNull(message = "Degradable Organic Fraction is required")
    @Min(value = 0, message = "Degradable Organic Fraction must be between 0 and 100")
    @Max(value = 100, message = "Degradable Organic Fraction must be between 0 and 100")
    private Double degradableOrganicFraction; // percentage (0-100)

    @NotNull(message = "Landfill Avoidance is required")
    @Positive(message = "Landfill Avoidance must be positive")
    private Double landfillAvoidance; // kgCO₂e/tonne

    @NotNull(message = "Composting Emission Factor is required")
    @Positive(message = "Composting Emission Factor must be positive")
    private Double compostingEF; // kgCO₂e/tonne of DOF
}
