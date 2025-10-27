package com.navyn.emissionlog.Enums.Agriculture;

import lombok.Getter;

@Getter
public enum ManureManagementConstants {
    // Volatile Solids factors per species (kg VS/day/1000kg animal)
    VS_DAIRY_CATTLE(5.0),
    VS_NON_DAIRY_CATTLE(4.5),
    VS_BUFFALO(4.3),
    VS_SWINE(0.5),
    VS_SHEEP(0.3),
    VS_GOATS(0.28),
    VS_CAMELS(3.5),
    VS_HORSES(2.5),
    VS_MULES_ASSES(2.3),
    VS_POULTRY(0.02),
    
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
    
    // Maximum methane producing capacity
    BO_FACTOR(0.24), // m³ CH4/kg VS
    
    // CH4 density
    CH4_DENSITY(0.67), // kg/m³
    
    // N excretion rates (kg N/head/year)
    N_EXCRETION_DAIRY_CATTLE(100.0),
    N_EXCRETION_NON_DAIRY_CATTLE(60.0),
    N_EXCRETION_BUFFALO(65.0),
    N_EXCRETION_SWINE(16.0),
    N_EXCRETION_SHEEP(10.0),
    N_EXCRETION_GOATS(9.0),
    N_EXCRETION_CAMELS(50.0),
    N_EXCRETION_HORSES(40.0),
    N_EXCRETION_MULES_ASSES(35.0),
    N_EXCRETION_POULTRY(0.6),
    
    // Emission factors
    N2O_DIRECT_EF(0.02), // kg N2O-N/kg N
    N2O_VOLATILIZATION_EF(0.01), // kg N2O-N/kg N volatilized
    N2O_LEACHING_EF(0.0075), // kg N2O-N/kg N leached
    
    // Fractions
    FRAC_GASMS(0.20), // Fraction of N volatilized
    FRAC_LEACH(0.30), // Fraction of N leached
    
    // N2O-N to N2O conversion
    N2O_CONVERSION_FACTOR(44.0/28.0),
    
    // Default animal weights (kg)
    WEIGHT_DAIRY_CATTLE(600.0),
    WEIGHT_NON_DAIRY_CATTLE(400.0),
    WEIGHT_BUFFALO(450.0),
    WEIGHT_SWINE(100.0),
    WEIGHT_SHEEP(50.0),
    WEIGHT_GOATS(45.0),
    WEIGHT_CAMELS(500.0),
    WEIGHT_HORSES(450.0),
    WEIGHT_MULES_ASSES(350.0),
    WEIGHT_POULTRY(2.0);
    
    private final Double value;
    
    ManureManagementConstants(Double value) {
        this.value = value;
    }
}
