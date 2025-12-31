package com.navyn.emissionlog.modules.transportScenarios.modalShift.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.navyn.emissionlog.modules.mitigationProjects.intervention.Intervention;
import com.navyn.emissionlog.modules.transportScenarios.enums.FuelType;
import com.navyn.emissionlog.modules.transportScenarios.enums.VehicleCategory;
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
@Table(name = "modal_shift_mitigations")
@Data
public class ModalShiftMitigation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // ===== INPUT FIELDS =====
    @NotNull
    @Min(1900)
    @Max(2100)
    @Column(nullable = false)
    private Integer year;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "category_before_shift")
    private VehicleCategory categoryBeforeShift; // Vehicle category before shift

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "category_after_shift")
    private VehicleCategory categoryAfterShift; // Vehicle category after shift

    @Column(nullable = false, name = "vtk")
    private Double vtk; // Vehicle Kilometers Traveled in km


    @Column(name = "bau_of_shift")
    private Double bauOfShift; // GgCO2e


    @Column(nullable = false, name = "fuel_economy")
    private Double fuelEconomy; // Fuel economy (for all vehicle categories) L/100km

    @Column(nullable = false, name = "fleet_population")
    private Double fleetPopulation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "fuel_type")
    private FuelType fuelType;

    // ===== CALCULATED FIELDS =====
    @Column(name = "total_fuel")
    private Double totalFuel;

    @Column(name = "project_emission_carbon")
    private Double projectEmissionCarbon; // Project emission in CO2

    @Column(name = "project_emission_nitrogen")
    private Double projectEmissionNitrogen; // Project emission in N2O

    @Column(name = "project_emission_methane")
    private Double projectEmissionMethane; // Project emission in CH4

    @Column(name = "total_project_emission")
    private Double totalProjectEmission; // Total project emission, where CH4 and N2O converted into CO2

    @Column(name = "emission_reduction")
    private Double emissionReduction; // BAU of shift - total project emission

    // ===== INTERVENTION RELATIONSHIP =====
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intervention_id", nullable = true)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Intervention intervention;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column
    private LocalDateTime updatedAt;
}
