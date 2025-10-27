package com.navyn.emissionlog.modules.mitigationProjects.settlementTrees.models;

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
@Table(name = "settlement_trees_mitigation",
       uniqueConstraints = @UniqueConstraint(columnNames = {"year"}))
@Data
public class SettlementTreesMitigation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    // UNIQUE: year only (no category needed)
    @NotNull
    @Min(1900)
    @Max(2100)
    @Column(nullable = false, unique = true)
    private Integer year;
    
    // ===== INPUT FIELDS =====
    @Column(nullable = false)
    private Double cumulativeNumberOfTrees; // Total trees planted to date
    
    @Column(nullable = false)
    private Double numberOfTreesPlanted; // Trees planted this year
    
    @Column(nullable = false)
    private Double agbSingleTreePreviousYear; // m3 (USER provides)
    
    @Column(nullable = false)
    private Double agbSingleTreeCurrentYear; // m3 (USER provides)
    
    // ===== CALCULATED FIELDS =====
    private Double agbGrowth; // tonnes m3
    private Double abovegroundBiomassGrowth; // tonnes DM
    private Double totalBiomass; // tonnes DM/year (includes BGB)
    private Double biomassCarbonIncrease; // tonnes C/year
    private Double mitigatedEmissionsKtCO2e; // Kt CO2e
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
