package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandsRewetting.models;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandsRewetting.swap.Swap;
import com.navyn.emissionlog.modules.mitigationProjects.intervention.Intervention;
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
@Table(name = "wetlands_rewetting_mitigations")
@Data
public class WetlandsRewettingMitigation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Min(1900)
    @Max(2100)
    @Column(nullable = false)
    private Integer year;

    @NotNull
    @Column(name = "area_rewetted_mineral_wetlands_ha", nullable = false)
    private Double areaRewettedMineralWetlandsHa; // ha

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "swap_id", nullable = false)
    private Swap swap;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intervention_id", nullable = true)
    private Intervention intervention;

    // ===== CALCULATED FIELDS =====
    @Column(name = "ch4_emissions_kilotonnes_per_year")
    private Double ch4EmissionsKilotonnesPerYear;

    @Column(name = "emissions_co2e_tonnes_per_year")
    private Double emissionsCo2eTonnesPerYear;

    @Column(name = "sequestration_tonnes_c")
    private Double sequestrationTonnesC;

    @Column(name = "co2e_value_of_sequestration_tonnes")
    private Double co2eValueOfSequestrationTonnes;

    @Column(name = "total_mitigation_tonnes_co2e")
    private Double totalMitigationTonnesCo2e;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
