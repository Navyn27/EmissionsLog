package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.greenFences.models;

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
@Table(name = "green_fences_mitigation",
        uniqueConstraints = @UniqueConstraint(columnNames = {"year"}))
@Data
public class GreenFencesMitigation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // UNIQUE: year only
    @NotNull
    @Min(1900)
    @Max(2100)
    @Column(nullable = false, unique = true)
    private Integer year;

    // ===== INPUT FIELDS ====

    @Column(nullable = false)
    private Double numberOfHouseholdsWith10m2Fence; // Households with a fence

    @Column(nullable = false)
    private Double agbOf10m2LiveFence; // tonnes DM (USER provides)

    // ===== CALCULATED FIELDS =====
    @Column(nullable = false)
    private Double cumulativeNumberOfHouseholds; // Total households
    private Double agbFenceBiomassCumulativeHouseholds; // Tonnes C (AGB only)
    private Double agbPlusBgbCumulativeHouseholds; // Tonnes C (AGB + BGB)
    private Double mitigatedEmissionsKtCO2e; // Kt CO2e
    private Double adjustmentMitigation; // Kilotonnes CO2 (BAU.value - ghgEmissionsSavings)
    // ===== INTERVENTION RELATIONSHIP =====
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intervention_id", nullable = true)
    private Intervention intervention;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
