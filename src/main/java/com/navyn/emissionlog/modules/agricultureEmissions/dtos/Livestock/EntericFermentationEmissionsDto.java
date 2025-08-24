package com.navyn.emissionlog.modules.agricultureEmissions.dtos.Livestock;

import com.navyn.emissionlog.Enums.Agriculture.LivestockSpecies;
import lombok.Data;

@Data
public class EntericFermentationEmissionsDto {
    private LivestockSpecies species;
    private int year;
    private double animalPopulation;
}
