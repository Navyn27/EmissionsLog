package com.navyn.emissionlog.Enums.LandUse;

import lombok.Getter;

@Getter
public enum LandUseConstants {

    AVG_ANNUAL_ABG_BIOMASS_GROWTH(5.2),
    RATIO_BGB_AGB(0.27),
    BIOMASS_CONVERSION_EXPANSION_FACTOR(1.57),
    C_FRACT_DRY_MATTER(0.47),
    FRACT_BIOMASS_LOST_DISTURBANCE(1.0),
    C_TO_CO2_FACTOR(4.0),
    CH4_EF_REWETTED_LAND(900.0),
    ABG_BIOMASS_STOCK(79.43);


    private final Double value;

    LandUseConstants(Double value){
        this.value = value;
    }
}
