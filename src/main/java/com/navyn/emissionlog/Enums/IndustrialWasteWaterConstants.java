package com.navyn.emissionlog.Enums;

import lombok.Getter;

@Getter
public enum IndustrialWasteWaterConstants {

    //Sugar constants
    WASTE_WATER_GENERATED(9.0, 6.3, 7.0, 8.0),
    CODi(3.2, 2.9, 2.7, 4.1),
    INDUSTRIAL_WW_MCF(0.7, 0.7, 0.7, 0.7),
    INDUSTRIAL_WW_BO(0.25, 0.25, 0.25, 0.25),
    ;

    private Double sugarValue;
    private Double beerValue;
    private Double dairyProductsValue;
    private Double meatAndPoultryValue;



    IndustrialWasteWaterConstants(Double sugarValue, Double beerValue, Double dairyProductsValue, Double meatAndPoultryValue) {
        this.sugarValue = sugarValue;
        this.beerValue = beerValue;
        this.dairyProductsValue = dairyProductsValue;
        this.meatAndPoultryValue = meatAndPoultryValue;
    }



}
