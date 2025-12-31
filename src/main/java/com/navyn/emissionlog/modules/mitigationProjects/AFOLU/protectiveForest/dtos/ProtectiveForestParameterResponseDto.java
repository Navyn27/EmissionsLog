package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.protectiveForest.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ProtectiveForestParameterResponseDto {
    private UUID id;
    private Double conversationM3ToTonnes; // Conversion m3 to tonnes dry matter (DM)
    private Double ratioOfBelowGroundBiomass; // Ratio of belowground biomass BGB to AGB
    private Double carbonContent; // Carbon content in dry increased biomass
    private Double carbonToC02; // C to CO2 conversion
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
