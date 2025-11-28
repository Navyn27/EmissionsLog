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

    /**
     * Converts the given area value to hectares
     * @param value The area value in this unit
     * @return The area value in hectares
     */
    public double toHectares(double value) {
        // Convert to square meters first, then to hectares (1 hectare = 10000 square meters)
        return (value * factorToSI) / 10000.0;
    }
}
