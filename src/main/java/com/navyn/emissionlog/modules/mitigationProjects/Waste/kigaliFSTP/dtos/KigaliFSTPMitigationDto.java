package com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.dtos;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.UUID;

@Data
public class KigaliFSTPMitigationDto {
    
    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be 1900 or later")
    @Max(value = 2100, message = "Year must be 2100 or earlier")
    private Integer year;
    
    @NotNull(message = "Annual sludge treated is required")
    @Positive(message = "Annual sludge treated must be positive")
    private Double annualSludgeTreated; // m³/year
    
    @NotNull(message = "Project Intervention is required")
    private UUID projectInterventionId; // Foreign key to Intervention table
    
    // For Excel upload - intervention name (will be converted to UUID)
    private String projectInterventionName; // Used for Excel import
}
