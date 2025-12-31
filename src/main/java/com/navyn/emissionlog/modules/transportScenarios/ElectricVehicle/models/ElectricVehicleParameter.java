package com.navyn.emissionlog.modules.transportScenarios.ElectricVehicle.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "electric_vehicle_parameters")
public class ElectricVehicleParameter {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @NotNull(message = "Grid Emission Factor is required")
    @Positive(message = "Grid Emission Factor must be positive")
    @Column(nullable = false, name = "grid_emission_factor")
    private Double gridEmissionFactor; // tonne CO₂/MWh
    
    @Column(nullable = false, name = "is_active")
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
