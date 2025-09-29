package com.navyn.emissionlog.modules.LandUseEmissions.Dtos;

import com.navyn.emissionlog.Enums.LandUse.LandCategory;
import lombok.Data;

@Data
public class FirewoodRemovalBiomassLossDto {
    private Integer year;
    private LandCategory landCategory;
    private double removedFirewoodAmount;
}
