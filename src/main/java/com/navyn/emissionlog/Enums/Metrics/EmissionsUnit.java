package com.navyn.emissionlog.Enums.Metrics;

import lombok.Getter;

@Getter
public enum EmissionsUnit {
    // Standard unit: TONNES_CO2E (tCO₂e)
    TONNES_CO2E(0.001),           // tonnes CO₂e (standard)
    KILOGRAMS_CO2E(0.000001),      // kg CO₂e
    GRAMS_CO2E(0.000000001),       // g CO₂e
    MEGATONNES_CO2E(1000.0),    // Mt CO₂e
    KILOTONNES_CO2E(1.0);    // kt CO₂e
    
    private final double conversionFactorToTonnes;
    
    EmissionsUnit(double conversionFactorToTonnes) {
        this.conversionFactorToTonnes = conversionFactorToTonnes;
    }
    
    /**
     * Convert a value from this unit to tonnes CO₂e (standard unit)
     * @param value Value in this unit
     * @return Value in tonnes CO₂e
     */
    public double toKiloTonnesCO2e(double value) {
        return value * this.conversionFactorToTonnes;
    }
}
