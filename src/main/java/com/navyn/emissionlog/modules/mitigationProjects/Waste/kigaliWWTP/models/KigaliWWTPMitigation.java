package com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.models;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.constants.WWTPProjectPhase;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "kigali_wwtp_mitigation", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"year"})
})
@Data
public class KigaliWWTPMitigation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Integer year;
    
    // User inputs
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WWTPProjectPhase projectPhase;
    
    // Calculated fields
    @Column(nullable = false)
    private Double connectedHouseholdsPercentage; // %
    
    @Column(nullable = false)
    private Double effectiveDailyFlow; // m³/day
    
    @Column(nullable = false)
    private Double annualSludgeTreated; // m³/year
    
    @Column(nullable = false)
    private Double annualEmissionsReductionTonnes; // tCO2e
    
    @Column(nullable = false)
    private Double annualEmissionsReductionKilotonnes; // ktCO2e
}
