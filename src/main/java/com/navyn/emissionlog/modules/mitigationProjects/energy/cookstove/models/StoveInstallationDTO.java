package com.navyn.emissionlog.modules.mitigationProjects.Energy.cookstove.models;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class StoveInstallationDTO {

    /**
     * Calendar year for this installation data.
     */
    private int year;

    /**
     * Identifier of the stove type being installed.
     */
    private UUID stoveTypeId;

    /**
     * Number of units installed in this specific year.
     */
    private int unitsInstalledThisYear;

    /**
     * Business as usual emissions value for this year (user input).
     */
    private double bau;
}
