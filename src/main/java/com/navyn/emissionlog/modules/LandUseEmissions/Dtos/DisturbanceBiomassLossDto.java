package com.navyn.emissionlog.modules.LandUseEmissions.Dtos;

import com.navyn.emissionlog.Enums.LandUse.LandCategory;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class DisturbanceBiomassLossDto {
    
    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be 1900 or later")
    @Max(value = 2100, message = "Year must be 2100 or earlier")
    private Integer year;
    
    @NotNull(message = "Land category is required")
    private LandCategory landCategory;
    
    @NotNull(message = "Affected forest area is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Affected forest area must be greater than 0")
    private double affectedForestArea;
    
    @NotNull(message = "Area affected by disturbance is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Area affected by disturbance must be greater than 0")
    private double areaAffectedByDisturbance;
}
