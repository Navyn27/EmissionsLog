package com.navyn.emissionlog.Payload.Requests.Agriculture;

import com.navyn.emissionlog.Enums.WaterRegime;
import lombok.Data;

@Data
public class RiceCultivationEmissionsDto {
    private String riceEcosystem;
    private WaterRegime waterRegime;
    private double harvestedArea;
    private int cultivationPeriod;
    private int year;
}
