package com.navyn.emissionlog.modules.transportScenarios.dtos;

import com.navyn.emissionlog.modules.transportScenarios.enums.TransportScenarioFuelType;
import com.navyn.emissionlog.modules.transportScenarios.enums.TransportScenarioVehicleCategory;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class TransportScenarioVehicleAssumptionDto {

    private UUID id; // Nullable for create

    @NotNull(message = "Scenario ID is required")
    private UUID scenarioId;

    @NotNull(message = "Year is required")
    @Min(value = 1990, message = "Year must be at least 1990")
    @Max(value = 2100, message = "Year cannot exceed 2100")
    private Integer year;

    @NotNull(message = "Vehicle category is required")
    private TransportScenarioVehicleCategory vehicleCategory;

    @NotNull(message = "Fuel type is required")
    private TransportScenarioFuelType fuelType;

    @NotNull(message = "Fleet size is required")
    @Min(value = 0, message = "Fleet size must be at least 0")
    private Double fleetSize;

    @NotNull(message = "Average km per vehicle is required")
    @Min(value = 0, message = "Average km per vehicle must be at least 0")
    private Double averageKmPerVehicle;

    @NotNull(message = "Fuel economy is required")
    @Positive(message = "Fuel economy must be positive")
    private Double fuelEconomyLPer100Km;

    @NotNull(message = "EV share is required")
    @DecimalMin(value = "0.0", message = "EV share must be at least 0")
    @DecimalMax(value = "1.0", message = "EV share cannot exceed 1")
    private Double evShare;

    @NotNull(message = "EV energy consumption is required")
    @Min(value = 0, message = "EV energy consumption must be at least 0")
    private Double evKWhPer100Km;
}
