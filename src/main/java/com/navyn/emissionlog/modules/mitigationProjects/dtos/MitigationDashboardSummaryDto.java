package com.navyn.emissionlog.modules.mitigationProjects.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MitigationDashboardSummaryDto {
    private Double totalBAUKtCO2e; // Sum of BAU across all sectors for selected years
    private Double totalMitigationKtCO2e; // Sum of all sector mitigations
    private Double mitigationScenarioKtCO2e; // BAU - Total Mitigation
    private Double afoluMitigationKtCO2e; // AFOLU sector mitigation
    private Double wasteMitigationKtCO2e; // WASTE sector mitigation
    private Double energyMitigationKtCO2e; // ENERGY sector mitigation
    private Double ippuMitigationKtCO2e; // IPPU sector mitigation
    private Double transportMitigationKtCO2e; // TRANSPORT sector mitigation
    private Integer startingYear;
    private Integer endingYear;
}

