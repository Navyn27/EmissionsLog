package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.streetTrees.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Table
@Entity(name = "street_trees_parameters")
public class StreetTreesParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private Double conversationM3ToTonnes; //Conversion m3 to tonnes dry matter (DM)
    private Double ratioOfBelowGroundBiomass; //Ratio of belowground biomass BGB to AGB
    private Double carbonContent;//Carbon content in dry increased biomass
    private Double carbonToC02;// C to CO2 conversion
    private Boolean isActive = true;
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
