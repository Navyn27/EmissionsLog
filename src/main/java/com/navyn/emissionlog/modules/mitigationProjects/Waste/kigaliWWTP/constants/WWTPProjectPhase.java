package com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.constants;

import lombok.Getter;

@Getter
public enum WWTPProjectPhase {
    NONE(0.0, "None"),
    PHASE_I(12000.0, "Phase I"),
    PHASE_II(20000.0, "Phase II"),
    PHASE_III(50000.0, "Phase III");
    
    private final double capacityPerDay; // m³/day
    private final String displayName;
    
    WWTPProjectPhase(double capacityPerDay, String displayName) {
        this.capacityPerDay = capacityPerDay;
        this.displayName = displayName;
    }
}
