package com.navyn.emissionlog.modules.agricultureEmissions.dtos.AgriculturalLand.DirectLand;

import com.navyn.emissionlog.Enums.Agriculture.LandUseCategory;
import lombok.Data;

@Data
public class MineralSoilEmissionsDto {
    public Integer year;
    public LandUseCategory initialLandUse;
    public LandUseCategory landUseInReportingYear;
    public double avLossOfSoilC = 0.0;

}
