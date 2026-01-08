package com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.dto.parameters;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class LGPParameterResponseDto {
    private UUID id;
    private Double netCalorificValue; // Net calorific value TJ/t
    private Double emissionFactor; // CO2 emission factor TJ/t
    private Double adjustedEmissionFactor; // Non CO2 emission factor TJ/t
    private Double fuelConsumption; // Per capita fuel consumption by a baseline device tonnes
    private Double efficiency; // Baseline efficiency %
    private Double size; // HH size no unit
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

