package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Table(name = "manure_covering_parameters")
@Entity
public class ManureCoveringParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Double emissionPerCow; //N2O emissions per cow, tonnes CO2e/cow/year

    private Double reduction; // Reduction of N2O emissions, %

    private Boolean isActive = true;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
