package com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.constants;

import lombok.Getter;

@Getter
public enum WasteToEnergyConstants {
    NET_EMISSION_FACTOR(0.7); // tCO2eq/t
    
    private final double value;
    
    WasteToEnergyConstants(double value) {
        this.value = value;
    }
}
