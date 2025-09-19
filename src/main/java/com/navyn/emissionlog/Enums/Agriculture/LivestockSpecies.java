package com.navyn.emissionlog.Enums.Agriculture;

import lombok.Getter;

@Getter
public enum LivestockSpecies {

    DAIRY_LACTATING_COWS(57.49, 48.75, 0.146, 0.02,77.37, 44.66, 57.488, 0.75, 0.01),
    DAIRY_MATURE_COWS(35.64, 48.75, 0.146, 0.02,66.32, 18.26, 35.642, 0.75, 0.01 ),
    DAIRY_GROWING_COWS(25.18, 48.75,0.146, 0.02,51.11, 12.9, 25.18, 0.75, 0.01),
    SHEEP(3.92, 7.968, 0.233, 0.01,9.49, 4.44, 3.922, 0.86, 0.01),
    GOATS(3.97, 6.292, 0.194, 0.01,5.0, 3.24, 3.971, 0.76, 0.01),
    SWINE(10.43, 12.805, 0.094, 0.02 ,1.0, 5.94, 10.428, 0.86, 0.01),
    POULTRY(1.06, 1.572, 0.346, 0.02 ,0.446, 0.446, 1.064, 0.75, 0.01),
    RABBITS(0.0, 1.144, 0.0, 0.0,0.08, 0.08, 8.308, 0.89, 0.01);

    LivestockSpecies(double annualNExcretion, double excretionRate, double fractionOfManureDepositedOnPasture,double NEFManureDepositedOnPasture, double entericFermentationCH4EF, double manureManagementCH4EF, double manureNitrogenEF, double meanLossesOfNinManureMMS, double EFFOrgManureCompostAppliedInFields) {
        this.annualNExcretion = annualNExcretion;
        this.excretionRate = excretionRate;
        this.fractionOfManureDepositedOnPasture = fractionOfManureDepositedOnPasture;
        this.NEFManureDepositedOnPasture = NEFManureDepositedOnPasture;
        this.entericFermentationCH4EF = entericFermentationCH4EF;
        this.manureManagementCH4EF = manureManagementCH4EF;
        this.manureNitrogenEF = manureNitrogenEF;
        this.meanLossesOfNinManureMMS = meanLossesOfNinManureMMS;
        this.EFFOrgManureCompostAppliedInFields = EFFOrgManureCompostAppliedInFields;
    }

    private double annualNExcretion = 0.0;
    private double excretionRate = 0.0;
    private double fractionOfManureDepositedOnPasture = 0.0;
    private double NEFManureDepositedOnPasture = 0.0;

    //Emission Factors
    private double entericFermentationCH4EF = 0.0;
    private double manureManagementCH4EF = 0.0;
    private double manureNitrogenEF = 0.0;
    private double meanLossesOfNinManureMMS = 0.0;
    private double EFFOrgManureCompostAppliedInFields = 0.0;
}

