package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity(name = "crop_rotation_paramaters")
public class CropRotationParameters {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private Double aboveGroundBiomass; //AboveGround biomass (ABG) of , tonnes DM/ha
    private Double ratioOfBelowGroundBiomass; //Ratio of belowground biomass BGB to AGB
    private Double carbonContent;//Carbon content in dry increased biomass
    private Double carbonToC02;// C to CO2 conversion
    private Boolean isActive = true;
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
