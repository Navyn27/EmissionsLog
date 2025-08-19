package com.navyn.emissionlog.modules.agricultureEmissions.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "aquaculture_emissions")
public class AquacultureEmissions {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(unique = true)
    private int year;

    private String activityDesc;
    private double fishProduction;
    private double N2ONEmissions;
    private double N2OEmissions;
    private double CO2EqEmissions;
}
