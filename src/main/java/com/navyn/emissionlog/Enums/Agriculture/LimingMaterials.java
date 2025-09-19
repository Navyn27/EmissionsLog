package com.navyn.emissionlog.Enums.Agriculture;

import lombok.Getter;

@Getter
public enum LimingMaterials {
    LIMESTONE(0.12),
    DOLOMITE(0.13);

    LimingMaterials(double value) {
        this.limingConstant = value;
    }

    private double limingConstant = 0.0;
}
