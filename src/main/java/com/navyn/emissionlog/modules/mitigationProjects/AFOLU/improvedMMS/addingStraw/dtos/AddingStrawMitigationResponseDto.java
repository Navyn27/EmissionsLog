package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddingStrawMitigationResponseDto {
    private UUID id;
    private Integer year;
    private Integer numberOfCows;
    private Double ch4EmissionsAddingStraw; // tonnes CO2e
    private Double ch4ReductionAddingStraw; // tonnes CO2e
    private Double mitigatedCh4EmissionsKilotonnes; // ktCO2e/year
    private Double adjustmentMitigation; // Kilotonnes CO2
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

