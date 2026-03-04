package com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.dto.parameters;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ElectricityParameterResponseDto {
    private UUID id;
    private Double fuelConsumption; // Per capita fuel consumption by a baseline device MWh
    private Double emissionFactor; // CO2 emission factor tCO2/MWh
    private Double efficiency; // Baseline efficiency %
    private Double size; // HH size no unit
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

