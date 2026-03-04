package com.navyn.emissionlog.modules.transportScenarios.dashboard.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransportScenarioDashboardYearDto {
    private Integer year;
    private Double bauScenarioKtCO2e; // BAU for this year (Modal Shift BAU + Electric Vehicle BAU)
    private Double modalShiftMitigationKtCO2e; // Modal Shift mitigation for this year
    private Double electricVehicleMitigationKtCO2e; // Electric Vehicle mitigation for this year
    private Double totalMitigationKtCO2e; // Combined mitigation for this year
    private Double netEmissionsKtCO2e; // BAU - Total Mitigation for this year
}

