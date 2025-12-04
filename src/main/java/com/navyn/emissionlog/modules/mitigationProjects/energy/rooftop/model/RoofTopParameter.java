package com.navyn.emissionlog.modules.mitigationProjects.Energy.rooftop.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "rooftop_parameters")
public class RoofTopParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "solar_pv_capacity", nullable = false)
    private double solarPVCapacity;

    @Column(name = "energy_output", nullable = false)
    private double energyOutPut;

    @Column(name = "percentage_output_displaced_diesel", nullable = false)
    private double percentageOutPutDisplacedDiesel;

    @Column(name = "avoided_diesel_consumption", nullable = false)
    private double avoidedDieselConsumption;

    @Column(name = "diesel_energy_content", nullable = false)
    private double dieselEnergyContent;

    @Column(name = "genset_efficiency", nullable = false)
    private double gensetEfficiency;

    @Column(name = "constant_mj_per_mwh", nullable = false)
    private double constant; // MJ/MWh

    // --- TRANSIENT CALCULATIONS (Not saved to DB) ---
    @Transient
    private double avoidedDieselConsumptionCalculated;

    @Transient
    private double avoidedDieselConsumptionAverage;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
