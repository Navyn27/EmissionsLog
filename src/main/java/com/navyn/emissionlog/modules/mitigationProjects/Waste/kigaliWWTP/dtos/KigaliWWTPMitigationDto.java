package com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.dtos;

import com.navyn.emissionlog.Enums.Metrics.VolumePerTimeUnit;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.UUID;

@Data
public class KigaliWWTPMitigationDto {
    
    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be 1900 or later")
    @Max(value = 2100, message = "Year must be 2100 or earlier")
    private Integer year;
    
    @NotNull(message = "Annual Wastewater Treated is required")
    @Positive(message = "Annual Wastewater Treated must be positive")
    private Double annualWastewaterTreated; // m³/year
    
    @NotNull(message = "Annual Wastewater Treated unit is required")
    private VolumePerTimeUnit annualWastewaterTreatedUnit;
    
    @NotNull(message = "Project Intervention is required")
    private UUID projectInterventionId; // Foreign key to Intervention table
    
    // For Excel upload - intervention name (will be converted to UUID)
    private String projectInterventionName; // Used for Excel import
}
