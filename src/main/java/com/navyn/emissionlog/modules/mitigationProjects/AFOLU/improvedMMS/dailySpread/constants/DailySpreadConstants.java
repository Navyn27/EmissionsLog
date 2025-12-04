package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.dailySpread.constants;

import lombok.Getter;

@Getter
public enum DailySpreadConstants {
    CH4_EMISSIONS_PER_COW_DAILY_SPREAD(0.781),      // tonnes CO2e/cow/year
    CH4_REDUCTION_RATE_DAILY_SPREAD(0.5);           // 50% reduction with daily spread MMS
    
    private final Double value;
    
    DailySpreadConstants(Double value) {
        this.value = value;
    }
}
