package com.navyn.emissionlog.modules.agricultureEmissions.dtos;

import lombok.Data;

@Data
public class UreaEmissionsDto {
    private String fertilizerName;
    private int year;
    private double qty;
}
