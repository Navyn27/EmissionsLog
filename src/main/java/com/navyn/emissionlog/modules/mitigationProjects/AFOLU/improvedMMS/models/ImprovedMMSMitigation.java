package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "improved_mms_mitigation",
       uniqueConstraints = @UniqueConstraint(columnNames = {"year"}))
@Data
public class ImprovedMMSMitigation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    // UNIQUE: year only
    @NotNull
    @Min(1900)
    @Max(2100)
    @Column(nullable = false, unique = true)
    private Integer year;
    
    // ===== INPUT FIELDS =====
    @Column(nullable = false)
    private Integer numberOfCows;
    
    // ===== CALCULATED FIELDS =====
    // Strategy 1: N2O Reduction (Compaction and Manure Covering)
    private Double n2oEmissions; // tonnes CO2e/year
    private Double n2oReduction; // tonnes CO2e (30%)
    private Double mitigatedN2oEmissions; // Kt CO2e/year
    
    // Strategy 2: CH4 Reduction (Adding Straw)
    private Double ch4EmissionsAddingStraw; // tonnes CO2e
    private Double ch4ReductionAddingStraw; // tonnes CO2e (30%)
    private Double mitigatedCh4EmissionsAddingStraw; // Kt CO2e/year
    
    // Strategy 3: CH4 Reduction (Use of Daily Spread MMS)
    private Double ch4EmissionsDailySpread; // tonnes CO2e/year
    private Double ch4ReductionDailySpread; // tonnes CO2e (50%)
    private Double mitigatedCh4EmissionsDailySpread; // Kt CO2e/year
    
    // TOTAL
    private Double totalMitigation; // Kt CO2e
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
