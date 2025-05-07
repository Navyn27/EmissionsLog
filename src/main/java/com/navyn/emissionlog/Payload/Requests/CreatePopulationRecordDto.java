package com.navyn.emissionlog.Payload.Requests;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreatePopulationRecordDto {
    private int year;
    private Long population;
    private Double annualGrowth;
    private String country;
    private int numberOfKigaliHouseholds;
    private BigDecimal GDPMillions;
    private BigDecimal GDPPerCapita;
    private BigDecimal kigaliGDP;
}
