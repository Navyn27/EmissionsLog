package com.navyn.emissionlog.Payload.Requests.Agriculture;

import com.navyn.emissionlog.Enums.LivestockSpecies;
import lombok.Data;

@Data
public class EntericFermentationEmissionsDto {
    private LivestockSpecies species;
    private int year;
    private double animalPopulation;
}
