package com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.IndirectLandEmissions;

import com.navyn.emissionlog.Enums.Agriculture.LandUseCategory;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgricultureAbstractClass;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class LeachingAndRunoffEmissions extends AgricultureAbstractClass {

    @Enumerated(EnumType.STRING)
    private LandUseCategory landUseSubdivision;

    private double syntheticNAppliedToSoil;
    private double organicAdditionsAppliedToSoil;
    private double excretionsDepositedByGrazingAnimals;
    private double NInCropResidues;
    private double NMineralizedInMineralSoils;
    private double N2OProducedFromLeachingAndRunoff;
    private double N2OEmissions;
    private double CO2EqEmissions;

}
