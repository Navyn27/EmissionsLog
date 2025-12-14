package com.navyn.emissionlog.modules.mitigationProjects.IPPU.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IPPUDashboardSummaryDto {
    private Integer startingYear;
    private Integer endingYear;
    private Double totalMitigationKtCO2e;
    private Double totalBAUKtCO2e;
    private Double totalReducedEmissionKtCO2e;
    private Map<String, Double> totalsByFGas; // F-gas name -> total mitigation
    private Map<String, Double> percentagesByFGas; // F-gas name -> percentage contribution
}
