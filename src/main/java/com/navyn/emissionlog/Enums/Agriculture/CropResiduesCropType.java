package com.navyn.emissionlog.Enums.Agriculture;

import lombok.Getter;

@Getter
public enum CropResiduesCropType {

    //Abbreviations used

    //DM : Dry Matter
    //RAG
    //N : Nitrogen
    //AG : Above Ground
    //BGR : Below Ground Residues

    MAIZE_GRAINS(0.88, 0.006, 0.8, 0.22, 0.009, 0.01),
    BEANS(0.91, 0.008, 0.8, 0.2, 0.008, 0.01),
    PULSES(0.91, 0.008, 0.8, 0.2, 0.008, 0.01),
    TUBERS(0.22, 0.019, 0.8, 0.2, 0.014, 0.01),
    RICE_GRAINS(0.89, 0.007, 0.8, 0.16, 0.009, 0.01);

    private double DMFraction = 0.0;
    private double NContentOfAGResidues = 0.0;
    private double fractionOfAGResiduesRemoved = 0.0;
    private double ratioOfBGRToAGBiomass = 0.0;
    private double NContentforBGR = 0.0;
    private double NEF = 0.0;

    CropResiduesCropType(double DMFraction, double NContentOfAGResidues, double fractionOfAGResiduesRemoved, double ratioOfBGRToAGBiomass,double NContentforBGR, double NEF) {
        this.DMFraction = DMFraction;
        this.NContentOfAGResidues = NContentOfAGResidues;
        this.fractionOfAGResiduesRemoved = fractionOfAGResiduesRemoved;
        this.ratioOfBGRToAGBiomass = ratioOfBGRToAGBiomass;
        this.NContentforBGR = NContentforBGR;
        this.NEF = NEF;
    }

}
