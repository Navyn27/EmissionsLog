package com.navyn.emissionlog.modules.mitigationProjects.Waste.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WasteDashboardSummaryDto {
    private Double totalMitigationKtCO2e;
    private Double wasteToEnergy;
    private Double mbtComposting;
    private Double landfillGasUtilization;
    private Double eprPlasticWaste;
    private Double kigaliFSTP;
    private Double kigaliWWTP;
    private Double iswm;
    private Integer startingYear;
    private Integer endingYear;
}

