package com.navyn.emissionlog.Enums.Metrics;

public enum VolumeUnits {
    LITER(1.0),
    MILLILITER(0.001),
    GALLON(3.78541),
    CUBIC_METER(1000.0),
    CUBIC_CENTIMETER(0.001);

    private final double toLiterFactor;

    VolumeUnits(double toLiterFactor) {
        this.toLiterFactor = toLiterFactor;
    }

    public double toLiters(double value) {
        return value * toLiterFactor;
    }
}
