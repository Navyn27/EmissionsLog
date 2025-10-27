package com.navyn.emissionlog.Enums.Mitigation;

import lombok.Getter;

@Getter
public enum GreenFencesConstants {
    CARBON_CONTENT_DRY_AGB(0.47),
    RATIO_BGB_TO_AGB(0.27),
    CONVERSION_C_TO_CO2(3.6666667); // 44/12
    
    private final Double value;
    
    GreenFencesConstants(Double value) {
        this.value = value;
    }
}
