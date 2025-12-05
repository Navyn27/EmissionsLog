package com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "iswm_mitigation", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"year"})
})
@Data
public class ISWMMitigation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    private Integer year;
    
    // User inputs
    @Column(nullable = false)
    private Double wasteProcessed; // tonnes
    
    @Column(nullable = false)
    private Double degradableOrganicFraction; // percentage (0-100)
    
    @Column(nullable = false)
    private Double landfillAvoidance; // kgCO₂e/tonne
    
    @Column(nullable = false)
    private Double compostingEF; // kgCO₂e/tonne of DOF
    
    @Column(nullable = false)
    private Double bauEmission; // tCO₂e
    
    // Calculated fields
    @Column(nullable = false)
    private Double dofDiverted; // tonnes
    
    @Column(nullable = false)
    private Double avoidedLandfill; // kgCO₂e
    
    @Column(nullable = false)
    private Double compostingEmissions; // kgCO₂e
    
    @Column(nullable = false)
    private Double netAnnualReduction; // tCO₂e
    
    @Column(nullable = false)
    private Double mitigationScenarioEmission; // tCO₂e
}
