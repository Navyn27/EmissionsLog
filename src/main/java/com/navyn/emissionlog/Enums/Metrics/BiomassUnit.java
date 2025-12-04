package com.navyn.emissionlog.Enums.Metrics;

/**
 * Enum for biomass units used in AFOLU mitigation projects
 * Standard unit: TONNES_DM (Tonnes Dry Matter)
 */
public enum BiomassUnit {
    TONNES_DM(1.0),
    KILOGRAMS_DM(0.001),
    GRAMS_DM(0.000001),
    MEGAGRAMS_DM(1.0), // Same as tonnes
    POUNDS_DM(0.000453592);

    private final double toTonnesDMFactor;

    BiomassUnit(double toTonnesDMFactor) {
        this.toTonnesDMFactor = toTonnesDMFactor;
    }

    /**
     * Converts the given biomass value to tonnes DM
     * @param value The biomass value in this unit
     * @return The biomass value in tonnes DM
     */
    public double toTonnesDM(double value) {
        return value * toTonnesDMFactor;
    }
}
