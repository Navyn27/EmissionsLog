package com.navyn.emissionlog.modules.mitigationProjects.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MitigationDashboardYearDto {
    private Integer year;
    private Double bauScenarioKtCO2e; // BAU for this year, sum across all sectors
    private Double afoluMitigationKtCO2e; // AFOLU sector mitigation for this year
    private Double wasteMitigationKtCO2e; // WASTE sector mitigation for this year
    private Double energyMitigationKtCO2e; // ENERGY sector mitigation for this year
    private Double ippuMitigationKtCO2e; // IPPU sector mitigation for this year
    private Double transportMitigationKtCO2e; // TRANSPORT sector mitigation for this year
    private Double totalMitigationKtCO2e; // Sum of all sectors for this year
    private Double mitigationScenarioKtCO2e; // BAU - Total Mitigation for this year
}

