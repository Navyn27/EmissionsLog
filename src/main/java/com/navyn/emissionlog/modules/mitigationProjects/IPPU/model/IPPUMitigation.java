package com.navyn.emissionlog.modules.mitigationProjects.IPPU.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "ippu_mitigations")
public class IPPUMitigation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "year", nullable = false)
    private int year;

    @Column(name = "bau", nullable = false)
    private double bau;

    @Column(name = "f_gas_name", length = 100)
    private String fGasName;

    @Column(name = "amount_of_avoided_f_gas", nullable = false)
    private double amountOfAvoidedFGas;
    // amount in kg

    @Column(name = "gwp_factor", nullable = false)
    private double gwpFactor;

    @Column(name = "reduced_emission_kg_co2e")
    private double reducedEmissionInKgCO2e;// to be calculated

    @Column(name = "reduced_emission_kt_co2e")
    private double reducedEmissionInKtCO2e; // to be calculated

    @Column(name = "mitigation_scenario")
    private double mitigationScenario;  // to be calculated
}
