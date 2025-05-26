package com.navyn.emissionlog.Payload.Requests.Agriculture;

import lombok.Data;

@Data
public class AquacultureEmissionsDto {
    private int year;
    private String activityDesc;
    private double fishProduction;
}
