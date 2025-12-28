package com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ISWMMitigationDto {
    
    @NotNull(message = "Year is required")
    private Integer year;
    
    @NotNull(message = "Waste Processed is required")
    private Double wasteProcessed; // tonnes
    
    @NotNull(message = "Project Intervention is required")
    private UUID projectInterventionId; // Foreign key to Intervention table
    
    // For Excel upload - intervention name (will be converted to UUID)
    private String projectInterventionName; // Used for Excel import
}
