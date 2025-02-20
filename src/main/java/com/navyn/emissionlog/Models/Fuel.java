package com.navyn.emissionlog.Models;

import com.navyn.emissionlog.Enums.FuelType;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "fuels")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fuel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FuelType fuelType;

    @Column(nullable = false, unique = true)
    private String fuel; // Fuel type (e.g., Diesel, Natural Gas)

    @Column(nullable = false)
    private Double lowerHeatingValue; // LHV (TJ/Gg) or NCV

    @Column(nullable = true)
    private Double liquidDensity = 0.0; // kg/litre (only for liquid fuels)

    @Column(nullable = true)
    private Double gasDensity = 0.0; // kg/m³ (only for gaseous fuels)

    @OneToMany(mappedBy = "fuel")
    private List<EmissionFactors> emissionFactorsList;
}
