package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandsRewetting.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WetlandsRewettingMitigationResponseDto {
    private UUID id;
    private Integer year;
    private Double areaRewettedMineralWetlandsHa;
    private SwapInfo swap;
    private InterventionInfo intervention;
    private Double ch4EmissionsKilotonnesPerYear;
    private Double emissionsCo2eTonnesPerYear;
    private Double sequestrationTonnesC;
    private Double co2eValueOfSequestrationTonnes;
    private Double totalMitigationTonnesCo2e;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SwapInfo {
        private UUID id;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InterventionInfo {
        private UUID id;
        private String name;
    }
}
