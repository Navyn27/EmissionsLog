package com.navyn.emissionlog.Models;

import com.navyn.emissionlog.Enums.FuelState;
import com.navyn.emissionlog.Enums.Metric;
import com.navyn.emissionlog.Enums.Scopes;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    private Sector sector;

    @ManyToOne
    @JoinColumn(name = "emission_factors_list", nullable = false)
    private Fuel fuel;

    private Scopes scope;

    private Double fuelAmount;

    private FuelState fuelState;

    @Enumerated(EnumType.STRING)
    private Metric metric;

    private Double CH4Emissions = 0.0;

    private Double FossilCO2Emissions = 0.0;

    private Double BiomassCO2Emissions = 0.0;

    private Double N2OEmissions = 0.0;
}