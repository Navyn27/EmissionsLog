package com.navyn.emissionlog.modules.agricultureEmissions.dtos.AgriculturalLand;

import com.navyn.emissionlog.Enums.Agriculture.LimingMaterials;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class LimingEmissionsDto {
    
    @NotNull(message = "Liming material type is required")
    private LimingMaterials material;
    
    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be 1900 or later")
    @Max(value = 2100, message = "Year must be 2100 or earlier")
    private int year;
    
    @NotNull(message = "CaCO3 quantity is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "CaCO3 quantity must be greater than 0")
    private double CaCO3Qty;
}
