package com.navyn.emissionlog.Enums.Agriculture;

import lombok.Getter;

@Getter
public enum MMS {
    SOLID_STORAGE(0.4),
    PIT_STORAGE(0.28),
    DRY_LOT(0.2),
    WITH_LITTER(0.4),
    WITHOUT_LITTER(0.55),
    OTHER(0.15);

    MMS(double fractionOfManureNThatVolatilizes) {
        this.fractionOfManureNThatVolatilizes = fractionOfManureNThatVolatilizes;
    }

    private double fractionOfManureNThatVolatilizes = 0.0;
}
