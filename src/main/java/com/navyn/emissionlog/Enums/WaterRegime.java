package com.navyn.emissionlog.Enums;

import lombok.Getter;

@Getter
public enum WaterRegime {
    AGGREGATED(0.78),
    INTERMITTENT_FLOODED_MULTIPLE_AERATION(0.52),
    NON_FLOODED_PRE_SEASON_LT_180_DAYS(1.0),
    NON_FLOODED_PRE_SEASON_GT_180_DAYS(0.68),
    FLOODED_PRE_SEASON_GT_30_DAYS(1.9),
    AGGREGATED_CASE(1.22);

    private final Double value;

    WaterRegime(Double value) {
        this.value = value;
    }
}
