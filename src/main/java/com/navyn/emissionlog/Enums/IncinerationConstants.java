package com.navyn.emissionlog.Enums;

import lombok.Getter;

@Getter
public enum IncinerationConstants {


    WASTE_PER_CAPITA(0.794520548),
    INCINERATION_FRACTION(0.001),
    DRY_MATTER_CONTENT(0.5),
    FRACTION_OF_DRY_MATTER_CARBON(0.6),
    FRACTION_OF_FOSSIL_CARBON_IN_TOTAL_CARBON(0.4),
    OXIDATION_FACTOR(1.0),
    CH4_EF(6500.00),
    N2O_EF(60.00),
    CO2_EF(3.666666667),
    ;

    private Double value;

    IncinerationConstants(Double value){
        this.value = value;
    }
}
