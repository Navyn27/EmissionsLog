package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.dtos;

import com.navyn.emissionlog.Enums.Metrics.AreaUnits;
import com.navyn.emissionlog.Enums.Metrics.BiomassDensityUnit;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CropRotationMitigationDto {
    
    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be 1900 or later")
    @Max(value = 2100, message = "Year must be 2100 or earlier")
    private Integer year;
    
    @NotNull(message = "Cropland under crop rotation is required")
    @DecimalMin(value = "0.0", message = "Cropland under crop rotation must be at least 0")
    private Double croplandUnderCropRotation;
    
    @NotNull(message = "Cropland area unit is required")
    private AreaUnits croplandAreaUnit; // Unit for cropland area (standard: ha)
    
    @NotNull(message = "Aboveground biomass is required")
    @DecimalMin(value = "0.0", message = "Aboveground biomass must be at least 0")
    private Double abovegroundBiomass;
    
    @NotNull(message = "Aboveground biomass unit is required")
    private BiomassDensityUnit abovegroundBiomassUnit; // Unit for AGB (standard: tonnes DM/ha)
    
    @NotNull(message = "Increased biomass is required")
    @DecimalMin(value = "0.0", message = "Increased biomass must be at least 0")
    private Double increasedBiomass;
    
    @NotNull(message = "Increased biomass unit is required")
    private BiomassDensityUnit increasedBiomassUnit; // Unit for increased biomass (standard: tonnes DM/ha)
}
