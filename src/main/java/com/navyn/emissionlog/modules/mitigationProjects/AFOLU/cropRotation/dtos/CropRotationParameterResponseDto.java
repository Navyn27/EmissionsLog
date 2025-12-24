package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CropRotationParameterResponseDto {
    private UUID id;
    private Double aboveGroundBiomass; // AboveGround biomass (ABG), tonnes DM/ha
    private Double ratioOfBelowGroundBiomass; // Ratio of belowground biomass BGB to AGB
    private Double carbonContent; // Carbon content in dry increased biomass
    private Double carbonToC02; // C to CO2 conversion
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

