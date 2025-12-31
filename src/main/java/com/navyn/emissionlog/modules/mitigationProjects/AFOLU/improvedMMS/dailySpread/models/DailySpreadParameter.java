package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.dailySpread.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Table(name = "daily_spread_parameters")
@Entity
public class DailySpreadParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Double emissionPerCow; //CH4 emissions per cow, tonnes CO2e/cow/year

    private Double reduction; //Reduction of CH4 emissions, %

    private Boolean isActive = true;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
