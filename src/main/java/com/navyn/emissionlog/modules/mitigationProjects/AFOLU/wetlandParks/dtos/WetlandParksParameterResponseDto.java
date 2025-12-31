package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class WetlandParksParameterResponseDto {
    private UUID id;
    private Double conversionM3ToTonnesDM; // Conversion m3 to tonnes dry matter (DM)
    private Double ratioOfBelowGroundBiomass; // Ratio of belowground biomass BGB to AGB
    private Double carbonContentDryWood; // Carbon content in dry wood
    private Double carbonToC02; // C to CO2 conversion
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

