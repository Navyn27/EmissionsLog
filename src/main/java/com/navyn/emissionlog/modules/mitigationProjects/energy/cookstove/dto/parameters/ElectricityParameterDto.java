package com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.dto.parameters;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ElectricityParameterDto {
    @NotNull(message = "Fuel Consumption is required")
    @Positive(message = "Fuel Consumption must be positive")
    private Double fuelConsumption; // Per capita fuel consumption by a baseline device MWh

    @NotNull(message = "Emission Factor is required")
    @Positive(message = "Emission Factor must be positive")
    private Double emissionFactor; // CO2 emission factor tCO2/MWh

    @NotNull(message = "Efficiency is required")
    @Positive(message = "Efficiency must be positive")
    private Double efficiency; // Baseline efficiency %

    @NotNull(message = "Size is required")
    @Positive(message = "Size must be positive")
    private Double size; // HH size no unit
}

