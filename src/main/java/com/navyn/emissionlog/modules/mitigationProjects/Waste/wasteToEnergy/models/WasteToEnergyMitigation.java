package com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.navyn.emissionlog.modules.mitigationProjects.intervention.Intervention;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "waste_to_energy_mitigation", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"year"})
})
@Data
public class WasteToEnergyMitigation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    private Integer year;
    
    // User inputs
    @Column(nullable = false)
    private Double wasteToWtE; // t/year
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_intervention_id", nullable = true)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Intervention projectIntervention; // Foreign key to Intervention table
    
    // Calculated fields
    @Column(nullable = false)
    private Double ghgReductionTonnes; // tCO2eq
    
    @Column(nullable = false)
    private Double ghgReductionKilotonnes; // ktCO2eq
    
    @Column(nullable = false)
    private Double adjustedEmissionsWithWtE; // ktCO2e
}
