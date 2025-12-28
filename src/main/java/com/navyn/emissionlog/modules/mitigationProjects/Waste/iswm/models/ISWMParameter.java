package com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "iswm_parameters")
@Data
public class ISWMParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, name = "degradable_organic_fraction")
    private Double degradableOrganicFraction; // percentage (0-100)

    @Column(nullable = false, name = "landfill_avoidance")
    private Double landfillAvoidance; // kgCO₂e/tonne

    @Column(nullable = false, name = "composting_ef")
    private Double compostingEF; // kgCO₂e/tonne of DOF

    private Boolean isActive = true;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column
    private LocalDateTime updatedAt;
}
