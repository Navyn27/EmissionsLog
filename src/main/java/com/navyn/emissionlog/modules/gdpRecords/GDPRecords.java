package com.navyn.emissionlog.modules.gdpRecords;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.UUID;

@Entity
public class GDPRecords {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String year;

    private Double rdaGdpMillions;

    private Double kglGdpMillions;

    private Double perCapitaGdp;

    private Double growthRate;
}
