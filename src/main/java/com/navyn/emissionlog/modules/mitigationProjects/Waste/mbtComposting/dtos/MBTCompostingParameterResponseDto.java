package com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class MBTCompostingParameterResponseDto {
    private UUID id;
    private Double emissionFactor; // tCO2eq/ton
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

