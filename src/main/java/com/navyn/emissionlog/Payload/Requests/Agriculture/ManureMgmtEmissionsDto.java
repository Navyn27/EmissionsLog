package com.navyn.emissionlog.Payload.Requests.Agriculture;

import com.navyn.emissionlog.Enums.LivestockSpecies;
import com.navyn.emissionlog.Enums.OrganicAmendmentTypes;
import lombok.Data;

@Data
public class ManureMgmtEmissionsDto {
    private LivestockSpecies species;
    private int year;
    private double population;
    private OrganicAmendmentTypes amendmentType;
}
