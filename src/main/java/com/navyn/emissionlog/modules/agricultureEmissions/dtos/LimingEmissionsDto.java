package com.navyn.emissionlog.modules.agricultureEmissions.dtos;

import com.navyn.emissionlog.Enums.LimingMaterials;
import lombok.Data;

@Data
public class LimingEmissionsDto {
    private LimingMaterials material;
    private int year;
    private double CaCO3Qty;
}
