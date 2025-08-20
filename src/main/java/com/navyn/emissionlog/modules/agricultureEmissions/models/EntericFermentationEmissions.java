package com.navyn.emissionlog.modules.agricultureEmissions.models;

import com.navyn.emissionlog.Enums.LivestockSpecies;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

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

