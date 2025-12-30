package com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.navyn.emissionlog.modules.mitigationProjects.intervention.Intervention;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "iswm_mitigations")
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
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_intervention_id", nullable = true)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Intervention projectIntervention; // Foreign key to Intervention table
    
    // Calculated fields
    @Column(nullable = false)
    private Double dofDiverted; // tonnes
    
    @Column(nullable = false)
    private Double avoidedLandfill; // kgCO₂e
    
    @Column(nullable = false)
    private Double compostingEmissions; // kgCO₂e
    
    @Column(nullable = false)
    private Double netAnnualReduction; // ktCO₂e
    
    @Column(nullable = false, name = "mitigation_scenario_emission")
    private Double mitigationScenarioEmission; // ktCO₂e
}

