package com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.enums.EStoveType;
import com.navyn.emissionlog.modules.mitigationProjects.intervention.Intervention;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "stove_mitigations")
public class StoveMitigation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // inputs + intervention
    @Enumerated(EnumType.STRING)
    private EStoveType stoveType;
    /**
     * Calendar year for this record.
     */
    private int year;

    /**
     * Total number of stoves installed up to and including this year.
     */
    private int unitsInstalled;
    private Double efficiency; // no units

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_intervention_id", nullable = true)
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private Intervention projectIntervention;

    // calculated fields
    private Double fuelConsumption; // no unit
    private Double projectEmission; // KtCO2e

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
