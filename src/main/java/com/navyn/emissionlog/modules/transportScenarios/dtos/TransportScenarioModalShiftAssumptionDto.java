package com.navyn.emissionlog.modules.transportScenarios.dtos;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class TransportScenarioModalShiftAssumptionDto {

    private UUID id; // Nullable for create

    @NotNull(message = "Scenario ID is required")
    private UUID scenarioId;

    @NotNull(message = "Year is required")
    @Min(value = 1990, message = "Year must be at least 1990")
    @Max(value = 2100, message = "Year cannot exceed 2100")
    private Integer year;

    @NotNull(message = "Passenger-km for motorcycle BAU is required")
    @Min(value = 0, message = "Passenger-km for motorcycle BAU must be at least 0")
    private Double passengerKmMotorcycleBau;

    @NotNull(message = "Passenger-km for car BAU is required")
    @Min(value = 0, message = "Passenger-km for car BAU must be at least 0")
    private Double passengerKmCarBau;

    @NotNull(message = "Passenger-km for bus BAU is required")
    @Min(value = 0, message = "Passenger-km for bus BAU must be at least 0")
    private Double passengerKmBusBau;

    @NotNull(message = "Emission factor for motorcycle is required")
    @Min(value = 0, message = "Emission factor for motorcycle must be at least 0")
    private Double emissionFactorMotorcycle_gPerPassKm;

    @NotNull(message = "Emission factor for car is required")
    @Min(value = 0, message = "Emission factor for car must be at least 0")
    private Double emissionFactorCar_gPerPassKm;

    @NotNull(message = "Emission factor for bus is required")
    @Min(value = 0, message = "Emission factor for bus must be at least 0")
    private Double emissionFactorBus_gPerPassKm;

    @NotNull(message = "Shift fraction from motorcycle to bus is required")
    @DecimalMin(value = "0.0", message = "Shift fraction from motorcycle to bus must be at least 0")
    @DecimalMax(value = "1.0", message = "Shift fraction from motorcycle to bus cannot exceed 1")
    private Double shiftFractionMotorcycleToBus;

    @NotNull(message = "Shift fraction from car to bus is required")
    @DecimalMin(value = "0.0", message = "Shift fraction from car to bus must be at least 0")
    @DecimalMax(value = "1.0", message = "Shift fraction from car to bus cannot exceed 1")
    private Double shiftFractionCarToBus;
}
