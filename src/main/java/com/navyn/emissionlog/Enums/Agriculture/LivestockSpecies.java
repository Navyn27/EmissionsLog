package com.navyn.emissionlog.Enums.Agriculture;

import lombok.Getter;

@Getter
public enum LivestockSpecies {



    DAIRY_LACTATING_COWS(57.49, 0.146, 0.02),
    DAIRY_MATURE_COWS(35.64, 0.146, 0.02),
    DAIRY_GROWING_COWS(25.18, 0.146, 0.02),
    SHEEP(3.92, 0.233, 0.01),
    GOATS(3.97, 0.194, 0.01),
    SWINE(10.43, 0.094, 0.02),
    POULTRY(1.06, 0.346, 0.02),
    RABBITS(0.0, 0.0, 0.0);

    LivestockSpecies(double annualNExcretion,double fractionOfManureDepositedOnPasture,double NEFManureDepositedOnPasture) {
        this.annualNExcretion = annualNExcretion;
        this.fractionOfManureDepositedOnPasture = fractionOfManureDepositedOnPasture;
        this.NEFManureDepositedOnPasture = NEFManureDepositedOnPasture;

    }

    private double annualNExcretion = 0.0;
    private double fractionOfManureDepositedOnPasture = 0.0;
    private double NEFManureDepositedOnPasture = 0.0;
}
