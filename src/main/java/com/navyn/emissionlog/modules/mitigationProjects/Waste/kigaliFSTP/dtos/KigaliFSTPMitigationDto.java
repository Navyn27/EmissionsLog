package com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.dtos;

import com.navyn.emissionlog.Enums.Metrics.VolumePerTimeUnit;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.constants.ProjectPhase;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class KigaliFSTPMitigationDto {
    
    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be 1900 or later")
    @Max(value = 2100, message = "Year must be 2100 or earlier")
    private Integer year;
    
    @NotNull(message = "Project Phase is required")
    private ProjectPhase projectPhase;
    
    @NotNull(message = "Phase capacity per day is required")
    @DecimalMin(value = "0.0", message = "Phase capacity must be at least 0")
    private Double phaseCapacityPerDay; // m³/day
    
    @NotNull(message = "Phase capacity unit is required")
    private VolumePerTimeUnit phaseCapacityUnit;
    
    @NotNull(message = "Plant operational efficiency is required")
    @DecimalMin(value = "0.0", message = "Plant operational efficiency must be at least 0")
    @DecimalMax(value = "1.0", message = "Plant operational efficiency must be at most 1.0 (100%)")
    private Double plantOperationalEfficiency; // Efficiency as decimal (e.g., 0.85 for 85%)
}
