package com.navyn.emissionlog.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Month;
import java.time.Year;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardData {
    Double totalN2OEmissions = 0.0;
    Double totalFossilCO2Emissions = 0.0;
    Double totalBioCO2Emissions = 0.0;
    Double totalCH4Emissions = 0.0;
    Double totalCO2EqEmissions = 0.0;
    
    // Land Use Emissions
    Double totalLandUseEmissions = 0.0;  // CO2eq from land use changes
    
    // Mitigation Projects
    Double totalMitigationKtCO2e = 0.0;  // Total carbon sequestration (Kt CO2e)
    
    // Net Emissions
    Double netEmissionsKtCO2e = 0.0;  // Gross emissions - Mitigation
    
    String startingDate;
    String endingDate;
    Month month;
    Year year;
}
