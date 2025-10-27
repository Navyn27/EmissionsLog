package com.navyn.emissionlog.modules.mitigationProjects.cropRotation.models;

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
@Table(name = "crop_rotation_mitigation",
       uniqueConstraints = @UniqueConstraint(columnNames = {"year"}))
@Data
public class CropRotationMitigation {
    
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
    private Double croplandUnderCropRotation; // ha
    
    @Column(nullable = false)
    private Double abovegroundBiomass; // tonnes DM/ha
    
    @Column(nullable = false)
    private Double increasedBiomass; // tonnes DM/ha
    
    // ===== CALCULATED FIELDS =====
    private Double totalIncreasedBiomass; // tonnes DM/year
    private Double biomassCarbonIncrease; // tonnes C/year
    private Double mitigatedEmissionsKtCO2e; // Kt CO2e
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
