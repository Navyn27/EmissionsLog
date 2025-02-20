package com.navyn.emissionlog.Enums;

public enum DistanceUnits {
    KILOMETER(1000.0),
    METER(1.0),
    CENTIMETER(0.01),
    MILE(1609.34),
    YARD(0.9144),
    FOOT(0.3048),
    INCH(0.0254);

    private final double toMeterFactor;

    DistanceUnits(double toMeterFactor) {
        this.toMeterFactor = toMeterFactor;
    }

    public double toMeters(double value) {
        return value * toMeterFactor;
    }
}
