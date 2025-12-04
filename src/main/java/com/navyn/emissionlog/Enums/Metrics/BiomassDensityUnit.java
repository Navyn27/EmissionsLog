package com.navyn.emissionlog.Enums.Metrics;

/**
 * Enum for biomass density units used in AFOLU mitigation projects
 * Standard unit: TONNES_DM_PER_HA (Tonnes Dry Matter per Hectare)
 */
public enum BiomassDensityUnit {
    TONNES_DM_PER_HA(1.0),
    KG_DM_PER_HA(0.001),
    GRAMS_DM_PER_HA(0.000001),
    TONNES_DM_PER_SQM(10000.0), // 1 ha = 10000 m²
    KG_DM_PER_SQM(10.0),
    TONNES_DM_PER_ACRE(2.47105); // 1 acre = 0.404686 ha

    private final double toTonnesDMPerHAFactor;

    BiomassDensityUnit(double toTonnesDMPerHAFactor) {
        this.toTonnesDMPerHAFactor = toTonnesDMPerHAFactor;
    }

    /**
     * Converts the given biomass density value to tonnes DM per hectare
     * @param value The biomass density value in this unit
     * @return The biomass density value in tonnes DM per hectare
     */
    public double toTonnesDMPerHA(double value) {
        return value * toTonnesDMPerHAFactor;
    }
}
