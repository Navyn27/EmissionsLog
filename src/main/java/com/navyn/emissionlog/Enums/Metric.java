package com.navyn.emissionlog.Enums;

public enum Metric {
    MASS, VOLUME, ENERGY;

    public String getSIUnit(Metric metric){
        switch(metric){
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
