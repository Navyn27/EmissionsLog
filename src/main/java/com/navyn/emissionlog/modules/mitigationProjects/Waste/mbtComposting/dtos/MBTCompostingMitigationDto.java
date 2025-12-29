package com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.UUID;

@Data
public class MBTCompostingMitigationDto {
    
    @NotNull(message = "Year is required")
    private Integer year;
    
    @NotNull(message = "Organic Waste Treated (tons/year) is required")
    @Positive(message = "Organic Waste Treated must be positive")
    private Double organicWasteTreatedTonsPerYear; // tons/year
    
    @NotNull(message = "Project Intervention is required")
    private UUID projectInterventionId; // Foreign key to Intervention table
    
    // For Excel upload - intervention name (will be converted to UUID)
    private String projectInterventionName; // Used for Excel import
}
