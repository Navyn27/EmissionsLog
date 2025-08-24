package com.navyn.emissionlog.Enums.Waste;

import lombok.Getter;

@Getter
public enum BurntWasteConstants {
    //Constants for Burnt waste
    WASTE_PER_CAPITA(0.794520548),
    FRACTION_OF_POP_BURNING_WASTE(0.10),
    FRACTION_OF_WASTE_OPEN_BURNT(0.30),
    CH4_EF(6500.00),
    N2O_EF(150.00),
    CO2_EF(0.01551616),
    ;
    private Double value;

    BurntWasteConstants(Double value){
        this.value = value;
    }
}
