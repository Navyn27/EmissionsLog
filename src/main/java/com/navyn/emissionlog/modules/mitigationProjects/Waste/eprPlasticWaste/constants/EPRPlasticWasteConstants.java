package com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.constants;

import lombok.Getter;

@Getter
public enum EPRPlasticWasteConstants {
    EMISSION_FACTOR(2.60), // tCO2eq
    RECYCLING_RATE_WITHOUT_EPR(0.03); // 3%
    
    private final double value;
    
    EPRPlasticWasteConstants(double value) {
        this.value = value;
    }
}
