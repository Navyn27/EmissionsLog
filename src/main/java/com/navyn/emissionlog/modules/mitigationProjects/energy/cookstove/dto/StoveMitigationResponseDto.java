package com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.dto;

import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.enums.EStoveType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class StoveMitigationResponseDto {
    private UUID id;
    private EStoveType stoveType;
    private int year;
    private int unitsInstalled;
    private Double efficiency;
    private Double fuelConsumption;
    private Double projectEmission; // KtCO2e
    private InterventionInfo projectIntervention;
    private Double totalProjectEmission; // Sum of projectEmission for all records (not stored in DB)
    private Double emissionReduction; // BAU ENERGY - totalProjectEmission (not stored in DB)
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

