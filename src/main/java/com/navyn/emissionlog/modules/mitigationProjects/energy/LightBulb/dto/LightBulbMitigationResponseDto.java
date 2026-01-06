package com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class LightBulbMitigationResponseDto {
    private UUID id;
    private int year;
    private double totalInstalledBulbsPerYear;
    private double reductionCapacityPerBulb;
    private double totalReductionPerYear;
    private double netGhGMitigationAchieved; // in tCO2e
    private double scenarioGhGMitigationAchieved; // in ktCO2e
    private double adjustedBauEmissionMitigation; // in ktCO2e
    private InterventionInfo projectIntervention;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    public static class InterventionInfo {
        private UUID id;
        private String name;

        public InterventionInfo(UUID id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}

