package com.navyn.emissionlog.Enums;

import lombok.Getter;

@Getter
public enum GWP {
    N2O(298),
    CH4(25),
    ;

    private final int value;
    GWP(int value) {
        this.value = value;
    }
}
