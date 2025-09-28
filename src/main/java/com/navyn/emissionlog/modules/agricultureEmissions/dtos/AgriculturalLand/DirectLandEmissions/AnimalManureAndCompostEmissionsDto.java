package com.navyn.emissionlog.modules.agricultureEmissions.dtos.AgriculturalLand.DirectLandEmissions;

import com.navyn.emissionlog.Enums.Agriculture.LivestockSpecies;
import com.navyn.emissionlog.Enums.Agriculture.OrganicAmendmentTypes;
import lombok.Data;

@Data
public class AnimalManureAndCompostEmissionsDto {
    private int year;
    private double population;
    private LivestockSpecies species;
}
