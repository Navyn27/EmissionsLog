package com.navyn.emissionlog.Enums;

public enum GeneralWasteWaterConstants {

    PROTEIN_EXCRETION_PER_CAPITA(3.20),
    F_NPR(0.16),
    F_NON_CON(1.40),
    F_IND_COM(1.25),
    N_SLUDGE(0.0),
    EF_EFFLUENT(0.005),
    ;

    private Double value;

    GeneralWasteWaterConstants(Double value) {
        this.value = value;
    }

}
