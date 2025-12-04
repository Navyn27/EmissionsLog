package com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.dtos;

import com.navyn.emissionlog.Enums.Metrics.EmissionsKilotonneUnit;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class EPRPlasticWasteMitigationDto {
    
    @NotNull(message = "Year is required")
    private Integer year;
    
    @NotNull(message = "BAU Solid Waste Emissions is required")
    @Positive(message = "BAU Solid Waste Emissions must be positive")
    private Double bauSolidWasteEmissions; // ktCO2eq
    
    @NotNull(message = "BAU solid waste emissions unit is required")
    private EmissionsKilotonneUnit bauSolidWasteEmissionsUnit;
    
    @NotNull(message = "Plastic Waste Growth Factor is required")
    @Positive(message = "Plastic Waste Growth Factor must be positive")
    private Double plasticWasteGrowthFactor; // multiplier (e.g., 1.05 for 5% growth)
    
    @NotNull(message = "Recycling Rate (with EPR) is required")
    @Positive(message = "Recycling Rate must be positive")
    private Double recyclingRateWithEPR; // percentage as decimal (e.g., 0.15 for 15%)
    
    // Optional: Base plastic waste - required for first year, optional for subsequent years
    @Positive(message = "Plastic Waste Base must be positive if provided")
    private Double plasticWasteBaseTonnesPerYear; // t/year (optional, used for first year)
}
