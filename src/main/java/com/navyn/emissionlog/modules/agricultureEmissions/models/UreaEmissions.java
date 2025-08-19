package com.navyn.emissionlog.modules.agricultureEmissions.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "urea_emissions")
public class UreaEmissions {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String fertilizerName;

    @Column(unique = true)
    private int year;
    private double qty;
    private double CO2Emissions;
}

