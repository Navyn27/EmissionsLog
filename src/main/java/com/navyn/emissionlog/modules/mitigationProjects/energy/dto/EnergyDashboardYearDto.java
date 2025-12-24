package com.navyn.emissionlog.modules.mitigationProjects.energy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnergyDashboardYearDto {
    private Integer year;
    private Double cookstove;
    private Double rooftop;
    private Double lightbulb;
    private Double waterheat;
    private Double totalMitigationKtCO2e;
}

