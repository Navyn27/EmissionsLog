package com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

@Data
public class LandfillGasParameterDto {
    
    @NotNull(message = "Destruction Efficiency Percentage is required")
    @Positive(message = "Destruction Efficiency Percentage must be positive")
    @DecimalMin(value = "0.0", message = "Destruction Efficiency Percentage must be between 0 and 100")
    @DecimalMax(value = "100.0", message = "Destruction Efficiency Percentage must be between 0 and 100")
    private Double destructionEfficiencyPercentage; // Percentage (0-100)
    
    @NotNull(message = "Global Warming Potential (CH₄) is required")
    @Positive(message = "Global Warming Potential (CH₄) must be positive")
    private Double globalWarmingPotentialCh4; // GWP for CH₄
}

