package com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.dtos;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.constants.OperationStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class MBTCompostingMitigationDto {
    
    @NotNull(message = "Year is required")
    private Integer year;
    
    @NotNull(message = "Operation Status is required")
    private OperationStatus operationStatus;
    
    @NotNull(message = "Organic Waste Treated (tons/day) is required")
    @Positive(message = "Organic Waste Treated must be positive")
    private Double organicWasteTreatedTonsPerDay; // tons/day
    
    @NotNull(message = "BAU Emission Biological Treatment is required")
    @Positive(message = "BAU Emission Biological Treatment must be positive")
    private Double bauEmissionBiologicalTreatment; // ktCO2eq
}
