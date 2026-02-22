package com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @NotNull(message = "CH4 GWP (100-year) is required")
    @Positive(message = "CH4 GWP (100-year) must be positive")
    private Double ch4Gwp100Year; // kg CO2e per kg CH4

    // N2O parameters (optional for backward compatibility; when null, N2O contribution = 0)
    private Double totalNKgPerM3; // kg N per m³ wastewater
    private Double n2oEfPlant; // kg N2O-N per kg N (plant)
    private Double n2oEfEffluent; // kg N2O-N per kg N (effluent)
    @JsonProperty("nRemovalEfficiency")
    private Double nRemovalEfficiency; // fraction 0–1 (e.g. 0.85)
    private Double n2oGwp100Year; // kg CO2e per kg N2O
}

