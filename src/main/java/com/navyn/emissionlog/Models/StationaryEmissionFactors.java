package com.navyn.emissionlog.Models;

import com.navyn.emissionlog.Enums.Emissions;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "emission_factors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StationaryEmissionFactors {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private Emissions emmission;

    @ManyToOne
    @JoinColumn(name = "fuel_id", nullable = false)
    @ToString.Exclude
    private Fuel fuel; // Relation to Fuel entity

    @Column(nullable = false)
    private double energyBasis; // kg CO₂/TJ

    @Column(nullable = false)
    private double massBasis; // kg CO₂/tonne

    @Column(nullable = true)
    private Double liquidBasis; // kg CO₂/litre (for liquid fuels)

    @Column(nullable = true)
    private Double gasBasis; // kg CO₂/m³ (for gaseous fuels)
}
