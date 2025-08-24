package com.navyn.emissionlog.Enums.Metrics;

public enum MassUnits {
    KILOGRAM(1.0),
    GRAM(0.001),
    MILLIGRAM(0.000001),
    TON(1000.0),
    POUND(0.453592),
    OUNCE(0.0283495);

    private final double toKilogramFactor;

    MassUnits(double toKilogramFactor) {
        this.toKilogramFactor = toKilogramFactor;
    }

    public double toKilograms(double value) {
        return value * toKilogramFactor;
    }
}
