package com.navyn.emissionlog.modules.agricultureEmissions.dtos.AgriculturalLand.IndirectLandEmissions;

import com.navyn.emissionlog.Enums.Agriculture.LandUseCategory;
import lombok.Data;

@Data
public class AtmosphericDepositionEmissionsDto {

    private Integer year;
    private LandUseCategory landUseCategory;
    private double syntheticNThatVolatilizes;
    private double organicNSoilAdditions;
    private double excretionsDepositedByGrazingAnimals;

}
