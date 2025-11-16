package com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.constants;

import lombok.Getter;

@Getter
public enum ProjectPhase {
    NONE(0.0, "None"),
    PHASE_I(200.0, "Phase I"),
    PHASE_II(1000.0, "Phase II"),
    PHASE_III(1500.0, "Phase III");
    
    private final double capacityPerDay; // m³/day
    private final String displayName;
    
    ProjectPhase(double capacityPerDay, String displayName) {
        this.capacityPerDay = capacityPerDay;
        this.displayName = displayName;
    }
}
