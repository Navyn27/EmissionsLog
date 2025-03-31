package com.navyn.emissionlog.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.navyn.emissionlog.Enums.FuelTypes;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
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
    private FuelTypes fuelTypes;

    private String fuelDescription; // Description of the fuel

    @Column(nullable = false, unique = true)
    private String fuel; // Fuel type (e.g., Diesel, Natural Gas)

    @Column(nullable = false)
    private Double lowerHeatingValue; // LHV (TJ/Gg) or NCV

    @Column(nullable = true)
    private Double liquidDensity = 0.0; // kg/litre (only for liquid fuels)

    @Column(nullable = true)
    private Double gasDensity = 0.0; // kg/m³ (only for gaseous fuels)

    @OneToMany(mappedBy = "fuel", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnore
    @ToString.Exclude
    private List<StationaryEmissionFactors> stationaryEmissionFactorsList = new ArrayList<>();

    @OneToMany
    @JsonIgnore
    private List<TransportFuelEmissionFactors> transportFuelEmissionFactors = new ArrayList<>();
}
