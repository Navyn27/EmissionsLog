package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AFOLUDashboardSummaryDto {
    private Double totalMitigationKtCO2e;
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
    private Integer startingYear;
    private Integer endingYear;
}

