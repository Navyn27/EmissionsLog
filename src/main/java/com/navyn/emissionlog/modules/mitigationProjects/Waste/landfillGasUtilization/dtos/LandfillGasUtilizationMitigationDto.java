package com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.UUID;

@Data
public class LandfillGasUtilizationMitigationDto {
    
    @NotNull(message = "Year is required")
    private Integer year;
    
    @NotNull(message = "CH₄ Captured is required")
    @Positive(message = "CH₄ Captured must be positive")
    private Double ch4Captured; // CH₄ captured (user input)
    
    @NotNull(message = "Project Intervention is required")
    private UUID projectInterventionId; // Foreign key to Intervention table
    
    // For Excel upload - intervention name (will be converted to UUID)
    private String projectInterventionName; // Used for Excel import
}
