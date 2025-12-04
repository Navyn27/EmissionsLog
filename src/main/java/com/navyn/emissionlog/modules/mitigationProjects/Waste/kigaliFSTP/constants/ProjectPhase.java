package com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.constants;

import lombok.Getter;

@Getter
public enum ProjectPhase {
    NONE("None"),
    PHASE_I("Phase I"),
    PHASE_II("Phase II"),
    PHASE_III("Phase III");
    
    private final String displayName;
    
    ProjectPhase(String displayName) {
        this.displayName = displayName;
    }
}
