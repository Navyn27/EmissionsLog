package com.navyn.emissionlog.modules.mitigationProjects.IPPU.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IPPUDashboardYearDto {
    private Integer year;
    private Map<String, Double> fGasValues; // F-gas name -> mitigation value for this year
    private Double totalMitigationKtCO2e;
}
