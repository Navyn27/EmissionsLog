package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CropRotationMitigationResponseDto {
    private UUID id;
    private Integer year;
    private Double croplandUnderCropRotation;
    private Double totalIncreasedBiomass;
    private Double biomassCarbonIncrease;
    private Double increasedBiomass;
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

