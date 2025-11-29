package com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.constants;

import lombok.Getter;

@Getter
public enum OperationStatus {
    PRE_OPERATION("Pre-Operation / Construction", 0.0),
    HALF_YEAR_OPERATION("Half Year Operation", 182.5), // 365/2
    FULL_YEAR_OPERATION("Full Year Operation", 365.0);
    
    private final String displayName;
    private final double daysPerYear;
    
    OperationStatus(String displayName, double daysPerYear) {
        this.displayName = displayName;
        this.daysPerYear = daysPerYear;
    }
}
