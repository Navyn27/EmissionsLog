package com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "landfill_gas_parameters")
@Data
public class LandfillGasParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, name = "destruction_efficiency_percentage")
    private Double destructionEfficiencyPercentage; // Percentage (0-100)

    @Column(nullable = false, name = "global_warming_potential_ch4")
    private Double globalWarmingPotentialCh4; // GWP for CH₄

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

