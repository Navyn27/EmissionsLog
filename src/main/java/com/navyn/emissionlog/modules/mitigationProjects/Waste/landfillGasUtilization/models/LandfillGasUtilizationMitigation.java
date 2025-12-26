package com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.navyn.emissionlog.modules.mitigationProjects.intervention.Intervention;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "landfill_gas_utilization_mitigation", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"year"})
})
@Data
public class LandfillGasUtilizationMitigation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private Integer year;

    // User inputs
    @Column(nullable = false, name = "ch4_captured")
    private Double ch4Captured; // CH₄ captured (user input)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_intervention_id", nullable = true)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Intervention projectIntervention; // Foreign key to Intervention table

    // Calculated fields
    @Column(nullable = false, name = "ch4_destroyed")
    private Double ch4Destroyed; // CH₄Captured * DestructionEfficiency(%)

    @Column(nullable = false, name = "equivalent_co2e_reduction")
    private Double equivalentCO2eReduction; // CH₄Destroyed * GlobalWarmingPotential(CH₄)

    @Column(nullable = false, name = "mitigation_scenario_grand")
    private Double mitigationScenarioGrand; // BAU - EquivalentCO₂eReduction

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Getter for project reduction emissions (alias for equivalentCO2eReduction in kilotonnes)
    public Double getProjectReductionEmissions() {
        return equivalentCO2eReduction;
    }
}
