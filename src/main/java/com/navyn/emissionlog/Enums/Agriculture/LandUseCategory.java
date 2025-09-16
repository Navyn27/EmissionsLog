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
}
