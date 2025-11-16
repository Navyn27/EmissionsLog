package com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.constants;

import lombok.Getter;

@Getter
public enum OperationStatus {
    CONSTRUCTION_PRE_OP(0.0, "Construction/Pre-op"),
    HALF_YEAR_OPERATION(182.5, "Half-year operation"),  // 365/2
    FULL_OPERATION(365.0, "Full operation");
    
    private final double daysPerYear;
    private final String displayName;
    
    OperationStatus(double daysPerYear, String displayName) {
        this.daysPerYear = daysPerYear;
        this.displayName = displayName;
    }
}
