package com.navyn.emissionlog.Enums.Metrics;

import lombok.Getter;

@Getter
public enum EmissionsKilotonneUnit {
    // Standard unit: KILOTONNES_CO2E (ktCO₂e)
    KILOTONNES_CO2E(1.0),       // kt CO₂e (standard)
    TONNES_CO2E(0.001),         // t CO₂e
    MEGATONNES_CO2E(1000.0),    // Mt CO₂e
    KILOGRAMS_CO2E(0.000001),   // kg CO₂e
    GRAMS_CO2E(0.000000001);    // g CO₂e
    
    private final double conversionFactorToKilotonnes;
    
    EmissionsKilotonneUnit(double conversionFactorToKilotonnes) {
        this.conversionFactorToKilotonnes = conversionFactorToKilotonnes;
    }
    
    /**
     * Convert a value from this unit to kilotonnes CO₂e (standard unit)
     * @param value Value in this unit
     * @return Value in kilotonnes CO₂e
     */
    public double toKilotonnesCO2e(double value) {
        return value * this.conversionFactorToKilotonnes;
    }
}
