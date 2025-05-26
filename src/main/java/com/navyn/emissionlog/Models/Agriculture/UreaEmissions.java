package com.navyn.emissionlog.Models.Agriculture;

import com.navyn.emissionlog.Enums.Fertilizers;
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

