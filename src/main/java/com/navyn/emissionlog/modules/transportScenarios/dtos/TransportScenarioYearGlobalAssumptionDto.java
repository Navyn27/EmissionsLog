package com.navyn.emissionlog.modules.transportScenarios.dtos;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class TransportScenarioYearGlobalAssumptionDto {

    private UUID id; // Nullable for create

    @NotNull(message = "Scenario ID is required")
    private UUID scenarioId;

    @NotNull(message = "Year is required")
    @Min(value = 1990, message = "Year must be at least 1990")
    @Max(value = 2100, message = "Year cannot exceed 2100")
    private Integer year;

    @NotNull(message = "Fuel emission factor for gasoline is required")
    @Positive(message = "Fuel emission factor for gasoline must be positive")
    private Double fuelEmissionFactorTco2PerTJ_Gasoline;

    @NotNull(message = "Fuel emission factor for diesel is required")
    @Positive(message = "Fuel emission factor for diesel must be positive")
    private Double fuelEmissionFactorTco2PerTJ_Diesel;

    @NotNull(message = "Fuel energy density for gasoline is required")
    @Positive(message = "Fuel energy density for gasoline must be positive")
    private Double fuelEnergyDensityTjPerL_Gasoline;

    @NotNull(message = "Fuel energy density for diesel is required")
    @Positive(message = "Fuel energy density for diesel must be positive")
    private Double fuelEnergyDensityTjPerL_Diesel;

    @NotNull(message = "Grid emission factor is required")
    @Min(value = 0, message = "Grid emission factor must be at least 0")
    private Double gridEmissionFactorTco2PerMWh;
}
