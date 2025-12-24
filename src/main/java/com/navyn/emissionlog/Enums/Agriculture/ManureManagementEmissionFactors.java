package com.navyn.emissionlog.Enums.Agriculture;

import lombok.Getter;

@Getter
public enum ManureManagementEmissionFactors {
    // CH4 Emission Factors (kg CH4/animal/year)
    CH4_DAIRY_COWS_LACTATING(44.66),
    CH4_DAIRY_COWS_OTHER_MATURE(18.26),
    CH4_DAIRY_COWS_GROWING(12.9),
    CH4_SHEEP(4.44),
    CH4_GOATS(3.24),
    CH4_SWINE(5.94),
    CH4_POULTRY(0.446),
    CH4_RABBITS(0.08),

    // N2O Emission Factors (kg N2O/animal/year)
    N2O_DAIRY_COWS_LACTATING(0.183333),
    N2O_DAIRY_COWS_OTHER_MATURE(0.113333),
    N2O_DAIRY_COWS_GROWING(0.080000),
    N2O_SHEEP(0.000293),
    N2O_GOATS(0.000900),
    N2O_SWINE(0.001767),
    N2O_POULTRY(0.001067),
    N2O_RABBITS(0.013000);

    private final double value;

    ManureManagementEmissionFactors(double value) {
        this.value = value;
    }

    // Conversion factors
    public static final double CH4_TO_CO2EQ = 28.0;
    public static final double N2O_TO_CO2EQ = 28.0;
}
