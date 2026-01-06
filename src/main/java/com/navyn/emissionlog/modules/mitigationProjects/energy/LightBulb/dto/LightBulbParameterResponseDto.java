package com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class LightBulbParameterResponseDto {
    private UUID id;
    private Double emissionFactor; // tCO2eq/t
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

