package com.navyn.emissionlog.Models;

import com.navyn.emissionlog.Enums.Countries;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "population_records")
@Data
public class PopulationRecords {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private Long population;

    @Column(unique = true)
    private int year;

    private Double annualGrowth;

    @Enumerated(EnumType.STRING)
    private Countries country;

    private BigDecimal GDPMillions;

    private int numberOfKigaliHouseholds;

    private BigDecimal GDPPerCapita;

    private BigDecimal kigaliGDP;
}
