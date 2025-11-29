package com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.models;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.constants.ProjectPhase;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "kigali_fstp_mitigation", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"year"})
})
@Data
public class KigaliFSTPMitigation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Integer year;
    
    // User inputs
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectPhase projectPhase;
    
    @Column(nullable = false)
    private Double phaseCapacityPerDay; // m³/day
    
    @Column(nullable = false)
    private Double plantOperationalEfficiency; // Efficiency as decimal (0.0-1.0)
    
    // Calculated fields
    @Column(nullable = false)
    private Double effectiveDailyTreatment; // m³/day
    
    @Column(nullable = false)
    private Double annualSludgeTreated; // m³/year
    
    @Column(nullable = false)
    private Double annualEmissionsReductionTonnes; // tCO2e
    
    @Column(nullable = false)
    private Double annualEmissionsReductionKilotonnes; // ktCO2e
}
