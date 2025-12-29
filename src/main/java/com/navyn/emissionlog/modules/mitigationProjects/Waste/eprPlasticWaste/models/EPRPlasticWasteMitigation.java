package com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.navyn.emissionlog.modules.mitigationProjects.intervention.Intervention;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "epr_plastic_waste_mitigation", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"year"})
})
@Data
public class EPRPlasticWasteMitigation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    private Integer year;
    
    // User inputs
    @Column(nullable = false, name = "plastic_waste_tonnes_per_year")
    private Double plasticWasteTonnesPerYear; // t/year
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_intervention_id", nullable = true)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Intervention projectIntervention; // Foreign key to Intervention table
    
    // Calculated fields
    @Column(nullable = false, name = "recycled_plastic_without_epr_tonnes_per_year")
    private Double recycledPlasticWithoutEPRTonnesPerYear; // t/year
    
    @Column(nullable = false, name = "recycled_plastic_with_epr_tonnes_per_year")
    private Double recycledPlasticWithEPRTonnesPerYear; // t/year
    
    @Column(nullable = false, name = "additional_recycling_vs_bau_tonnes_per_year")
    private Double additionalRecyclingVsBAUTonnesPerYear; // t/year
    
    @Column(nullable = false, name = "ghg_reduction_tonnes")
    private Double ghgReductionTonnes; // tCO2eq
    
    @Column(nullable = false, name = "ghg_reduction_kilotonnes")
    private Double ghgReductionKilotonnes; // ktCO2eq
    
    @Column(nullable = false, name = "adjusted_bau_emission_mitigation")
    private Double adjustedBauEmissionMitigation; // ktCO2e
}
