package com.navyn.emissionlog.Enums.Agriculture;

import lombok.Getter;

@Getter
public enum Fertilizers {
    NPK(0.17),
    UREA(0.46);

    Fertilizers(double nContent){
        this.nContent = nContent;
    }

    private double nContent = 0.0;
}
