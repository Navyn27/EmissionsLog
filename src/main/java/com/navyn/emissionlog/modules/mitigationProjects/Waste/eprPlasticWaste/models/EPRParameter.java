package com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "epr_parameters")
@Data
public class EPRParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, name = "recycling_rate_without_epr")
    private Double recyclingRateWithoutEPR; // percentage as decimal (e.g., 0.03 for 3%)

    @Column(nullable = false, name = "recycling_rate_with_epr")
    private Double recyclingRateWithEPR; // percentage as decimal (e.g., 0.15 for 15%)

    @Column(nullable = false, name = "emission_factor")
    private Double emissionFactor; // tCO2eq

    private Boolean isActive = true;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column
    private LocalDateTime updatedAt;
}

