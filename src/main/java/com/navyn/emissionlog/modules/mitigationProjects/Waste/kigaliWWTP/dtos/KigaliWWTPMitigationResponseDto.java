package com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KigaliWWTPMitigationResponseDto {
    private UUID id;
    private Integer year;
    private Double annualWastewaterTreated;
    private Double methanePotential;
    private Double co2ePerM3Wastewater;
    private Double annualEmissionsReductionTonnes;
    private Double annualEmissionsReductionKilotonnes;
    private Double adjustedBauEmissionMitigation;
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

