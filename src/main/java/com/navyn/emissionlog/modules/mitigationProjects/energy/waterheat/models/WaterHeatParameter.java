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

    @Column(nullable = false)
    private int averageWaterHeat;

    @Column(nullable = false)
    private int deltaTemperature;

    @Column(nullable = false)
    private int specificHeatWater;

    @Transient
    public double getAvoidedElectricityPerHousehold() {
        return (averageWaterHeat * deltaTemperature * specificHeatWater * 365.0) / (3600000 * 1000.0);
    }

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public WaterHeatParameter(int averageWaterHeat, int deltaTemperature, int specificHeatWater) {
        this.averageWaterHeat = averageWaterHeat;
        this.deltaTemperature = deltaTemperature;
        this.specificHeatWater = specificHeatWater;
    }
}
