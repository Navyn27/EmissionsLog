package com.navyn.emissionlog.modules.transportScenarios.ElectricVehicle.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ElectricVehicleParameterResponseDto {
    private UUID id;
    private Double gridEmissionFactor; // tonne CO₂/MWh
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

