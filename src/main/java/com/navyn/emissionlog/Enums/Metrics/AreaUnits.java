package com.navyn.emissionlog.Enums.Metrics;

public enum AreaUnits {
    HECTARES(10000),
    ARES(1000),
    CENTIARES(1),
    ACRES(4046.86),
    SQUARE_METERS(1),
    SQUARE_KILOMETERS(1000000),
    SQUARE_MILES(2589988.110336),
    SQUARE_YARDS(0.83612736),
    SQUARE_FEET(0.092903);

    AreaUnits(double factorToSI) {
        this.factorToSI = factorToSI;
    }

    private double factorToSI;
}
