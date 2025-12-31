package com.navyn.emissionlog.modules.mitigationProjects.modalShift.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "modal_shift_parameters")
@Data
public class ModalShiftParameter {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    // Energy content of fuels
    @Column(nullable = false, name = "energy_content_diesel")
    private Double energyContentDiesel; // MJ/litre
    
    @Column(nullable = false, name = "energy_content_gasoline")
    private Double energyContentGasoline; // MJ/litre

    // Emission factor (CO2)
    @Column(nullable = false, name = "emission_factor_carbon_diesel")
    private Double emissionFactorCarbonDiesel; // kgCO2/TJ
    
    @Column(nullable = false, name = "emission_factor_carbon_gasoline")
    private Double emissionFactorCarbonGasoline; // kgCO2/TJ
    
    // Emission factor (CH4)
    @Column(nullable = false, name = "emission_factor_methane_diesel")
    private Double emissionFactorMethaneDiesel; // kgCH4/TJ
    
    @Column(nullable = false, name = "emission_factor_methane_gasoline")
    private Double emissionFactorMethaneGasoline; // kgCH4/TJ
    
    // Emission factor (NO2)
    @Column(nullable = false, name = "emission_factor_nitrogen_diesel")
    private Double emissionFactorNitrogenDiesel; // kgNO2/TJ
    
    @Column(nullable = false, name = "emission_factor_nitrogen_gasoline")
    private Double emissionFactorNitrogenGasoline; // kgNO2/TJ
    
    // Global warming potentials
    @Column(nullable = false, name = "potential_methane")
    private Double potentialMethane; // GWPCH4
    
    @Column(nullable = false, name = "potential_nitrogen")
    private Double potentialNitrogen; // GWPNO2
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column
    private LocalDateTime updatedAt;
}
