package com.navyn.emissionlog.Enums;

import lombok.Getter;

@Getter
public enum BioTreatedWasteConstants {
    //Constants for BioTreated Waste
    CH4_EF(4.0),
    N2O_EF(0.001),
    WASTE_PER_CAPITA(190.0),

    //1990 - 2010
    COMPOSTED_WASTE_A(31.40),

    //2011 - 2013
    COMPOSTED_WASTE_B(22.10),

    //2014 - 2016
    COMPOSTED_WASTE_C(21.70),

    //2017 - 2021
    COMPOSTED_WASTE_D(16.20),

    //2022 -
    COMPOSTED_WASTE_E(20.80)
    ;

    private Double value;

    BioTreatedWasteConstants(Double value){
        this.value = value;
    }
}
