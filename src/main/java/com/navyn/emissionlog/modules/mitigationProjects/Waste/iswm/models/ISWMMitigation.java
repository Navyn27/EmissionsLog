package com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.navyn.emissionlog.modules.mitigationProjects.intervention.Intervention;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

/**
 * ISWM (Integrated Solid Waste Management) Mitigation Entity
 * 
 * This entity represents a mitigation project record for Integrated Solid Waste Management.
 * 
 * Data Sources:
 * - Parameters (degradableOrganicFraction, landfillAvoidance, compostingEF) come from ISWMParameter (latest active)
 * - BAU value comes from BAU table (sector: WASTE, same year) - not stored in this entity
 * - Project Intervention is a foreign key reference to Intervention table
 * 
 * Calculation Notes:
 * - All calculated fields are automatically computed based on user inputs and parameters
 * - NetAnnualReduction and MitigationScenarioEmission are stored in ktCO₂e to match BAU unit
 * - No unique constraint on year - multiple records per year are allowed
 */
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
    private Double dofDiverted; // tonnes - calculated as: wasteProcessed × (degradableOrganicFraction / 100)
    
    @Column(nullable = false)
    private Double avoidedLandfill; // kgCO₂e - calculated as: wasteProcessed × landfillAvoidance
    
    @Column(nullable = false)
    private Double compostingEmissions; // kgCO₂e - calculated as: dofDiverted × compostingEF
    
    @Column(nullable = false)
    private Double netAnnualReduction; // ktCO₂e - calculated as: (avoidedLandfill - compostingEmissions) / 1000 / 1000
    
    @Column(nullable = false, name = "mitigation_scenario_emission")
    private Double mitigationScenarioEmission; // ktCO₂e - calculated as: BAU - netAnnualReduction
}

