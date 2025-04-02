package com.navyn.emissionlog.Models;

import com.navyn.emissionlog.Enums.*;
import com.navyn.emissionlog.Models.ActivityData.ActivityData;
import jakarta.persistence.*;
import lombok.Data;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Entity
@Data
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private Sectors sector;

    @Enumerated(EnumType.STRING)
    private Scopes scope;

    @ManyToOne
    private Region region;

    @OneToOne
    private ActivityData activityData;

    private Double CH4Emissions = 0.0;

    private Double FossilCO2Emissions = 0.0;

    private Double BiomassCO2Emissions = 0.0;

    private Double N2OEmissions = 0.0;

    private LocalDateTime activityYear = LocalDateTime.now();
}