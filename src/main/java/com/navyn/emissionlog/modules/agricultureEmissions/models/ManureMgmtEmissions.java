package com.navyn.emissionlog.modules.agricultureEmissions.models;

import com.navyn.emissionlog.Enums.LivestockSpecies;
import com.navyn.emissionlog.Enums.OrganicAmendmentTypes;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "manure_mgmt_emissions")
public class ManureMgmtEmissions {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private LivestockSpecies species;

    @Enumerated(EnumType.STRING)
    private OrganicAmendmentTypes amendmentType;

    @Column(unique = true)
    private int year;
    private double population;
    private double totalN;
    private double NAvailable;
    private double N2ONEmissions;
    private double N2OEmissions;
    private double CH4Emissions;
    private double CO2EqEmissions;
}

