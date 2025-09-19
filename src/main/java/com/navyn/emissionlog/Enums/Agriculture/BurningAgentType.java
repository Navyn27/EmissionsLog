package com.navyn.emissionlog.Enums.Agriculture;

import lombok.Getter;

@Getter
public enum BurningAgentType {
    SAVANNA_AND_GRASSLAND(2.3, 1613.0, 0.21),
    AGRICULTURAL_RESIDUES(2.7, 1515.0, 0.07),
    FOREST(4.7, 1569.0, 0.26),
    BIOFUEL_BURNING(6.1, 1550.0, 0.06);

    BurningAgentType(double CH4, double CO2, double N2O){
        this.CH4EF = CH4;
        this.CO2EF = CO2;
        this.N2OEF = N2O;
    }

    private double CH4EF = 0.0;
    private double CO2EF = 0.0;
    private double N2OEF = 0.0;

}
