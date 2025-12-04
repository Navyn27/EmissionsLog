package com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.constants;

import lombok.Getter;

@Getter
public enum WWTPProjectPhase {
    NONE("None", 0.0),
    PHASE_I("Phase I", 12000.0),
    PHASE_II("Phase II", 20000.0),
    PHASE_III("Phase III", 50000.0);
    
    private final String displayName;
    private final double capacityPerDay; // m³/day
    
    WWTPProjectPhase(String displayName, double capacityPerDay) {
        this.displayName = displayName;
        this.capacityPerDay = capacityPerDay;
    }
}
