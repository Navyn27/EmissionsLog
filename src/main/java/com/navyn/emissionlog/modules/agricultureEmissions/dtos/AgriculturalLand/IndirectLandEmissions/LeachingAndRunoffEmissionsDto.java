package com.navyn.emissionlog.modules.agricultureEmissions.dtos.AgriculturalLand.IndirectLandEmissions;

import com.navyn.emissionlog.Enums.Agriculture.LandUseCategory;
import lombok.Data;

@Data
public class LeachingAndRunoffEmissionsDto {

    private Integer year;
    private LandUseCategory landUseCategory;
    private double syntheticNApplied;
    private double organicSoilAdditions;
    private double excretionsDepositedByGrazingAnimals;
    private double nInCropResidues;
    private double nMineralizedInMineralSoils;
}
