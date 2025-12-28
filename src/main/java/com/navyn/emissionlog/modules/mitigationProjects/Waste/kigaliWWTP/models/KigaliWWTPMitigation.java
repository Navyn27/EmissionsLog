package com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.navyn.emissionlog.modules.mitigationProjects.intervention.Intervention;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "kigali_wwtp_mitigation", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"year"})
})
@Data
public class KigaliWWTPMitigation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    private Integer year;
    
    // User inputs
    // Note: nullable = true to allow database migration with existing records
    // Validation is enforced in the service layer for new records
    @Column(nullable = true, name = "annual_wastewater_treated")
    private Double annualWastewaterTreated; // m³/year
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_intervention_id", nullable = true)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Intervention projectIntervention; // Foreign key to Intervention table
    
    // Calculated fields
    // Note: nullable = true to allow database migration with existing records
    // These are always calculated in the service layer for new records
    @Column(nullable = true, name = "methane_potential")
    private Double methanePotential; // Calculated: MethaneEmissionFactor * COD_concentration
    
    @Column(nullable = true, name = "co2e_per_m3_wastewater")
    private Double co2ePerM3Wastewater; // Calculated: MethanePotential * CH4GWP(100-yr)
    
    @Column(nullable = true, name = "annual_emissions_reduction_tonnes")
    private Double annualEmissionsReductionTonnes; // Calculated: AnnualWastewaterTreated * CO2ePerM3Wastewater
    
    @Column(nullable = true, name = "annual_emissions_reduction_kilotonnes")
    private Double annualEmissionsReductionKilotonnes; // Calculated: annualEmissionsReductionTonnes / 1000
    
    @Column(nullable = true, name = "adjusted_bau_emission_mitigation")
    private Double adjustedBauEmissionMitigation; // Calculated: BAU - annualEmissionsReductionKilotonnes
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
