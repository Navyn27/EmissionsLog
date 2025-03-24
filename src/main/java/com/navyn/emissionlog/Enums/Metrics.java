package com.navyn.emissionlog.Enums;

public enum Metrics {
    MASS, VOLUME, ENERGY;

    public String getSIUnit(Metrics metrics){
        switch(metrics){
            case MASS:
                return "KILOGRAM";
            case VOLUME:
                return "LITER";
            case ENERGY:
                return "KWH";
            default:
                return "-";
        }
    }
}
