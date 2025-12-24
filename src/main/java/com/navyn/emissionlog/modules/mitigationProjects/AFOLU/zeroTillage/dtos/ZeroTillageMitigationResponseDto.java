package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.zeroTillage.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZeroTillageMitigationResponseDto {
    private UUID id;
    private Integer year;
    private Double areaUnderZeroTillage;
    private Double totalCarbonIncreaseInSoil;
    private Double emissionsSavings;
    private Double ureaApplied;
    private Double emissionsFromUrea;
    private Double ghgEmissionsSavings;
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

