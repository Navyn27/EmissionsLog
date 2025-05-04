package com.navyn.emissionlog.Models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Data;
import jakarta.persistence.Id;

import java.util.UUID;

@Data
@Entity
public class EICVReport {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

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
