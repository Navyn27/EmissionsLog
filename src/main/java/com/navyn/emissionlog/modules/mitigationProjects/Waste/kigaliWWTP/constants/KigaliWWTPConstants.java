package com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.constants;

import lombok.Getter;

@Getter
public enum KigaliWWTPConstants {
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
}
