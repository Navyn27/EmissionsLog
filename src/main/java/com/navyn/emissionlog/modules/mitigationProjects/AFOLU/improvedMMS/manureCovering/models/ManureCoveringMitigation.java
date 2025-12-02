package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "manure_covering_mitigation", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"year"})
})
@Data
public class ManureCoveringMitigation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    private Integer year;
    
    // User input
    @Column(nullable = false)
    private Integer numberOfCows;
    
    // Calculated fields
    @Column(nullable = false)
    private Double n2oEmissions; // tonnes CO2e/year
    
    @Column(nullable = false)
    private Double n2oReduction; // tonnes CO2e (30%)
    
    @Column(nullable = false)
    private Double mitigatedN2oEmissionsKilotonnes; // ktCO2e/year
}
