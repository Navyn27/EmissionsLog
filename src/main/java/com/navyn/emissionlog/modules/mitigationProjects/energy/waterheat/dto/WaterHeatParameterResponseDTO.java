package com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WaterHeatParameterResponseDTO {
    private UUID id;
    private Integer deltaTemperature;
    private Integer specificHeatWater;
    private Double gridEmissionFactor; // tCO2/MWh
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

