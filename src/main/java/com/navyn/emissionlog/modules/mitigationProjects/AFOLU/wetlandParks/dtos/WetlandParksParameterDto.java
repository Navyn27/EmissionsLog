package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WetlandParksParameterDto {

    @NotNull(message = "Conversion m3 to tonnes DM is required")
    @DecimalMin(value = "0.0", message = "Conversion m3 to tonnes DM must be at least 0")
    private Double conversionM3ToTonnesDM; // Conversion m3 to tonnes dry matter (DM)

    @NotNull(message = "Ratio of belowground biomass is required")
    @DecimalMin(value = "0.0", message = "Ratio of belowground biomass must be at least 0")
    private Double ratioOfBelowGroundBiomass; // Ratio of belowground biomass BGB to AGB

    @NotNull(message = "Carbon content in dry wood is required")
    @DecimalMin(value = "0.0", message = "Carbon content in dry wood must be at least 0")
    private Double carbonContentDryWood; // Carbon content in dry wood

    @NotNull(message = "C to CO2 conversion is required")
    @DecimalMin(value = "0.0", message = "C to CO2 conversion must be at least 0")
    private Double carbonToC02; // C to CO2 conversion
}

