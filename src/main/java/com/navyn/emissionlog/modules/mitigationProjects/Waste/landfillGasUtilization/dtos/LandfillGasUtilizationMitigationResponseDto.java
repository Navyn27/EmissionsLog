package com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LandfillGasUtilizationMitigationResponseDto {
    private UUID id;
    private Integer year;
    private Double ch4Captured;
    private Double ch4Destroyed;
    private Double equivalentCO2eReduction;
    private Double mitigationScenarioGrand;
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

