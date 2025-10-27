package com.navyn.emissionlog.Enums.Mitigation;

import lombok.Getter;

@Getter
public enum WetlandParksConstants {
    CONVERSION_M3_TO_TONNES_DM(0.66),
    RATIO_BGB_TO_AGB(0.27),
    CARBON_CONTENT_DRY_WOOD(0.47),
    CONVERSION_C_TO_CO2(3.6666667); // 44/12
    
    private final Double value;
    
    WetlandParksConstants(Double value) {
        this.value = value;
    }
}
