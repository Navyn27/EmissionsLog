package com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.dtos;

import com.navyn.emissionlog.Enums.Metrics.MassPerYearUnit;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.UUID;

@Data
public class WasteToEnergyMitigationDto {
    
    @NotNull(message = "Year is required")
    private Integer year;
    
    @NotNull(message = "Waste to WtE is required")
    @Positive(message = "Waste to WtE must be positive")
    private Double wasteToWtE; // t/year
    
    @NotNull(message = "Waste to WtE unit is required")
    private MassPerYearUnit wasteToWtEUnit;
    
    @NotNull(message = "Project Intervention is required")
    private UUID projectInterventionId; // Foreign key to Intervention table
    
    // For Excel upload - intervention name (will be converted to UUID)
    private String projectInterventionName; // Used for Excel import
}
