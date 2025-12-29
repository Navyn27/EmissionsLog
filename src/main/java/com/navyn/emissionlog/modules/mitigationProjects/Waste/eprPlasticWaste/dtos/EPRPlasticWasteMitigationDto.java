package com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.dtos;

import com.navyn.emissionlog.Enums.Metrics.MassPerYearUnit;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.UUID;

@Data
public class EPRPlasticWasteMitigationDto {
    
    @NotNull(message = "Year is required")
    private Integer year;
    
    @NotNull(message = "Plastic Waste (t/year) is required")
    @Positive(message = "Plastic Waste must be positive")
    private Double plasticWasteTonnesPerYear; // t/year
    
    @NotNull(message = "Plastic Waste unit is required")
    private MassPerYearUnit plasticWasteTonnesPerYearUnit;
    
    @NotNull(message = "Project Intervention is required")
    private UUID projectInterventionId; // Foreign key to Intervention table
    
    // For Excel upload - intervention name (will be converted to UUID)
    private String projectInterventionName; // Used for Excel import
}
