package com.navyn.emissionlog.Enums;

public enum BurntWasteConstants {
    //Constants for Burnt waste
    WastePerCap(0.794520548),
    FractionOfPopBurningWaste(0.10),
    FractionOfWasteOpenBurnt(0.30),
    CH4_Emission(6500.00),
    N2O_Emission(150.00),
    CO2_Emission(0.02),
    ;
    private Double value;

    BurntWasteConstants(Double value){
        this.value = value;
    }
}
