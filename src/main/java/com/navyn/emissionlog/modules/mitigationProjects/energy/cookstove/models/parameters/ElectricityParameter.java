package com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.models.parameters;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "electricity_parameters")
public class ElectricityParameter {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Double fuelConsumption;//Per capita fuel consumption by a baseline device MWh
    private Double emissionFactor; //CO2 emission factor  tCO2/MWh
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
