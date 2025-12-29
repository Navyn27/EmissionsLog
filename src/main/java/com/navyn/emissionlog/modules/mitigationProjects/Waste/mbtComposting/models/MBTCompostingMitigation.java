package com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.navyn.emissionlog.modules.mitigationProjects.intervention.Intervention;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "mbt_composting_mitigation", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"year"})
})
@Data
public class MBTCompostingMitigation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    private Integer year;
    
    // User inputs
    @Column(nullable = false, name = "organic_waste_treated_tons_per_year")
    private Double organicWasteTreatedTonsPerYear; // tons/year
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_intervention_id", nullable = true)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Intervention projectIntervention; // Foreign key to Intervention table
    
    // Calculated fields
    @Column(nullable = false, name = "estimated_ghg_reduction_tonnes_per_year")
    private Double estimatedGhgReductionTonnesPerYear; // tCO2eq/year
    
    @Column(nullable = false, name = "estimated_ghg_reduction_kilotonnes_per_year")
    private Double estimatedGhgReductionKilotonnesPerYear; // ktCO2eq/year
    
    @Column(nullable = false, name = "adjusted_bau_emission_biological_treatment")
    private Double adjustedBauEmissionBiologicalTreatment; // ktCO2eq/year
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
