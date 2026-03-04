package com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.dto.parameters;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CharcoalParameterDto {
    @NotNull(message = "Net Calorific Value is required")
    @Positive(message = "Net Calorific Value must be positive")
    private Double netCalorificValue; // Net calorific value TJ/t

    @NotNull(message = "Emission Factor is required")
    @Positive(message = "Emission Factor must be positive")
    private Double emissionFactor; // CO2 emission factor TJ/t

    @NotNull(message = "Adjusted Emission Factor is required")
    @Positive(message = "Adjusted Emission Factor must be positive")
    private Double adjustedEmissionFactor; // Non CO2 emission factor TJ/t

    @NotNull(message = "Biomass is required")
    @Positive(message = "Biomass must be positive")
    private Double biomass; // Faction of non-renewable biomass %

    @NotNull(message = "Fuel Consumption is required")
    @Positive(message = "Fuel Consumption must be positive")
    private Double fuelConsumption; // Per capita fuel consumption by a baseline device tonnes

    @NotNull(message = "Efficiency is required")
    @Positive(message = "Efficiency must be positive")
    private Double efficiency; // Baseline efficiency %

    @NotNull(message = "Size is required")
    @Positive(message = "Size must be positive")
    private Double size; // HH size no unit
}

