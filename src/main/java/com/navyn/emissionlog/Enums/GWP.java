package com.navyn.emissionlog.Enums;

public enum GWP {
    NO2(298),
    CH4(25),
    ;

    private final int value;
    public int getValue() {
        return value;
    }
    GWP(int value) {
        this.value = value;
    }
}
