package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.dtos;

import com.navyn.emissionlog.Enums.Mitigation.WetlandTreeCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WetlandParksMitigationResponseDto {
    private UUID id;
    private Integer year;
    private WetlandTreeCategory treeCategory;
    private Double areaPlanted;
    private Double abovegroundBiomassAGB;
    private Double cumulativeArea;
    private Double previousYearAGB;
    private Double agbGrowth;
    private Double abovegroundBiomassGrowth;
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

