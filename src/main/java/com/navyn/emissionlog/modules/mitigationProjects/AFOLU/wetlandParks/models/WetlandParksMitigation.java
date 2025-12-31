package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.models;

import com.navyn.emissionlog.Enums.Mitigation.WetlandTreeCategory;
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
@Table(name = "wetland_parks_mitigations",
       uniqueConstraints = @UniqueConstraint(columnNames = {"year", "tree_category"}))
@Data
public class WetlandParksMitigation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    // COMPOSITE UNIQUE: year + treeCategory
    @NotNull
    @Min(1900)
    @Max(2100)
    @Column(nullable = false)
    private Integer year;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tree_category", nullable = false)
    private WetlandTreeCategory treeCategory;
    
    // ===== INPUT FIELDS =====
    @Column(nullable = false)
    private Double cumulativeArea; // ha
    
    @Column(nullable = false)
    private Double areaPlanted; // ha
    
    @Column(nullable = false)
    private Double abovegroundBiomassAGB; // m3/ha (current year)
    
    // ===== CALCULATED FIELDS =====
    private Double previousYearAGB; // m3/ha (fetched from previous year)
    private Double agbGrowth; // m3/ha
    private Double abovegroundBiomassGrowth; // tonnes DM/ha
    private Double totalBiomass; // tonnes DM/year
    private Double biomassCarbonIncrease; // tonnes C/year
    private Double mitigatedEmissionsKtCO2e; // Kt CO2e
    private Double adjustmentMitigation; // Kilotons CO2 (BAU.value - gagEmissionsSavings)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intervention_id", nullable = true)
    private Intervention intervention;
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
