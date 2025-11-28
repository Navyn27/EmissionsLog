package com.navyn.emissionlog.Enums.Metrics;

/**
 * Enum for volume per area units used in AFOLU mitigation projects
 * Standard unit: CUBIC_METER_PER_HA (Cubic meters per Hectare)
 */
public enum VolumePerAreaUnit {
    CUBIC_METER_PER_HA(1.0),
    CUBIC_METER_PER_SQM(10000.0), // 1 ha = 10000 m²
    CUBIC_METER_PER_ACRE(2.47105), // 1 acre = 0.404686 ha
    LITER_PER_HA(0.001), // 1 m³ = 1000 L
    LITER_PER_SQM(10.0);

    private final double toCubicMeterPerHAFactor;

    VolumePerAreaUnit(double toCubicMeterPerHAFactor) {
        this.toCubicMeterPerHAFactor = toCubicMeterPerHAFactor;
    }

    /**
     * Converts the given volume per area value to cubic meters per hectare
     * @param value The volume per area value in this unit
     * @return The volume per area value in cubic meters per hectare
     */
    public double toCubicMeterPerHA(double value) {
        return value * toCubicMeterPerHAFactor;
    }
}
