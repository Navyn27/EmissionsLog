package com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.dtos;

import com.navyn.emissionlog.Enums.Metrics.VolumePerTimeUnit;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.constants.WWTPProjectPhase;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class KigaliWWTPMitigationDto {
    
    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be 1900 or later")
    @Max(value = 2100, message = "Year must be 2100 or earlier")
    private Integer year;
    
    @NotNull(message = "Project phase is required")
    private WWTPProjectPhase projectPhase;
    
    @NotNull(message = "Phase capacity per day is required")
    @DecimalMin(value = "0.0", message = "Phase capacity must be at least 0")
    private Double phaseCapacityPerDay; // m³/day
    
    @NotNull(message = "Phase capacity unit is required")
    private VolumePerTimeUnit phaseCapacityUnit;
    
    @NotNull(message = "Connected households is required")
    @DecimalMin(value = "0.0", message = "Connected households must be at least 0")
    private Double connectedHouseholds; // Total number of connected households
    
    @NotNull(message = "Connected households percentage is required")
    @DecimalMin(value = "0.0", message = "Connected households percentage must be at least 0")
    @DecimalMax(value = "1.0", message = "Connected households percentage must be at most 1.0 (100%)")
    private Double connectedHouseholdsPercentage; // Percentage as decimal (e.g., 0.65 for 65%)
}
