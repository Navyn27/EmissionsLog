package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.constants;

import lombok.Getter;

@Getter
public enum ManureCoveringConstants {
    N2O_EMISSIONS_PER_COW(21.5),        // tonnes CO2e/cow/year
    N2O_REDUCTION_RATE(0.3);            // 30% reduction with compaction and manure covering
    
    private final Double value;
    
    ManureCoveringConstants(Double value) {
        this.value = value;
    }
}
