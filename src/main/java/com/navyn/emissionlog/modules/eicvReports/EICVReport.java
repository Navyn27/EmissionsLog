package com.navyn.emissionlog.modules.eicvReports;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "eicv_reports")
public class EICVReport {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String name;

    @Column(unique = true)
    private int year;
    private Double totalImprovedSanitation;
    private Double improvedTypeNotSharedWithOtherHH;
    private Double flushToilet;
    private Double protectedLatrines;
    private Double unprotectedLatrines;
    private Double others;
    private Double noToiletFacilities;
    private Double totalHouseholds;
}
