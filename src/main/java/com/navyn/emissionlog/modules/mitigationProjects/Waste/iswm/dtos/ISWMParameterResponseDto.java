package com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ISWMParameterResponseDto {
    private UUID id;
    private Double degradableOrganicFraction; // percentage (0-100)
    private Double landfillAvoidance; // kgCO₂e/tonne
    private Double compostingEF; // kgCO₂e/tonne of DOF
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

