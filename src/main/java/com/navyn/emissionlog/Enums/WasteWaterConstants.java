package com.navyn.emissionlog.Enums;

import lombok.Getter;

@Getter
public enum WasteWaterConstants{

    BOD(13.505),
    BO(0.6),
    FLUSH_TOILET_MCF(0.5),
    FLUSH_TOILET_EF(0.3),
    LATRINES_MCF(0.70),
    LATRINES_EF(0.42),
    PROTEIN_EXCRETION(3.2)
    ;

    private Double value;

    WasteWaterConstants(Double value) {
        this.value = value;
    }
}
