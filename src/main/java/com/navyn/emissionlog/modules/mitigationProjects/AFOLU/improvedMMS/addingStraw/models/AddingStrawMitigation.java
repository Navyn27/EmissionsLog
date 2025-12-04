package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "adding_straw_mitigation", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"year"})
})
@Data
public class AddingStrawMitigation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    private Integer year;
    
    // User input
    @Column(nullable = false)
    private Integer numberOfCows;
    
    // Calculated fields
    @Column(nullable = false)
    private Double ch4EmissionsAddingStraw; // tonnes CO2e
    
    @Column(nullable = false)
    private Double ch4ReductionAddingStraw; // tonnes CO2e (30%)
    
    @Column(nullable = false)
    private Double mitigatedCh4EmissionsKilotonnes; // ktCO2e/year
}
