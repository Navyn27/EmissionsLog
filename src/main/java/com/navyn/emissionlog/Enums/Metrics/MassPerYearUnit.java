package com.navyn.emissionlog.Enums.Metrics;

import lombok.Getter;

@Getter
public enum MassPerYearUnit {
    // Standard unit: TONNES_PER_YEAR (t/year)
    TONNES_PER_YEAR(1.0),               // tonnes/year (standard)
    KILOGRAMS_PER_YEAR(0.001),          // kg/year
    MEGATONNES_PER_YEAR(1000000.0),     // Mt/year
    KILOTONNES_PER_YEAR(1000.0),        // kt/year
    TONNES_PER_DAY(365.0),              // tonnes/day
    KILOGRAMS_PER_DAY(0.365),           // kg/day
    TONNES_PER_MONTH(12.0),             // tonnes/month (approximate)
    POUNDS_PER_YEAR(0.000453592);       // lbs/year
    
    private final double conversionFactorToTonnesPerYear;
    
    MassPerYearUnit(double conversionFactorToTonnesPerYear) {
        this.conversionFactorToTonnesPerYear = conversionFactorToTonnesPerYear;
    }
    
    /**
     * Convert a value from this unit to tonnes per year (standard unit)
     * @param value Value in this unit
     * @return Value in tonnes/year
     */
    public double toTonnesPerYear(double value) {
        return value * this.conversionFactorToTonnesPerYear;
    }
}
