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
    
    // User inputs (stored in ktCO₂eq)
    @Column(nullable = false, name = "bau_solid_waste_emissions")
    private Double bauSolidWasteEmissions; // BAU Solid Waste Emissions (ktCO₂eq)
    
    @Column(nullable = false, name = "project_reduction_40_percent_efficiency")
    private Double projectReduction40PercentEfficiency; // Project Reduction (40% Efficiency) (ktCO₂eq)
    
    @Column(nullable = false, name = "bau_grand_total")
    private Double bauGrandTotal; // BAU Grand Total (ktCO₂eq)
    
    // Calculated fields
    @Column(nullable = false, name = "project_reduction_emissions")
    private Double projectReductionEmissions; // Project Reduction Emissions (KtCO₂eq)
    // if year > 2028: BAU Solid Waste Emissions * Project Reduction (40% Efficiency)
    // else: 0
    
    @Column(nullable = false, name = "adjusted_solid_waste_emissions")
    private Double adjustedSolidWasteEmissions; // Adjusted Solid Waste Emissions (KtCO₂eq)
    // BAU Solid Waste Emissions - Project Reduction Emissions
    
    @Column(nullable = false, name = "adjusted_grand_total")
    private Double adjustedGrandTotal; // Adjusted Grand Total (KtCO₂eq)
    // BAU Grand Total - Project Reduction Emissions
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
