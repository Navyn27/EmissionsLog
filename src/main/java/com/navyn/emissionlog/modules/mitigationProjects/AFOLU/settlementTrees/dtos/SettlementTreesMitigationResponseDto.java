package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.settlementTrees.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettlementTreesMitigationResponseDto {
    private UUID id;
    private Integer year;
    private Double cumulativeNumberOfTrees;
    private Double numberOfTreesPlanted;
    private Double agbSingleTreeCurrentYear;
    private Double agbGrowth;
    private Double aboveGroundBiomassGrowth;
    private Double totalBiomass;
    private Double biomassCarbonIncrease;
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
