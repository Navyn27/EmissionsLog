package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Table
@Entity(name = "wetland_parks_parameters")
public class WetlandParksParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private Double conversionM3ToTonnesDM; // Conversion m3 to tonnes dry matter (DM)
    private Double ratioOfBelowGroundBiomass; // Ratio of belowground biomass BGB to AGB
    private Double carbonContentDryWood; // Carbon content in dry wood
    private Double carbonToC02; // C to CO2 conversion
    private Boolean isActive = true;
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

