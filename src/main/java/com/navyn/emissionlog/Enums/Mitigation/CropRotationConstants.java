package com.navyn.emissionlog.Enums.Mitigation;

import lombok.Getter;

@Getter
public enum CropRotationConstants {
    RATIO_BGB_TO_AGB(0.22), // Different from other projects (0.27)
    CARBON_CONTENT_DRY_BIOMASS(0.47),
    CONVERSION_C_TO_CO2(3.6666667); // 44/12
    
    private final Double value;
    
    CropRotationConstants(Double value) {
        this.value = value;
    }
}
