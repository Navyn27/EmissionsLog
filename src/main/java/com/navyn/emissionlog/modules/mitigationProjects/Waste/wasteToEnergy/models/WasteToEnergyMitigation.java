package com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "waste_to_energy_mitigation", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"year"})
})
@Data
public class WasteToEnergyMitigation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Integer year;
    
    // User inputs
    @Column(nullable = false)
    private Double wasteToWtE; // t/year
    
    @Column(nullable = false)
    private Double bauEmissionsSolidWaste; // ktCO2e
    
    // Calculated fields
    @Column(nullable = false)
    private Double ghgReductionTonnes; // tCO2eq
    
    @Column(nullable = false)
    private Double ghgReductionKilotonnes; // ktCO2eq
    
    @Column(nullable = false)
    private Double adjustedEmissionsWithWtE; // ktCO2e
}
