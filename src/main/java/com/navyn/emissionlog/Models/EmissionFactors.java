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
public class EmissionFactors {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private Emissions emmission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fuel_id", nullable = false)
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
