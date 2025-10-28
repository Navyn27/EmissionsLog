package com.navyn.emissionlog.Enums.Mitigation;

import lombok.Getter;

@Getter
public enum ImprovedMMSConstants {
    N2O_EMISSIONS_PER_COW(21.5),                    // tonnes CO2e/cow/year
    CH4_EMISSIONS_PER_COW_ADDING_STRAW(0.781),      // tonnes CO2e/cow/year
    CH4_EMISSIONS_PER_COW_DAILY_SPREAD(0.781),      // tonnes CO2e/cow/year
    N2O_REDUCTION_RATE(0.3),                        // 30%
    CH4_REDUCTION_RATE_STRAW(0.3),                  // 30%
    CH4_REDUCTION_RATE_DAILY_SPREAD(0.5);           // 50%
    
    private final Double value;
    
    ImprovedMMSConstants(Double value) {
        this.value = value;
    }
}
