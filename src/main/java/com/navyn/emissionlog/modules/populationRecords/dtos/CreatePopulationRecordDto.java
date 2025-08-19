package com.navyn.emissionlog.modules.population.dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreatePopulationRecordDto {
    private Double year;
    private Double population;
    private Double kigaliAnnualGrowth;
    private Double annualGrowth;
    private String country = "Rwanda";
    private Double numberOfKigaliHouseholds;
    private BigDecimal GDPMillions;
    private BigDecimal GDPPerCapita;
    private BigDecimal kigaliGDP;
}
