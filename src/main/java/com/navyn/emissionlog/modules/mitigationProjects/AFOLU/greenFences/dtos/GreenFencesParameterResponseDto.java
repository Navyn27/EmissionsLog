package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.greenFences.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class GreenFencesParameterResponseDto {
    private UUID id;
    private Double carbonContent; // Carbon content in Abs increased biomass
    private Double ratioOfBelowGroundBiomass; // Ratio of belowground biomass BGB to AGB
    private Double carbonContentInDryWoods; // Carbon content in dry increased biomass
    private Double carbonToC02; // C to CO2 conversion
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

