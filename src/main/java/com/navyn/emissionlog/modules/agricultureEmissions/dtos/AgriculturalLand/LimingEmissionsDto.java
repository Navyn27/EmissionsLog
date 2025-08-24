package com.navyn.emissionlog.modules.agricultureEmissions.dtos.AgriculturalLand;

import com.navyn.emissionlog.Enums.Agriculture.LimingMaterials;
import lombok.Data;

@Data
public class LimingEmissionsDto {
    private LimingMaterials material;
    private int year;
    private double CaCO3Qty;
}
