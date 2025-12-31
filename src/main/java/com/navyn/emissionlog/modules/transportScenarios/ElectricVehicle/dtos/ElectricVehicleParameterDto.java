package com.navyn.emissionlog.modules.transportScenarios.ElectricVehicle.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ElectricVehicleParameterDto {

    @NotNull(message = "Grid Emission Factor is required")
    @Positive(message = "Grid Emission Factor must be positive")
    private Double gridEmissionFactor; // tonne CO₂/MWh
}

