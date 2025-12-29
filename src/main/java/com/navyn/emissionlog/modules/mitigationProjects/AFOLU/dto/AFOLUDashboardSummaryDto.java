package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AFOLUDashboardSummaryDto {
    private Double totalMitigationKtCO2e;
    private Double totalBAU; // Sum of BAU values in year range
    private Double adjustmentMitigation; // BAU sum - Total Mitigation
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
    private Double improvedMMSTotal; // Sum of Manure Covering + Adding Straw + Daily Spread
    private Integer startingYear;
    private Integer endingYear;
    
    // Data completeness metrics
    private Map<String, Long> recordCounts; // Record count per project
    private Map<String, Double> dataCoverage; // Percentage of years with data per project
    
    // Intervention breakdown (optional, can be null)
    private Map<String, Double> interventionBreakdown; // Mitigation by intervention name
}

