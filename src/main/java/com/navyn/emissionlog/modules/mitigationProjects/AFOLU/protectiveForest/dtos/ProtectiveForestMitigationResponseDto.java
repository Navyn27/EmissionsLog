package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.protectiveForest.dtos;

import com.navyn.emissionlog.Enums.Mitigation.ProtectiveForestCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProtectiveForestMitigationResponseDto {
    private UUID id;
    private Integer year;
    private ProtectiveForestCategory category;
    private Double areaPlanted;
    private Double agbCurrentYear;
    private Double cumulativeArea;
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
