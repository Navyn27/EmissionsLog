package com.navyn.emissionlog.modules.agricultureEmissions.models.Livestock;

import com.navyn.emissionlog.Enums.Agriculture.LivestockSpecies;
import com.navyn.emissionlog.modules.agricultureEmissions.models.AgricultureAbstractClass;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "enteric_fermentation")
public class EntericFermentationEmissions extends AgricultureAbstractClass {
    @Enumerated(EnumType.STRING)
    private LivestockSpecies species;

    private double animalPopulation;
    private double CH4Emissions;
    private double CO2EqEmissions;
}