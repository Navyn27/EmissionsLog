package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AFOLUDashboardYearDto {
    private Integer year;
    private Double wetlandParks;
    private Double settlementTrees;
    private Double streetTrees;
    private Double greenFences;
    private Double cropRotation;
    private Double zeroTillage;
    private Double protectiveForest;
    private Double manureCovering;
    private Double addingStraw;
    private Double dailySpread;
    private Double totalMitigationKtCO2e;
}

