package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandsRewetting.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WetlandsRewettingParameterDto {

    @NotNull(message = "CH4 emission factor per ha per year is required")
    @DecimalMin(value = "0.0", message = "CH4 emission factor must be at least 0")
    private Double ch4EmissionFactorPerHaPerYear; // tonnes CH4/ha/year

    @NotNull(message = "GWP of methane is required")
    @DecimalMin(value = "0.0", message = "GWP of methane must be at least 0")
    private Double gwpMethane;

    @NotNull(message = "Carbon sequestration factor per ha per year is required")
    @DecimalMin(value = "0.0", message = "Carbon sequestration factor must be at least 0")
    private Double carbonSequestrationFactorPerHaPerYear; // tonnes C/year/ha
}
