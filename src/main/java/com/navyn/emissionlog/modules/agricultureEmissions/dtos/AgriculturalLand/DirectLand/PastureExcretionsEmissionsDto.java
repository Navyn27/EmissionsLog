package com.navyn.emissionlog.modules.agricultureEmissions.dtos.AgriculturalLand.DirectLand;

import com.navyn.emissionlog.Enums.Agriculture.LivestockSpecies;
import com.navyn.emissionlog.Enums.Agriculture.MMS;
import lombok.Data;

@Data
public class PastureExcretionsEmissionsDto {
    private Integer year;
    private MMS mms;
    private LivestockSpecies livestockSpecies;
    private double animalPopulation = 0.0;
}
