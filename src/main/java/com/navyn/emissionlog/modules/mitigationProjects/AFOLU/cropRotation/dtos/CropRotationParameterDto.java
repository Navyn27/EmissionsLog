package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CropRotationParameterDto {

    @NotNull(message = "Aboveground biomass is required")
    @DecimalMin(value = "0.0", message = "Aboveground biomass must be at least 0")
    private Double aboveGroundBiomass; // AboveGround biomass (ABG), tonnes DM/ha

    @NotNull(message = "Ratio of belowground biomass is required")
    @DecimalMin(value = "0.0", message = "Ratio of belowground biomass must be at least 0")
    private Double ratioOfBelowGroundBiomass; // Ratio of belowground biomass BGB to AGB

    @NotNull(message = "Carbon content is required")
    @DecimalMin(value = "0.0", message = "Carbon content must be at least 0")
    private Double carbonContent; // Carbon content in dry increased biomass

    @NotNull(message = "C to CO2 conversion is required")
    @DecimalMin(value = "0.0", message = "C to CO2 conversion must be at least 0")
    private Double carbonToC02; // C to CO2 conversion
}

