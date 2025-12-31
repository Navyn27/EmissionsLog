package com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvoidedElectricityProductionResponseDTO {
    private UUID id;
    private Integer year;
    private Integer unitsInstalledThisYear;
    private Integer cumulativeUnitsInstalled;
    private Integer averageWaterHeat;
    private Double annualAvoidedElectricity;      // MWh
    private Double cumulativeAvoidedElectricity;  // MWh
    private Double netGhGMitigation;              // tCO2
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

