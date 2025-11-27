package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.greenFences.dtos;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class GreenFencesMitigationDto {
    
    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be 1900 or later")
    @Max(value = 2100, message = "Year must be 2100 or earlier")
    private Integer year;
    
    @NotNull(message = "Number of households with 10m2 fence is required")
    @DecimalMin(value = "0.0", message = "Number of households with 10m2 fence must be at least 0")
    private Double numberOfHouseholdsWith10m2Fence;
    
    @NotNull(message = "AGB of 10m2 live fence is required")
    @DecimalMin(value = "0.0", message = "AGB of 10m2 live fence must be at least 0")
    private Double agbOf10m2LiveFence; // tonnes DM
}
