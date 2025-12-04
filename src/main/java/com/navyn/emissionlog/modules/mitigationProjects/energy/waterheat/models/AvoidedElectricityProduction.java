package com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.models;

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

    private int year;
    private int unitsInstalledThisYear;
    private int cumulativeUnitsInstalled;
    private double annualAvoidedElectricity;      // MWh
    private double cumulativeAvoidedElectricity;  // MWh

    private Double netGhGMitigation;              // tCO2, NEW

    public AvoidedElectricityProduction(int year,
                                        int unitsInstalledThisYear,
                                        int cumulativeUnitsInstalled,
                                        WaterHeatParameter param) {

        this.year = year;
        this.unitsInstalledThisYear = unitsInstalledThisYear;
        this.cumulativeUnitsInstalled = cumulativeUnitsInstalled;
        this.annualAvoidedElectricity =
                unitsInstalledThisYear * param.getAvoidedElectricityPerHousehold();

        this.cumulativeAvoidedElectricity =
                cumulativeUnitsInstalled * param.getAvoidedElectricityPerHousehold();
    }
}
