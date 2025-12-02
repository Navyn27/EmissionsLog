package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.dailySpread.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "daily_spread_mitigation", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"year"})
})
@Data
public class DailySpreadMitigation {
    
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
    private Double ch4EmissionsDailySpread; // tonnes CO2e/year
    
    @Column(nullable = false)
    private Double ch4ReductionDailySpread; // tonnes CO2e (50%)
    
    @Column(nullable = false)
    private Double mitigatedCh4EmissionsKilotonnes; // ktCO2e/year
}
