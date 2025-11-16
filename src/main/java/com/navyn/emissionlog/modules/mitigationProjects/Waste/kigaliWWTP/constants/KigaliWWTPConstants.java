package com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.constants;

import lombok.Getter;

@Getter
public enum KigaliWWTPConstants {
    CONNECTED_HOUSEHOLDS(208000.0), // Total connected households
    PLANT_OPERATIONAL_EFFICIENCY(0.85),
    METHANE_EMISSION_FACTOR(0.25), // kg CH4 per kg COD
    COD_CONCENTRATION(0.80), // kg COD per m³
    METHANE_POTENTIAL(0.20), // kg CH4 per m³
    CH4_GWP_100_YEAR(28.00), // kg CO2e per kg CH4
    CO2E_PER_M3_SLUDGE(5.60); // kg CO2e per m³
    
    private final double value;
    
    KigaliWWTPConstants(double value) {
        this.value = value;
    }
    
    /**
     * Get Connected Households percentage based on year
     * - 2026 or less: 0%
     * - 2027: 65%
     * - 2028: 70%
     * - 2029: 75%
     * - 2030+: 80%
     */
    public static double getConnectedHouseholdsPercentage(int year) {
        if (year <= 2026) {
            return 0.0;
        } else if (year == 2027) {
            return 0.65;
        } else if (year == 2028) {
            return 0.70;
        } else if (year == 2029) {
            return 0.75;
        } else { // 2030 and above
            return 0.80;
        }
    }
}
