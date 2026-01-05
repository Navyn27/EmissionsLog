package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@Table(name = "crop_rotation_mitigations")
@Data
public class CropRotationMitigation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // UNIQUE: year only
    @NotNull
    @Min(1900)
    @Max(2100)
    @Column(nullable = false)
    private Integer year;

    // ===== INPUT FIELDS =====
    @Column(nullable = false)
    private Double croplandUnderCropRotation; // ha


    // ===== CALCULATED FIELDS =====
    private Double totalIncreasedBiomass; // tonnes DM/year
    private Double biomassCarbonIncrease; // tonnes C/year
    @Column(nullable = false)
    private Double increasedBiomass; // tonnes DM/ha
    private Double mitigatedEmissionsKtCO2e; // Kt CO2e
    private Double adjustmentMitigation; // Kilotonnes CO2 (BAU.value - ghgEmissionsSavings)


    // ===== INTERVENTION RELATIONSHIP =====
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intervention_id", nullable = true)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Intervention intervention;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
