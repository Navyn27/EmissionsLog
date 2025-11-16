package com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.models;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.constants.OperationStatus;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "mbt_composting_mitigation", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"year"})
})
@Data
public class MBTCompostingMitigation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Integer year;
    
    // User inputs
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OperationStatus operationStatus;
    
    @Column(nullable = false)
    private Double organicWasteTreatedTonsPerDay; // tons/day
    
    @Column(nullable = false)
    private Double bauEmissionBiologicalTreatment; // ktCO2eq
    
    // Calculated fields
    @Column(nullable = false)
    private Double organicWasteTreatedTonsPerYear; // tons/year
    
    @Column(nullable = false)
    private Double estimatedGhgReductionTonnesPerYear; // tCO2eq/year
    
    @Column(nullable = false)
    private Double estimatedGhgReductionKilotonnesPerYear; // ktCO2eq/year
    
    @Column(nullable = false)
    private Double adjustedBauEmissionBiologicalTreatment; // ktCO2eq/year
}
