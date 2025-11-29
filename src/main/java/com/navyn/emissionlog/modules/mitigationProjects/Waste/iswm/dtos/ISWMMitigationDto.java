package com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.dtos;

import com.navyn.emissionlog.Enums.Metrics.EmissionsUnit;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ISWMMitigationDto {
    
    @NotNull(message = "Year is required")
    private Integer year;
    
    @NotNull(message = "BAU Emissions is required")
    private Double bauEmissions; // tCO₂e
    
    @NotNull(message = "BAU emissions unit is required")
    private EmissionsUnit bauEmissionsUnit;
    
    @NotNull(message = "Annual Reduction is required")
    private Double annualReduction; // tCO₂e
    
    @NotNull(message = "Annual reduction unit is required")
    private EmissionsUnit annualReductionUnit;
}
