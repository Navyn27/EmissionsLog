package com.navyn.emissionlog.modules.transportScenarios.dashboard.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransportScenarioDashboardSummaryDto {
    private Double totalBAUKtCO2e; // Sum of BAU from both Modal Shift and Electric Vehicle
    private Double totalModalShiftMitigationKtCO2e; // Modal Shift mitigation
    private Double totalElectricVehicleMitigationKtCO2e; // Electric Vehicle mitigation
    private Double totalMitigationKtCO2e; // Combined mitigation (Modal Shift + Electric Vehicle)
    private Double netEmissionsKtCO2e; // BAU - Total Mitigation
    private Double percentageReduction; // (Total Mitigation / Total BAU) * 100
    private Integer startingYear;
    private Integer endingYear;
}
