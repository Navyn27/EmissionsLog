package com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.navyn.emissionlog.modules.mitigationProjects.intervention.Intervention;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "light_bulbs")
public class LightBulb {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "year", nullable = false)
    private int year;

    @Column(name = "total_installed_bulbs_per_year", nullable = false)
    private double totalInstalledBulbsPerYear;

    @Column(name = "reduction_capacity_per_bulb", nullable = false)
    private double reductionCapacityPerBulb;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_intervention_id", nullable = true)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Intervention projectIntervention; // Foreign key to Intervention table

    // Calculated Fields
    @Column(name = "total_reduction_per_year")
    private double totalReductionPerYear;

    @Column(name = "net_ghg_mitigation_achieved")
    private double netGhGMitigationAchieved;

    @Column(name = "scenario_ghg_mitigation_achieved")
    private double scenarioGhGMitigationAchieved;

    @Column(name = "adjusted_bau_emission_mitigation")
    private double adjustedBauEmissionMitigation; // BAU - netGhGMitigationAchieved

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
