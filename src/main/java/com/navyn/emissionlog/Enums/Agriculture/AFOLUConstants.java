package com.navyn.emissionlog.Enums.Agriculture;

import lombok.Getter;

@Getter
public enum AFOLUConstants {

    //Urea Constants
    UREA_EMISSION_FACTOR(0.2),

    //Aquaculture Constants
    FISH_N20_EF(0.0169),

    //Rice Cultivation Constants
    EFC(2.2),              // Baseline emission factor
    SFP(1.9),              // Scaling factor for pre-season water regime
    ROA(5.0),                // Rate of organic amendment in t/ha
    SFOA(1.0),           // Scaling factor for organic amendment
    SFSR(1.0),               // Scaling factor for soil type or cultivar

    CONVERSION_FACTOR(3.6667),

    //Combustion Factors
    EUCALYPTUS_FOREST_CF(0.63),
    OTHER_FOREST_CF(0.59),

    //Fuel Biomass Consumption Values
    EUCALYPTUS_FOREST_FBC(3.5),
    OTHER_FOREST_FBC(2.5),

    //Atmopsheric N Deposition Emission Factor
    FRACTION_OF_APPLIED_ORGANIC_N_EXCRETIONS_THAT_VOLATILIZES(0.2),
    EF_N2O_AtmoNDeposition(0.01),

    //Crop Residue Emissions Factors
    N_CROP_RESIDUES_EF(0.01);

    private final Double value;

    AFOLUConstants(Double value) {
        this.value = value;
    }
}
