package com.navyn.emissionlog.modules.LandUseEmissions.Dtos;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RewettedMineralWetlandsDto {
    
    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be 1900 or later")
    @Max(value = 2100, message = "Year must be 2100 or earlier")
    private Integer year;
    
    @NotNull(message = "Area of rewetted wetlands is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Area of rewetted wetlands must be greater than 0")
    private double areaOfRewettedWetlands;
}
