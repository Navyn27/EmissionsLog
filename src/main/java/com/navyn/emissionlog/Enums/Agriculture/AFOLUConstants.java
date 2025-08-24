package com.navyn.emissionlog.Enums.Agriculture;

import lombok.Getter;

@Getter
public enum AFOLUConstants {

    //Enteric Fermentation Constants
    ENTERIC_DAIRY_LACTATING_COWS_CH4_EF(77.37),
    ENTERIC_DAIRY_MATURE_COWS_CH4_EF(66.32),
    ENTERIC_DAIRY_GROWING_COWS_CH4_EF(51.11),
    ENTERIC_SHEEP_CH4_EF(9.49),
    ENTERIC_GOATS_CH4_EF(5.0),
    ENTERIC_SWINE_CH4_EF(1.0),

    //Manure management constants
    MANURE_DAIRY_LACTATING_COWS_CH4_EF(44.66),
    MANURE_DAIRY_MATURE_COWS_CH4_EF(18.26),
    MANURE_DAIRY_GROWING_COWS_CH4_EF(12.9),
    MANURE_SHEEP_CH4_EF(4.44),
    MANURE_GOATS_CH4_EF(3.24),
    MANURE_SWINE_CH4_EF(5.94),
    MANURE_POULTRY_CH4_EF(0.446),
    MANURE_RABBITS_CH4_EF(0.08),


    //Manure Nitrogen Emission Factors
    MANURE_DAIRY_LACTATING_COWS_N_EF(57.488),
    MANURE_DAIRY_MATURE_COWS_N_EF(35.642),
    MANURE_DAIRY_GROWING_COWS_N_EF(25.180),
    MANURE_SHEEP_N_EF(3.922),
    MANURE_GOATS_N_EF(3.971),
    MANURE_SWINE_N_EF(10.428),
    MANURE_POULTRY_N_EF(1.064),
    MANURE_RABBITS_N_EF(8.308),

    //Mean losses of N in manure in MMS
    MANURE_DAIRY_LACTATING_COWS_N_LOST(0.75),
    MANURE_DAIRY_MATURE_COWS_N_LOST(0.75),
    MANURE_DAIRY_GROWING_COWS_N_LOST(0.75),
    MANURE_SHEEP_N_LOST(0.86),
    MANURE_GOATS_N_LOST(0.76),
    MANURE_SWINE_N_LOST(0.86),
    MANURE_POULTRY_N_LOST(0.75),
    MANURE_RABBITS_N_LOST(0.89),

    //Emission factor for manure/compost applied in fields
    MANURE_DAIRY_LACTATING_COWS_EF_COMPOST_MANURE(0.01),
    MANURE_DAIRY_MATURE_COWS_EF_COMPOST_MANURE(0.01),
    MANURE_DAIRY_GROWING_COWS_EF_COMPOST_MANURE(0.01),
    MANURE_SHEEP_EF_COMPOST_MANURE(0.01),
    MANURE_GOATS_EF_COMPOST_MANURE(0.01),
    MANURE_SWINE_EF_COMPOST_MANURE(0.01),
    MANURE_POULTRY_EF_COMPOST_MANURE(0.01),
    MANURE_RABBITS_EF_COMPOST_MANURE(0.01),

    //Liming Constants
    LIMESTONE(0.12),
    DOLOMITE(0.13),

    //Urea Constants
    UREA_EMISSION_FACTOR(0.2),

    //Aquaculture Constants
    FISH_N20_EF(0.0169),

    //Synthetic Fertilizer Constants
    N_CONTENT_UREA(0.46),
    N_CONTENT_NPK(0.17),
    ANNUAL_CROPS_ON_HILLS_N2O_EF(0.01),
    FLOODED_RICE_N2O_EF(0.005),
    N20_CONVERSION_FACTOR(265.0),

    //Rice Cultivation Constants
    EFC(2.2),              // Baseline emission factor
    SFP(1.9),              // Scaling factor for pre-season water regime
    ROA(5.0),                // Rate of organic amendment in t/ha
//    SFOA(1 + 5 * 0.59),    // Computed as per formula: 1 + Sum(ROAi * SFOAi * 0.59)
    SFOA(1.0),           // Scaling factor for organic amendment
    SFSR(1.0),               // Scaling factor for soil type or cultivar

    CONVERSION_FACTOR(3.6667),

    //Combustion Factors
    EUCALYPTUS_FOREST_CF(0.63),
    OTHER_FOREST_CF(0.59),

    //CO2 Emission Factors
    SAVANNA_AND_GRASSLAND_CO2(1613.0),
    AGRICULTURAL_RESIDUES_CO2(1515.0),
    FOREST_CO2(1569.0),
    BIOFUEL_BURNING_CO2(1550.0),

    //CH4 Emission Factors
    SAVANNA_AND_GRASSLAND_CH4(2.3),
    AGRICULTURAL_RESIDUES_CH4(2.7),
    FOREST_CH4(4.7),
    BIOFUEL_BURNING_CH4(6.1),

    //N2O Emission Factors
    SAVANNA_AND_GRASSLAND_N2O(0.21),
    AGRICULTURAL_RESIDUES_N2O(0.07),
    FOREST_N2O(0.26),
    BIOFUEL_BURNING_N2O(0.06),

    //Fuel Biomass Consumption Values
    EUCALYPTUS_FOREST_FBC(3.5),
    OTHER_FOREST_FBC(2.5);
    
    private final Double value;

    AFOLUConstants(Double value) {
        this.value = value;
    }
}
