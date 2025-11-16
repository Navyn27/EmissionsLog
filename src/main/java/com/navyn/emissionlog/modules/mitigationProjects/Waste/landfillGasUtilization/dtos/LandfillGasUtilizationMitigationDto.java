package com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class LandfillGasUtilizationMitigationDto {
    
    @NotNull(message = "Year is required")
    private Integer year;
    
    @NotNull(message = "BAU Solid Waste Emissions is required")
    @Positive(message = "BAU Solid Waste Emissions must be positive")
    private Double bauSolidWasteEmissions; // ktCO2eq
    
    @NotNull(message = "Project Reduction (40% Efficiency) is required")
    @Positive(message = "Project Reduction must be positive")
    private Double projectReduction40PercentEfficiency; // ktCO2eq
    
    @NotNull(message = "BAU Grand Total is required")
    @Positive(message = "BAU Grand Total must be positive")
    private Double bauGrandTotal; // ktCO2eq
}
