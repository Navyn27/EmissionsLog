package com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
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
    @Column(nullable = false, name = "bau_solid_waste_emissions")
    private Double bauSolidWasteEmissions; // ktCO2eq
    
    @Column(nullable = false, name = "project_reduction_40_percent_efficiency")
    private Double projectReduction40PercentEfficiency; // ktCO2eq (efficiency value like 0.40)
    
    @Column(nullable = false, name = "bau_grand_total")
    private Double bauGrandTotal; // ktCO2eq
    
    // Calculated fields
    @Column(nullable = false, name = "project_reduction_emissions")
    private Double projectReductionEmissions; // ktCO2eq
    
    @Column(nullable = false, name = "adjusted_solid_waste_emissions")
    private Double adjustedSolidWasteEmissions; // ktCO2eq
    
    @Column(nullable = false, name = "adjusted_grand_total")
    private Double adjustedGrandTotal; // ktCO2eq
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
