package com.navyn.emissionlog.modules.agricultureEmissions.models;

import lombok.Data;

@Data
public class AquacultureEmissionsDto {
    private int year;
    private String activityDesc;
    private double fishProduction;
}
