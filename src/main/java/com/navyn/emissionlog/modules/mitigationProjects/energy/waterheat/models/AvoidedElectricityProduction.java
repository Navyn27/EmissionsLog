package com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.navyn.emissionlog.modules.mitigationProjects.intervention.Intervention;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "avoided_electricity_production")
public class AvoidedElectricityProduction {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(updatable = false, nullable = false)
    private UUID id;
    @Column
    private int year;
    @Column
    private int unitsInstalledThisYear;
    @Column
    private int cumulativeUnitsInstalled;
    
    // User input - moved from WaterHeatParameter
    @Column(nullable = false)
    private int averageWaterHeat;
    
    @Column
    private double annualAvoidedElectricity;      // MWh
    @Column
    private double cumulativeAvoidedElectricity;  // MWh

    private Double netGhGMitigation;              // tCO2

    // Foreign key to Intervention table
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_intervention_id", nullable = true)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Intervention projectIntervention;

    // Calculated field: BAU - netGhGMitigation (in ktCO2e)
    @Column
    private Double adjustedBauEmissionMitigation; // ktCO2e

    public AvoidedElectricityProduction(int year,
                                        int unitsInstalledThisYear,
                                        int cumulativeUnitsInstalled,
                                        int averageWaterHeat,
                                        WaterHeatParameter param) {

        this.year = year;
        this.unitsInstalledThisYear = unitsInstalledThisYear;
        this.cumulativeUnitsInstalled = cumulativeUnitsInstalled;
        this.averageWaterHeat = averageWaterHeat;
        
        double avoidedElectricityPerHousehold = param.getAvoidedElectricityPerHousehold(averageWaterHeat);
        this.annualAvoidedElectricity =
                unitsInstalledThisYear * avoidedElectricityPerHousehold;

        this.cumulativeAvoidedElectricity =
                cumulativeUnitsInstalled * avoidedElectricityPerHousehold;
    }
}
