package com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EPRPlasticWasteMitigationResponseDto {
    private UUID id;
    private Integer year;
    private Double plasticWasteTonnesPerYear; // t/year
    private Double recycledPlasticWithoutEPRTonnesPerYear; // t/year
    private Double recycledPlasticWithEPRTonnesPerYear; // t/year
    private Double additionalRecyclingVsBAUTonnesPerYear; // t/year
    private Double ghgReductionTonnes; // tCO2eq
    private Double ghgReductionKilotonnes; // ktCO2eq
    private Double adjustedBauEmissionMitigation; // ktCO2e
    private InterventionInfo projectIntervention;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InterventionInfo {
        private UUID id;
        private String name;
    }
}

