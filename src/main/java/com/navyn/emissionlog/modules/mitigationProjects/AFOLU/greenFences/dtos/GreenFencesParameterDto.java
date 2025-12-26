package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.greenFences.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class GreenFencesParameterDto {

    @NotNull(message = "Carbon content is required")
    @DecimalMin(value = "0.0", message = "Carbon content must be at least 0")
    private Double carbonContent; // Carbon content in Abs increased biomass

    @NotNull(message = "Ratio of belowground biomass is required")
    @DecimalMin(value = "0.0", message = "Ratio of belowground biomass must be at least 0")
    private Double ratioOfBelowGroundBiomass; // Ratio of belowground biomass BGB to AGB

    @NotNull(message = "Carbon content in dry woods is required")
    @DecimalMin(value = "0.0", message = "Carbon content in dry woods must be at least 0")
    private Double carbonContentInDryWoods; // Carbon content in dry increased biomass

    @NotNull(message = "C to CO2 conversion is required")
    @DecimalMin(value = "0.0", message = "C to CO2 conversion must be at least 0")
    private Double carbonToC02; // C to CO2 conversion
    // Optional intervention reference
    private UUID interventionId;

    // Temporary field for Excel import - intervention name (will be converted to interventionId)
    private String interventionName;
}

