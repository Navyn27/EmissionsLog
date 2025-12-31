package com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "water_heat_parameter")
public class WaterHeatParameter {

    @Id
    @GeneratedValue(generator = "UUID")
    @org.hibernate.annotations.GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(updatable = false, nullable = false)
    private UUID id;

    // averageWaterHeat moved to AvoidedElectricityProduction - now user input per record

    @Column(nullable = false)
    private int deltaTemperature;

    @Column(nullable = false)
    private int specificHeatWater;
    
    // Grid emission factor - dynamic (moved from GridEmissionFactorService)
    @Column(nullable = false)
    private Double gridEmissionFactor; // tCO2/MWh

    @Column
    private double avoidedElectricityPerHousehold;

    private Boolean isActive = true;

    // Updated to accept averageWaterHeat as parameter
    @Transient
    public double getAvoidedElectricityPerHousehold(int averageWaterHeat) {
        return (averageWaterHeat * deltaTemperature * specificHeatWater * 365.0) / (3600000 * 1000.0);
    }

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public WaterHeatParameter(int deltaTemperature, int specificHeatWater, Double gridEmissionFactor) {
        this.deltaTemperature = deltaTemperature;
        this.specificHeatWater = specificHeatWater;
        this.gridEmissionFactor = gridEmissionFactor;
    }
}
