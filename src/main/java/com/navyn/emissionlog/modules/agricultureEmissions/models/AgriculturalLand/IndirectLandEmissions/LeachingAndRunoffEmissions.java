package com.navyn.emissionlog.modules.agricultureEmissions.models.AgriculturalLand.IndirectLandEmissions;

import com.navyn.emissionlog.modules.agricultureEmissions.models.AgricultureAbstractClass;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class LeachingAndRunoffEmissions extends AgricultureAbstractClass {

    private String landUseSubdivision;
    private double syntheticNAppliedToSoil;
    private double organicAdditionsAppliedToSoil;
    private double excretionsDepositedByGrazingAnimals;
    private double NInCropResidues;
    private double NMineralizedInMineralSoils;
    private double N2OProducedFromLeachingAndRunoff;
    private double N2OEmissions;
    private double CO2EqEmissions;

}
