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
@Table(name = "kigali_wwtp_mitigation")
@Data
public class KigaliWWTPMitigation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    private Integer year;
    
    // User inputs
    @Column(nullable = false, name = "annual_wastewater_treated")
    private Double annualWastewaterTreated; // m³/year
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_intervention_id", nullable = true)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Intervention projectIntervention; // Foreign key to Intervention table
    
    // Calculated fields
    @Column(nullable = false, name = "methane_potential")
    private Double methanePotential; // kg CH4 per m³ (calculated: MethaneEmissionFactor * COD_concentration)
    
    @Column(nullable = false, name = "co2e_per_m3_wastewater")
    private Double co2ePerM3Wastewater; // kg CO2e per m³ (calculated: MethanePotential * CH4GWP(100-yr))
    
    @Column(nullable = false, name = "annual_emissions_reduction_tonnes")
    private Double annualEmissionsReductionTonnes; // tCO2e (calculated: AnnualWastewaterTreated * CO2ePerM3Wastewater)
    
    @Column(nullable = false, name = "annual_emissions_reduction_kilotonnes")
    private Double annualEmissionsReductionKilotonnes; // ktCO2e (calculated: annualEmissionsReductionTonnes / 1000)
    
    @Column(nullable = false, name = "adjusted_bau_emission_mitigation")
    private Double adjustedBauEmissionMitigation; // ktCO2e (calculated: BAU - annualEmissionsReductionKilotonnes)
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
