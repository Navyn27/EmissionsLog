package com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

/**
 * DTO for creating/updating ISWM Mitigation records
 * 
 * Note: This DTO only contains user inputs. Parameters (degradableOrganicFraction, 
 * landfillAvoidance, compostingEF) are fetched from ISWMParameter (latest active).
 * BAU value is fetched from BAU table (sector: WASTE, same year).
 * 
 * For Excel uploads, projectInterventionName can be provided instead of projectInterventionId.
 */
@Data
public class ISWMMitigationDto {
    
    @NotNull(message = "Year is required")
    private Integer year;
    
    @NotNull(message = "Waste Processed is required")
    private Double wasteProcessed; // tonnes
    
    @NotNull(message = "Project Intervention ID is required")
    private UUID projectInterventionId;
    
    // For Excel upload - intervention name (will be converted to ID during processing)
    private String projectInterventionName;
}
