package com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "avoided_electricity_production")
public class AvoidedElectricityProduction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    private int year;
    private int unitsInstalledThisYear;
    private int cumulativeUnitsInstalled;
    private double annualAvoidedElectricity;      // MWh
    private double cumulativeAvoidedElectricity;  // MWh

    private Double netGhGMitigation;              // tCO2, NEW

}
