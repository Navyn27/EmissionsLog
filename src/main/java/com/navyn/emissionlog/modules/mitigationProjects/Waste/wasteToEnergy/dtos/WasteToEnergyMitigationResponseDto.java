package com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WasteToEnergyMitigationResponseDto {
    private UUID id;
    private Integer year;
    private Double wasteToWtE; // t/year
    private Double ghgReductionTonnes; // tCO2eq
    private Double ghgReductionKilotonnes; // ktCO2eq
    private Double adjustedEmissionsWithWtE; // ktCO2e
    private InterventionInfo projectIntervention;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InterventionInfo {
        private UUID id;
        private String name;
    }
}

