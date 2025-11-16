package com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.constants;

import lombok.Getter;

@Getter
public enum MBTCompostingConstants {
    EMISSION_FACTOR(1.12); // tCO2eq/ton
    
    private final double value;
    
    MBTCompostingConstants(double value) {
        this.value = value;
    }
}
