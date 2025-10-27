package com.navyn.emissionlog.modules.agricultureEmissions.dtos.AgriculturalLand;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UreaEmissionsDto {
    
    @NotBlank(message = "Fertilizer name is required")
    @Size(max = 255, message = "Fertilizer name cannot exceed 255 characters")
    private String fertilizerName;
    
    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be 1900 or later")
    @Max(value = 2100, message = "Year must be 2100 or earlier")
    private int year;
    
    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Quantity must be greater than 0")
    private double qty;
}
