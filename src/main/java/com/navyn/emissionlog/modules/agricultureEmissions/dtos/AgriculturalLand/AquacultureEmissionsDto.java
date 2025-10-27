package com.navyn.emissionlog.modules.agricultureEmissions.dtos.AgriculturalLand;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AquacultureEmissionsDto {
    
    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be 1900 or later")
    @Max(value = 2100, message = "Year must be 2100 or earlier")
    private int year;
    
    @Size(max = 500, message = "Activity description cannot exceed 500 characters")
    private String activityDesc;
    
    @NotNull(message = "Fish production amount is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Fish production cannot be negative")
    private double fishProduction;
}
