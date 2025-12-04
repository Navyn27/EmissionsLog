package com.navyn.emissionlog.modules.mitigationProjects.Energy.cookstove.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "stove_mitigation_year")
@Getter
@Setter
public class StoveMitigationYear {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "stove_type_id", nullable = false)
    private StoveType stoveType;

    /**
     * Calendar year for this record.
     */
    private int year;

    /**
     * Total number of stoves installed up to and including this year.
     */
    private int unitsInstalled;

    /**
     * Business as usual emissions for this year (user input).
     */
    private double bau;

    /**
     * Number of additional stoves installed this year compared to previous year.
     */
    private int differenceInstalled;

    /**
     * Constant derived from baseline percentage: (baselinePercentage * 0.15) / 0.25.
     */
    private double constantValue;

    /**
     * Avoided emissions for this stove type in this year (tCO2e).
     */
    private double avoidedEmissions;

    /**
     * Sum of avoided emissions for all stove types in this year (tCO2e).
     */
    private double totalAvoidedEmissions;

    /**
     * Adjustment = BAU - totalAvoidedEmissions.
     */
    private double adjustment;
}
