package com.navyn.emissionlog.modules.agricultureEmissions.dtos.Livestock;

import com.navyn.emissionlog.Enums.Agriculture.LivestockSpecies;
import com.navyn.emissionlog.Enums.Agriculture.OrganicAmendmentTypes;
import lombok.Data;

@Data
public class ManureMgmtEmissionsDto {
    private LivestockSpecies species;
    private int year;
    private double population;
    private OrganicAmendmentTypes amendmentType;
}
