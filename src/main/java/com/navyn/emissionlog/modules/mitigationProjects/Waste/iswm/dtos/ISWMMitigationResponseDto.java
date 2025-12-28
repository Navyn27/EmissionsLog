package com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ISWMMitigationResponseDto {
    private UUID id;
    private Integer year;
    private Double wasteProcessed; // tonnes
    private Double dofDiverted; // tonnes
    private Double avoidedLandfill; // kgCO₂e
    private Double compostingEmissions; // kgCO₂e
    private Double netAnnualReduction; // ktCO₂e
    private Double mitigationScenarioEmission; // ktCO₂e
    private InterventionInfo projectIntervention;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InterventionInfo {
        private UUID id;
        private String name;
    }
}

