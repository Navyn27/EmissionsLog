package com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ISWMMitigationDto {
    
    @NotNull(message = "Year is required")
    private Integer year;
    
    @NotNull(message = "BAU Emissions is required")
    private Double bauEmissions; // ktCO2e
    
    @NotNull(message = "Annual Reduction is required")
    private Double annualReduction; // ktCO2e
}
