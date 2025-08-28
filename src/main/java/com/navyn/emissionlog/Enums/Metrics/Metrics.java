package com.navyn.emissionlog.Enums.Metrics;

public enum Metrics {
    MASS, VOLUME, ENERGY, DISTANCE, DENSITY;

    public String getSIUnit(Metrics metrics){
        switch(metrics){
            case MASS:
                return "KILOGRAM";
            case VOLUME:
                return "LITER";
            case ENERGY:
                return "KWH";
            case DISTANCE:
                return "METER";
            default:
                return "-";
        }
    }
}
