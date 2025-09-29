package com.navyn.emissionlog.modules.LandUseEmissions.Dtos;

import com.navyn.emissionlog.Enums.LandUse.LandCategory;
import lombok.Data;

@Data
public class BiomassGainDto {
    private Integer year;
    private LandCategory landCategory;
    private double forestArea;
}
