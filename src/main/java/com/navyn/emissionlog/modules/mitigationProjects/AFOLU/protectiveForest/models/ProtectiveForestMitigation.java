package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.protectiveForest.models;

import com.navyn.emissionlog.Enums.Mitigation.ProtectiveForestCategory;
import com.navyn.emissionlog.modules.mitigationProjects.intervention.Intervention;
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
@Table(name = "protective_forest_mitigations",
       uniqueConstraints = @UniqueConstraint(columnNames = {"year", "category"}))
@Data
public class ProtectiveForestMitigation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    // COMPOSITE UNIQUE: year + category
    @NotNull
    @Min(1900)
    @Max(2100)
    @Column(nullable = false)
    private Integer year;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProtectiveForestCategory category;
    
    // ===== INPUT FIELDS =====
    @Column(nullable = false)
    private Double areaPlanted; // ha
    
    @Column(nullable = false)
    private Double agbCurrentYear; // m3/ha (USER provides)
    
    // ===== CALCULATED FIELDS =====
    @Column(nullable = false)
    private Double cumulativeArea; // ha
    private Double agbGrowth; // tonnes m3/ha
    private Double aboveGroundBiomassGrowth; // tonnes DM/ha
    private Double totalBiomass; // tonnes DM/year
    private Double biomassCarbonIncrease; // tonnes C/year
    private Double mitigatedEmissionsKtCO2e; // Kt CO2e
    private Double adjustmentMitigation; // Kilotonnes CO2 (BAU.value - ghgEmissionsSavings)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intervention_id", nullable = true)
    private Intervention intervention;
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
