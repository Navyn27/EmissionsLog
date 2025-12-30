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
    
    @NotNull(message = "Project Intervention ID is required")
    private UUID projectInterventionId;
    
    // For Excel upload - intervention name (will be converted to ID)
    private String projectInterventionName;
}
