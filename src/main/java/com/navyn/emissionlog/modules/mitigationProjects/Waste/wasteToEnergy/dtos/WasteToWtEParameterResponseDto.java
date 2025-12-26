package com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class WasteToWtEParameterResponseDto {
    private UUID id;
    private Double netEmissionFactor; // tCO2eq/t
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

