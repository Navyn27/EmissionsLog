package com.navyn.emissionlog.Enums.Agriculture;

import lombok.Getter;

@Getter
public enum ManureManagementConstants {
    // CH4 Emission factors per species (kg CH4/animal/year)
    EF_DAIRY_COWS_LACTATING(44.66),
    EF_DAIRY_COWS_OTHER_MATURE(18.26),
    EF_DAIRY_COWS_GROWING(12.9),
    EF_SHEEP(4.44),
    EF_GOATS(3.24),
    EF_SWINE(5.94),
    EF_POULTRY(0.446),
    EF_RABBITS(0.08),
    
    // Conversion factor from CH4 to CO2 equivalents
    CH4_TO_CO2_CONVERSION_FACTOR(28.0),
    
    // Methane conversion factors (MCF) by MMS - Base values
    MCF_PASTURE(0.01),
    MCF_DAILY_SPREAD(0.001),
    MCF_SOLID_STORAGE(0.02),
    MCF_DRY_LOT(0.02),
    MCF_LIQUID_SLURRY(0.10),
    MCF_ANAEROBIC_LAGOON(0.39),
    MCF_ANAEROBIC_DIGESTER(0.10),
    MCF_COMPOSTING_INTENSIVE(0.005),
    MCF_COMPOSTING_STATIC_PILE(0.005),
    MCF_DEEP_BEDDING(0.02),
    MCF_POULTRY_WITH_LITTER(0.015),
    MCF_POULTRY_WITHOUT_LITTER(0.015),
    MCF_BURNED(0.0),
    
    // Emission factors for N2O calculations
    N2O_DIRECT_EF(0.02), // kg N2O-N/kg N
    N2O_VOLATILIZATION_EF(0.01), // kg N2O-N/kg N volatilized
    N2O_LEACHING_EF(0.0075), // kg N2O-N/kg N leached
    
    // Fractions
    FRAC_GASMS(0.20), // Fraction of N volatilized
    FRAC_LEACH(0.30), // Fraction of N leached
    
    // N2O-N to N2O conversion
    N2O_CONVERSION_FACTOR(44.0/28.0);
    
    private final Double value;
    
    ManureManagementConstants(Double value) {
        this.value = value;
    }
}
