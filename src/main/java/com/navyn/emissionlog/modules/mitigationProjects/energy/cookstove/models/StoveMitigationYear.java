package com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table
@Getter
@Setter
public class StoveMitigationYear {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "stove_type_id")
    private StoveType stoveType;

    private int year;

    private int unitsInstalled;           // Number of stoves installed this year
    private double avoidedEmissions;      // Calculated in service layer
}
