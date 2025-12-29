package com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KigaliWWTPMitigationResponseDto {
    private UUID id;
    private Integer year;
    private Double annualWastewaterTreated; // m³/year
    private Double methanePotential; // kg CH4 per m³
    private Double co2ePerM3Wastewater; // kg CO2e per m³
    private Double annualEmissionsReductionTonnes; // tCO2e
    private Double annualEmissionsReductionKilotonnes; // ktCO2e
    private Double adjustedBauEmissionMitigation; // ktCO2e
    private InterventionInfo projectIntervention;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InterventionInfo {
        private UUID id;
        private String name;
    }
}

