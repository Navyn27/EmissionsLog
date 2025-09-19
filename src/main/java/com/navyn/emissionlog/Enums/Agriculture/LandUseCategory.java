package com.navyn.emissionlog.Enums.Agriculture;

import lombok.Getter;

@Getter
public enum LandUseCategory {
    CROPLAND,
    FORESTLAND,
    GRASSLAND,
    WETLAND,
    SETTLEMENTS,
    OTHER_LAND;

    private final double CNRatioOfSoilOrganicMatter = 15.0;
    private final double EFNMineralised = 0.01;
    private final double NFractionAddedToSoilPostLeaching = 0.3;
    private final double EF_N2O_LeachAndRunoffNSoilAdditive = 0.0075;

}
