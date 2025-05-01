package com.navyn.emissionlog.Enums;

import lombok.Getter;

@Getter
public enum IncinerationConstants {

    WastePerCapita(0.794520548),
    IncinerationFraction(0.001),
    DryMatterContent(0.5),
    FractionOfDryMatterCarbon(0.6),
    FractionOfFossilCarbonInTotalCarbon(0.4),
    OxidationFactor(1.0),
    CH4EmissionFactor(6500.00),
    N2OEmissionFactor(60.00),
    CO2EmissionFactor(3.666666667),
    ;

    private Double value;

    IncinerationConstants(Double value){
        this.value = value;
    }
}
