package com.navyn.emissionlog.modules.agricultureEmissions.dtos.AgriculturalLand.DirectLandEmissions;

import com.navyn.emissionlog.Enums.Agriculture.CropResiduesCropType;
import com.navyn.emissionlog.Enums.Agriculture.LandUseCategory;
import lombok.Data;

@Data
public class CropResiduesEmissionsDto {
    private int year;
    private LandUseCategory landUseCategory;
    private CropResiduesCropType cropType;
    private double totalAreaHarvested;
    private double harvestedFreshCropYield;
    private double AGResiduesDryMatter;
    private double NInCropResiduesReturned;
}
