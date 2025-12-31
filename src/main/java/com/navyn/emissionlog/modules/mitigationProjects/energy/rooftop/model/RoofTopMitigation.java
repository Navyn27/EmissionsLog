package com.navyn.emissionlog.modules.mitigationProjects.energy.rooftop.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.navyn.emissionlog.modules.mitigationProjects.intervention.Intervention;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "roofTop_mitigations")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoofTopMitigation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "year", nullable = false)
    private int year;

    @Column(name = "installed_unit_per_year", nullable = false)
    private int installedUnitPerYear;

    @Column(name = "solar_pv_capacity", nullable = false)
    private double solarPVCapacity;

    @Column(name = "cumulative_installed_unit_per_year", nullable = false)
    private int cumulativeInstalledUnitPerYear;  // calculated

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_intervention_id", nullable = true)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Intervention projectIntervention; // Foreign key to Intervention table

    @Transient
    private int percentageOfFinalMaximumRate; // calculated

    @Transient
    private double dieselDisplacedInMillionLitterPerArea; // calculated

    @Transient
    private double dieselDisplacedInTonJoule; // calculated

    @Transient
    private double bauEmissionWithoutProject; // fetched from BAU table

    @Column(name = "net_ghg_mitigation_achieved", nullable = false)
    private double netGhGMitigationAchieved;

    @Column(name = "scenario_ghg_emission_with_project", nullable = false)
    private double scenarioGhGEmissionWithProject;

    @Column(name = "adjusted_bau_emission_mitigation", nullable = false)
    private double adjustedBauEmissionMitigation; // BAU - netGhGMitigationAchieved

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
