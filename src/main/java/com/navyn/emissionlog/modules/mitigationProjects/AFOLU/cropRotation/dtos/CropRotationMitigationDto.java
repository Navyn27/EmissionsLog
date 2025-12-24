package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.dtos;

import com.navyn.emissionlog.Enums.Metrics.AreaUnits;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.UUID;

@Data
public class CropRotationMitigationDto {
    
    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be 1900 or later")
    @Max(value = 2100, message = "Year must be 2100 or earlier")
    private Integer year;
    
    @NotNull(message = "Cropland under crop rotation is required")
    @DecimalMin(value = "0.0", message = "Cropland under crop rotation must be at least 0")
    private Double croplandUnderCropRotation;


    @NotNull(message = "Increased biomass")
    @DecimalMin(value = "0.0", message = "increased biomass must be at least 0")
    private Double increasedBiomass;


    @NotNull(message = "Cropland area unit is required")
    private AreaUnits croplandAreaUnit; // Unit for cropland area (standard: ha)
    
    // Optional intervention reference
    private UUID interventionId;
    
    // Temporary field for Excel import - intervention name (will be converted to interventionId)
    private String interventionName;
}
