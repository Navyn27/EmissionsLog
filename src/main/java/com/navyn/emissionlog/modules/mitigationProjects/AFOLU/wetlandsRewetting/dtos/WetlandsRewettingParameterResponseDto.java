package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandsRewetting.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class WetlandsRewettingParameterResponseDto {
    private UUID id;
    private Double ch4EmissionFactorPerHaPerYear;
    private Double gwpMethane;
    private Double carbonSequestrationFactorPerHaPerYear;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
