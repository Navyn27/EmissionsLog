package com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoveMitigationExcelDto {
    /**
     * Calendar year for this data entry.
     */
    private int year;

    /**
     * Stove type (FIRE_WOOD, CHARCOAL, LGP, ELECTRIC).
     */
    private String stoveType;

    /**
     * Total number of stoves installed up to and including this year.
     */
    private int unitsInstalled;

    /**
     * Efficiency of the stove (percentage 0-100).
     */
    private double efficiency;

    /**
     * Project Intervention Name (from dropdown).
     */
    private String projectInterventionName;
}

