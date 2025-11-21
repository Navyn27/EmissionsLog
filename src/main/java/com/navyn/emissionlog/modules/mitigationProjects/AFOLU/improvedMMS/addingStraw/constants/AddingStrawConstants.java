package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.constants;

import lombok.Getter;

@Getter
public enum AddingStrawConstants {
    CH4_EMISSIONS_PER_COW_ADDING_STRAW(0.781),      // tonnes CO2e/cow/year
    CH4_REDUCTION_RATE_STRAW(0.3);                  // 30% reduction with straw addition
    
    private final Double value;
    
    AddingStrawConstants(Double value) {
        this.value = value;
    }
}
