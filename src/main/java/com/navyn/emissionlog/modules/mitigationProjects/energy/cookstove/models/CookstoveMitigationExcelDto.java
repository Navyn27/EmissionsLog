package com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CookstoveMitigationExcelDto {
    /**
     * Calendar year for this installation data.
     */
    private int year;

    /**
     * Name of the stove type (will be used to find or create StoveType).
     */
    private String stoveTypeName;

    /**
     * Baseline percentage (required for new stove types, ignored if stove type
     * exists).
     */
    private Double baselinePercentage;

    /**
     * Number of units installed in this specific year.
     */
    private int unitsInstalledThisYear;

    /**
     * Business as usual emissions value for this year (user input).
     */
    private double bau;
}
