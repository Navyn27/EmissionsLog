package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ManureCoveringParameterResponseDto {
    private UUID id;
    private Double emissionPerCow; // N2O emissions per cow, tonnes CO2e
    private Double reduction; // Reduction of N2O emissions, %
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

