package com.navyn.emissionlog.modules.agricultureEmissions.dtos.AgriculturalLand;

import lombok.Data;

@Data
public class AquacultureEmissionsDto {
    private int year;
    private String activityDesc;
    private double fishProduction;
}
