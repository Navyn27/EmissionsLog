package com.navyn.emissionlog.Models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.UUID;

@Entity
public class HouseholdRecords {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String year;

    private Long householdNumber;

    private Double growthRate;
}
