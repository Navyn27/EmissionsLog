package com.navyn.emissionlog.modules.LandUseEmissions.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
public class RewettedMineralWetlands {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true)
    private Integer year = LocalDate.now().getYear();

    private double areaOfRewettedWetlands = 0.0;

    private double CH4Emissions = 0.0;

    private double CO2EqEmissions = 0.0;
}
