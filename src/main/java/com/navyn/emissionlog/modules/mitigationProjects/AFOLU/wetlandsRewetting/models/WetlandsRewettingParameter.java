package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandsRewetting.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "wetlands_rewetting_parameters")
public class WetlandsRewettingParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "ch4_emission_factor_per_ha_per_year", nullable = false)
    private Double ch4EmissionFactorPerHaPerYear; // tonnes CH4/ha/year

    @Column(name = "gwp_methane", nullable = false)
    private Double gwpMethane; // dimensionless (e.g. 25, 27.9)

    @Column(name = "carbon_sequestration_factor_per_ha_per_year", nullable = false)
    private Double carbonSequestrationFactorPerHaPerYear; // tonnes C/year/ha

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
