package com.navyn.emissionlog.Models.Agriculture;

import com.navyn.emissionlog.Enums.LivestockSpecies;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "enteric_fermentation")
public class EntericFermentationEmissions {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private LivestockSpecies species;

    @Column(unique = true)
    private int year;
    private double animalPopulation;
    private double CH4Emissions;
    private double CO2EqEmissions;
}

