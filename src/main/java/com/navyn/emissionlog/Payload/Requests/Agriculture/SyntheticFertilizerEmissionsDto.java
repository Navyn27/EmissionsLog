package com.navyn.emissionlog.Payload.Requests.Agriculture;

import com.navyn.emissionlog.Enums.CropTypes;
import com.navyn.emissionlog.Enums.Fertilizers;
import lombok.Data;

@Data
public class SyntheticFertilizerEmissionsDto {
    private int year;
    private CropTypes cropType;
    private Fertilizers fertType;
    private double qtyApplied;
}
