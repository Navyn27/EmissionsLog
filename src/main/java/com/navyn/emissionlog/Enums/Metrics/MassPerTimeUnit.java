package com.navyn.emissionlog.Enums.Metrics;

import lombok.Getter;

@Getter
public enum MassPerTimeUnit {
    // Standard unit: TONNES_PER_DAY (t/day or tons/day)
    TONNES_PER_DAY(1.0),                // tonnes/day (standard)
    KILOGRAMS_PER_DAY(0.001),           // kg/day
    TONNES_PER_YEAR(1.0/365.0),         // tonnes/year
    KILOGRAMS_PER_YEAR(0.001/365.0),    // kg/year
    TONNES_PER_HOUR(24.0),              // tonnes/hour
    KILOGRAMS_PER_HOUR(0.024),          // kg/hour
    MEGATONNES_PER_DAY(1000.0),         // Mt/day
    POUNDS_PER_DAY(0.000453592);        // lbs/day
    
    private final double conversionFactorToTonnesPerDay;
    
    MassPerTimeUnit(double conversionFactorToTonnesPerDay) {
        this.conversionFactorToTonnesPerDay = conversionFactorToTonnesPerDay;
    }
    
    /**
     * Convert a value from this unit to tonnes per day (standard unit)
     * @param value Value in this unit
     * @return Value in tonnes/day
     */
    public double toTonnesPerDay(double value) {
        return value * this.conversionFactorToTonnesPerDay;
    }
}
