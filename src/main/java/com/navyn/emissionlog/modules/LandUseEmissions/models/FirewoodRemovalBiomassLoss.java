package com.navyn.emissionlog.modules.LandUseEmissions.models;

import com.navyn.emissionlog.Enums.LandUse.LandCategory;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
public class FirewoodRemovalBiomassLoss {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true)
    private Integer year = LocalDate.now().getYear();

    @Enumerated(EnumType.STRING)
    private LandCategory landCategory;

    private double removedFirewoodAmount = 0.0;

    private double totalBiomass = 0.0;

    private double lossOfBiomassCarbon = 0.0;

    private double CO2EqOfBiomassCarbonLoss = 0.0;

}
