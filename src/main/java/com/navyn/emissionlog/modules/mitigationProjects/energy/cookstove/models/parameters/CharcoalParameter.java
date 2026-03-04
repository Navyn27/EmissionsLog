package com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.models.parameters;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;
@Data
@Entity
@Table(name = "charcoal_parameters")
public class CharcoalParameter {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Double netCalorificValue; //Net calorific value TJ/t
    private Double emissionFactor; //CO2 emission factor  TJ/t
    private Double adjustedEmissionFactor; //Non CO2 emission factor  TJ/t
    private Double biomass;//Faction of non-renewable biomass %
    private Double fuelConsumption;//Per capita fuel consumption by a baseline device tonnes
    private Double efficiency;//Baseline efficiency %
    private Double size;//HH size no unit

    private Boolean isActive = true;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column
    private LocalDateTime updatedAt;
}
