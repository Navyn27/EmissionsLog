package com.navyn.emissionlog.modules.mitigationProjects.zeroTillage.models;

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
@Table(name = "zero_tillage_mitigation",
       uniqueConstraints = @UniqueConstraint(columnNames = {"year"}))
@Data
public class ZeroTillageMitigation {
    
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
    private Double areaUnderZeroTillage; // ha
    
    // ===== CALCULATED FIELDS =====
    private Double totalCarbonIncreaseInSoil; // Tonnes C
    private Double emissionsSavings; // Kilotonnes CO2e
    private Double ureaApplied; // tonnes
    private Double emissionsFromUrea; // Tonnes CO2
    private Double ghgEmissionsSavings; // Kilotonnes CO2e (NET)
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
