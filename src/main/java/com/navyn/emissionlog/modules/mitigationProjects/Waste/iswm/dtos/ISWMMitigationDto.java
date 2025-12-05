package com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.dtos;

import com.navyn.emissionlog.Enums.Metrics.EmissionsUnit;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ISWMMitigationDto {
    
    @NotNull(message = "Year is required")
    private Integer year;
    
    @NotNull(message = "Waste Processed is required")
    private Double wasteProcessed; // tonnes
    
    @NotNull(message = "Degradable Organic Fraction is required")
    private Double degradableOrganicFraction; // percentage (0-100)
    
    @NotNull(message = "Landfill Avoidance is required")
    private Double landfillAvoidance; // kgCO₂e/tonne
    
    @NotNull(message = "Composting Emission Factor is required")
    private Double compostingEF; // kgCO₂e/tonne of DOF
    
    @NotNull(message = "BAU Emission is required")
    private Double bauEmission; // tCO₂e
    
    @NotNull(message = "BAU emission unit is required")
    private EmissionsUnit bauEmissionUnit;
}
