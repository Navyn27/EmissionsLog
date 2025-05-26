package com.navyn.emissionlog.Models.Agriculture;

import com.navyn.emissionlog.Enums.WaterRegime;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "rice_cultivation")
public class RiceCultivationEmissions {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String riceEcosystem;

    @Column(unique = true)
    private int year;

    @Enumerated(EnumType.STRING)
    private WaterRegime waterRegime;
    private double harvestedArea;
    private int cultivationPeriod;
    private double orgAmendScalingFactor;
    private double soilOrCultivarScaling;
    private double adjDailyEFEmissions;
    private double annualCH4Emissions;
}