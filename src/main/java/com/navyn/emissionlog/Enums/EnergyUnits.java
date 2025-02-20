package com.navyn.emissionlog.Enums;

public enum EnergyUnits {
    KWH(1.0),
    MWH(1000.0),
    JOULE(0.00000027778),
    MEGAJOULE(0.00027778),
    GIGAJOULE(0.27778);

    private final double toKWhFactor;

    EnergyUnits(double toKWhFactor) {
        this.toKWhFactor = toKWhFactor;
    }

    public double toKWh(double value) {
        return value * toKWhFactor;
    }
}
