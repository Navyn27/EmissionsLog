package com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.models;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.constants.WWTPProjectPhase;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "kigali_wwtp_mitigation", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"year"})
})
@Data
public class KigaliWWTPMitigation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    private Integer year;
    
    // User inputs
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WWTPProjectPhase projectPhase;
    
    @Column(nullable = false)
    private Double phaseCapacityPerDay; // m³/day (stored in standard unit)
    
    @Column(nullable = false)
    private Double connectedHouseholds; // Total connected households
    
    @Column(nullable = false)
    private Double connectedHouseholdsPercentage; // Percentage as decimal (0.0-1.0)
    
    // Calculated fields
    
    @Column(nullable = false)
    private Double effectiveDailyFlow; // m³/day
    
    @Column(nullable = false)
    private Double annualSludgeTreated; // m³/year
    
    @Column(nullable = false)
    private Double annualEmissionsReductionTonnes; // tCO2e
    
    @Column(nullable = false)
    private Double annualEmissionsReductionKilotonnes; // ktCO2e
}
