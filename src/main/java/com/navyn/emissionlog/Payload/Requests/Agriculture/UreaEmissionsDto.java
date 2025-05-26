package com.navyn.emissionlog.Payload.Requests.Agriculture;

import lombok.Data;

@Data
public class UreaEmissionsDto {
    private String fertilizerName;
    private int year;
    private double qty;
}
