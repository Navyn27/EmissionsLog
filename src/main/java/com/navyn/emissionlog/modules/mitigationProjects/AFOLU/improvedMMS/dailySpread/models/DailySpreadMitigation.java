package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.dailySpread.models;

import com.navyn.emissionlog.modules.mitigationProjects.intervention.Intervention;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "daily_spread_mitigations")
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
    private Double ch4ReductionDailySpread; // tonnes CO2e

    @Column(nullable = false)
    private Double mitigatedCh4EmissionsKilotonnes; // ktCO2e/year
    private Double adjustmentMitigation; // Kilotonnes CO2 (BAU.value - mitigatedEmissions)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intervention_id", nullable = true)
    private Intervention intervention;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
