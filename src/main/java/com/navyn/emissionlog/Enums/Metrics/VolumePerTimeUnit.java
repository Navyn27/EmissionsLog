package com.navyn.emissionlog.Enums.Metrics;

import lombok.Getter;

@Getter
public enum VolumePerTimeUnit {
    // Standard unit: CUBIC_METERS_PER_DAY (m³/day)
    CUBIC_METERS_PER_DAY(1.0),          // m³/day (standard)
    LITERS_PER_DAY(0.001),              // L/day
    CUBIC_METERS_PER_HOUR(24.0),        // m³/hour
    LITERS_PER_HOUR(0.024),             // L/hour
    CUBIC_METERS_PER_YEAR(1.0/365.0),   // m³/year
    LITERS_PER_YEAR(0.001/365.0),       // L/year
    GALLONS_PER_DAY(0.00378541);        // gallons/day (US)
    
    private final double conversionFactorToCubicMetersPerDay;
    
    VolumePerTimeUnit(double conversionFactorToCubicMetersPerDay) {
        this.conversionFactorToCubicMetersPerDay = conversionFactorToCubicMetersPerDay;
    }
    
    /**
     * Convert a value from this unit to cubic meters per day (standard unit)
     * @param value Value in this unit
     * @return Value in m³/day
     */
    public double toCubicMetersPerDay(double value) {
        return value * this.conversionFactorToCubicMetersPerDay;
    }
}
