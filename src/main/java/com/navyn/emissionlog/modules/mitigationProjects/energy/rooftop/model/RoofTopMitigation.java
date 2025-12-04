package com.navyn.emissionlog.modules.mitigationProjects.Energy.rooftop.model;

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


    @Column(name = "cumulative_installed_unit_per_year", nullable = false)
    private int cumulativeInstalledUnitPerYear;  // calculated

    @Transient
    private int percentageOfFinalMaximumRate; // calculated

    @Transient
    private double dieselDisplacedInMillionLitterPerArea; // calculated

    @Transient
    private double dieselDisplacedInTonJoule; // calculated

    @Column(name = "bau_emission_without_project", nullable = false)
    private double bauEmissionWithoutProject; // business As Usual

    @Column(name = "net_ghg_mitigation_achieved", nullable = false)
    private double netGhGMitigationAchieved;

    @Column(name = "scenario_ghg_emission_with_project", nullable = false)
    private double scenarioGhGEmissionWithProject;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
