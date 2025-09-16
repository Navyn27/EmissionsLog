package com.navyn.emissionlog.Enums.Metrics;

public enum EnergyUnits {

    MWH(0.001),
    KWH(1.0),
    WH(1000.0),

    JOULE(0.00000027778),
    MEGAJOULE(0.27778),
    GIGAJOULE(277.78),
    KILO_JOULE(0.00027778),
    TERA_JOULE(277777.78);

    private final double toKWhFactor;

    EnergyUnits(double toKWhFactor) {
        this.toKWhFactor = toKWhFactor;
    }

    public double toKWh(double value) {
        return value * toKWhFactor;
    }
}
