package com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class KigaliWWTPParameterDto {

    @NotNull(message = "Methane Emission Factor is required")
    @Positive(message = "Methane Emission Factor must be positive")
    private Double methaneEmissionFactor; // kg CH4 per kg COD

    @NotNull(message = "COD Concentration is required")
    @Positive(message = "COD Concentration must be positive")
    private Double codConcentration; // kg COD per m³

    @NotNull(message = "CH₄ GWP (100-year) is required")
    @Positive(message = "CH₄ GWP (100-year) must be positive")
    private Double ch4Gwp100Year; // kg CO2e per kg CH4
}

