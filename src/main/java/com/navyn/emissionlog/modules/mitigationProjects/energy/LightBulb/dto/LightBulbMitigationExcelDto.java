package com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LightBulbMitigationExcelDto {
    /**
     * Calendar year for this data entry.
     */
    private int year;

    /**
     * Total number of light bulbs installed in a year.
     */
    private double totalInstalledBulbsPerYear;

    /**
     * Reduction capacity of a single light bulb.
     */
    private double reductionCapacityPerBulb;

    /**
     * Project Intervention Name (from dropdown).
     */
    private String projectInterventionName;
}
