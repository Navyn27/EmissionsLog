package com.navyn.emissionlog.modules.LandUseEmissions.Dtos;

import com.navyn.emissionlog.Enums.LandUse.LandCategory;
import lombok.Data;

@Data
public class DisturbanceBiomassLossDto {
    private Integer year;
    private LandCategory landCategory;
    private double affectedForestArea;
    private double areaAffectedByDisturbance;
}
