package com.navyn.emissionlog.Enums.Agriculture;

public enum CropTypes {
    ANNUAL_CROPS_ON_HILLS(0.01),
    FLOODED_RICE(0.005);

    CropTypes(double EF){
        this.N2OEF = EF;
    }

    private double N2OEF = 0.0;

}
