package com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "kigali_fstp_parameters")
@Data
public class KigaliFSTPParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, name = "methane_emission_factor")
    private Double methaneEmissionFactor; // kg CH4 per kg COD

    @Column(nullable = false, name = "cod_concentration")
    private Double codConcentration; // kg COD per m³

    @Column(nullable = false, name = "ch4_gwp_100_year")
    private Double ch4Gwp100Year; // kg CO2e per kg CH4

    @Column(nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column
    private LocalDateTime updatedAt;
}

