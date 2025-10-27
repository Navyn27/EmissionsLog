package com.navyn.emissionlog.Enums.Mitigation;

import lombok.Getter;

@Getter
public enum ZeroTillageConstants {
    CARBON_INCREASE_SOIL(0.37),        // tonnes C/ha per year
    CONVERSION_C_TO_CO2(3.6666667),    // 44/12
    UREA_APPLICATION_RATE(0.1),        // tonnes/ha
    EMISSION_FACTOR_UREA(0.2);         // emission factor
    
    private final Double value;
    
    ZeroTillageConstants(Double value) {
        this.value = value;
    }
}
