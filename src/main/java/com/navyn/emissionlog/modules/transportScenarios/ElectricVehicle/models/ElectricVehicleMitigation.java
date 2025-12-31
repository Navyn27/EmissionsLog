package com.navyn.emissionlog.modules.transportScenarios.ElectricVehicle.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.navyn.emissionlog.modules.mitigationProjects.intervention.Intervention;
import com.navyn.emissionlog.modules.transportScenarios.enums.VehicleCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "electric_vehicle_mitigations")
public class ElectricVehicleMitigation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    // ===== INPUT FIELDS =====
    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be 1900 or later")
    @Max(value = 2100, message = "Year must be 2100 or earlier")
    @Column(nullable = false, name = "year")
    private Integer year;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "vehicle_category")
    private VehicleCategory vehicleCategory;

    @Column(name = "bau")
    private Double bau; // GgCO₂e (optional)

    @NotNull(message = "VTK is required")
    @Positive(message = "VTK must be positive")
    @Column(nullable = false, name = "vtk")
    private Double vkt; // Vehicle Kilometers Traveled in km

    @NotNull(message = "Fleet Population is required")
    @Positive(message = "Fleet Population must be positive")
    @Column(nullable = false, name = "fleet_population")
    private Double fleetPopulation;

    @NotNull(message = "EV Power Demand is required")
    @Positive(message = "EV Power Demand must be positive")
    @Column(nullable = false, name = "ev_power_demand")
    private Double evPowerDemand; // km/kWh

    // ===== CALCULATED FIELDS =====
    @Column(name = "annual_electricity_consumption")
    private Double annualElectricityConsumption; // MWh

    @Column(name = "total_project_emission")
    private Double totalProjectEmission; // GgCO₂e

    @Column(name = "emission_reduction")
    private Double emissionReduction; // GgCO₂e (BAU - total project emission)

    // ===== INTERVENTION RELATIONSHIP =====
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intervention_id", nullable = true)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Intervention intervention;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
