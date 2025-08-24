package com.navyn.emissionlog.modules.agricultureEmissions.dtos.AgriculturalLand;

import com.navyn.emissionlog.Enums.Agriculture.CropTypes;
import com.navyn.emissionlog.Enums.Agriculture.Fertilizers;
import lombok.Data;

@Data
public class SyntheticFertilizerEmissionsDto {
    private int year;
    private CropTypes cropType;
    private Fertilizers fertType;
    private double qtyApplied;
}
