package com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.dtos;

import com.navyn.emissionlog.Enums.Metrics.EmissionsKilotonneUnit;
import com.navyn.emissionlog.Enums.Metrics.MassPerYearUnit;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class WasteToEnergyMitigationDto {
    
    @NotNull(message = "Year is required")
    private Integer year;
    
    @NotNull(message = "Waste to WtE is required")
    @Positive(message = "Waste to WtE must be positive")
    private Double wasteToWtE; // t/year
    
    @NotNull(message = "Waste to WtE unit is required")
    private MassPerYearUnit wasteToWtEUnit;
    
    @NotNull(message = "BAU Emissions (Solid Waste) is required")
    @Positive(message = "BAU Emissions must be positive")
    private Double bauEmissionsSolidWaste; // ktCO2e
    
    @NotNull(message = "BAU emissions unit is required")
    private EmissionsKilotonneUnit bauEmissionsUnit;
}
