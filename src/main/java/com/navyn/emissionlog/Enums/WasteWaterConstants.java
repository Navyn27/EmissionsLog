package com.navyn.emissionlog.Enums;

import lombok.Getter;

@Getter
public enum WasteWaterConstants {

    BOD(13.505),
    Bo(0.6),
    FlushToiletMCF(0.5),
    FlushToiletEF(0.3),
    LatrinesMCF(0.70),
    LatrinesEF(0.42),
    ProteinExcretionPerCapita(3.20),
    F_NPR(0.16),
    F_NON_CON(1.40),
    F_IND_COM(1.25),
    N_SLUDGE(0.0),
    EF_EFFLUENT(0.005),
    ;

    private Double value;

    WasteWaterConstants(Double value) {
        this.value = value;
    }
}
