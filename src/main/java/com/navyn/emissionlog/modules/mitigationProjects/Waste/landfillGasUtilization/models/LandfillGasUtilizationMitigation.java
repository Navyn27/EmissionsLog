package com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "landfill_gas_utilization_mitigation", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"year"})
})
@Data
public class LandfillGasUtilizationMitigation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    private Integer year;
    
    // User inputs
    @Column(nullable = false)
    private Double bauSolidWasteEmissions; // ktCO2eq
    
    @Column(nullable = false)
    private Double projectReduction40PercentEfficiency; // ktCO2eq (efficiency value like 0.40)
    
    @Column(nullable = false)
    private Double bauGrandTotal; // ktCO2eq
    
    // Calculated fields
    @Column(nullable = false)
    private Double projectReductionEmissions; // ktCO2eq
    
    @Column(nullable = false)
    private Double adjustedSolidWasteEmissions; // ktCO2eq
    
    @Column(nullable = false)
    private Double adjustedGrandTotal; // ktCO2eq
}
