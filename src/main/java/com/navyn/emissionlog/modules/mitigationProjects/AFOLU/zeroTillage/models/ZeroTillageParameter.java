package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.zeroTillage.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "zero_tillage_paramaters")
@Data
public class ZeroTillageParameter {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private Double carbonIncreaseInSoil;// Carbon increase in soil, tonnes C/ha per year
    private Double carbonToC02;// C to CO2 conversion
    private Double emissionFactorFromUrea;// Emission factor from urea
    private Boolean isActive = true;
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
