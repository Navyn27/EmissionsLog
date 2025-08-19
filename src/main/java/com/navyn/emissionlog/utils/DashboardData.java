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
    String startingDate;
    String endingDate;
    Month month;
    Year year;
}
