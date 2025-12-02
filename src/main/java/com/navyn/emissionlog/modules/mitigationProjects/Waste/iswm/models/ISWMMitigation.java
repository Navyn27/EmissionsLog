package com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "iswm_mitigation", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"year"})
})
@Data
public class ISWMMitigation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    private Integer year;
    
    // User inputs
    @Column(nullable = false)
    private Double bauEmissions; // ktCO2e
    
    @Column(nullable = false)
    private Double annualReduction; // ktCO2e
    
    // Calculated fields
    @Column(nullable = false)
    private Double adjustedEmissions; // ktCO2e
}
