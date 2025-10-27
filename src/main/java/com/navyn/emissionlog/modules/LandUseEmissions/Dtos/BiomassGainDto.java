package com.navyn.emissionlog.modules.LandUseEmissions.Dtos;

import com.navyn.emissionlog.Enums.LandUse.LandCategory;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class BiomassGainDto {
    
    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be 1900 or later")
    @Max(value = 2100, message = "Year must be 2100 or earlier")
    private Integer year;
    
    @NotNull(message = "Land category is required")
    private LandCategory landCategory;
    
    @NotNull(message = "Forest area is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Forest area must be greater than 0")
    private double forestArea;
}
