package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.greenFences.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GreenFencesMitigationResponseDto {
    private UUID id;
    private Integer year;
    private Double cumulativeNumberOfHouseholds;
    private Double numberOfHouseholdsWith10m2Fence;
    private Double agbOf10m2LiveFence;
    private Double agbFenceBiomassCumulativeHouseholds;
    private Double agbPlusBgbCumulativeHouseholds;
    private Double mitigatedEmissionsKtCO2e;
    private Double adjustmentMitigation;
    private InterventionInfo intervention;
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

