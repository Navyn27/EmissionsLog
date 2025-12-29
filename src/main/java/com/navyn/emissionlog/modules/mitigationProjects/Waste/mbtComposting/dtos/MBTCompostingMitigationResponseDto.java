package com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MBTCompostingMitigationResponseDto {
    private UUID id;
    private Integer year;
    private Double organicWasteTreatedTonsPerYear;
    private Double estimatedGhgReductionTonnesPerYear;
    private Double estimatedGhgReductionKilotonnesPerYear;
    private Double adjustedBauEmissionBiologicalTreatment;
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

