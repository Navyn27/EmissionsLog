package com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "epr_plastic_waste_mitigation", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"year"})
})
@Data
public class EPRPlasticWasteMitigation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Integer year;
    
    // User inputs
    @Column(nullable = false)
    private Double bauSolidWasteEmissions; // ktCO2eq
    
    @Column(nullable = false)
    private Double plasticWasteGrowthFactor; // multiplier (e.g., 1.05 for 5% growth)
    
    @Column(nullable = false)
    private Double recyclingRateWithEPR; // percentage as decimal (e.g., 0.15 for 15%)
    
    // Optional: Base plastic waste for first year or override
    @Column
    private Double plasticWasteBaseTonnesPerYear; // t/year (optional, used for first year)
    
    // Calculated fields
    @Column(nullable = false)
    private Double plasticWasteTonnesPerYear; // t/year
    
    @Column(nullable = false)
    private Double recyclingWithoutEPRTonnesPerYear; // t/year (3% baseline)
    
    @Column(nullable = false)
    private Double recycledPlasticWithEPRTonnesPerYear; // t/year
    
    @Column(nullable = false)
    private Double additionalRecyclingVsBAUTonnesPerYear; // t/year
    
    @Column(nullable = false)
    private Double ghgReductionTonnes; // tCO2eq
    
    @Column(nullable = false)
    private Double ghgReductionKilotonnes; // ktCO2eq
}
