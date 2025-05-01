package com.navyn.emissionlog.Enums;

import lombok.Getter;

@Getter
public enum BioTreatedWasteConstants {
    //Constants for BioTreated Waste
    CH4_EF(4.0),
    N2O_EF(0.001),
    WastePerCap(190.0),

    //1990 - 2010
    CompostedWasteA(31.40),

    //2011 - 2013
    CompostedWasteB(22.10),

    //2014 - 2016
    CompostedWasteC(21.70),

    //2017 - 2021
    CompostedWasteD(16.20),

    //2022 -
    CompostedWasteE(20.80)
    ;

    private Double value;

    BioTreatedWasteConstants(Double value){
        this.value = value;
    }
}
