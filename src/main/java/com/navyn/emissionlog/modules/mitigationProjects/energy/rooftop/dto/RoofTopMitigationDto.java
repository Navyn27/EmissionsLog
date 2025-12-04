package com.navyn.emissionlog.modules.mitigationProjects.Energy.rooftop.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class RoofTopMitigationDto {
    @NotNull(message = "Year is required")
    @Positive(message = "Year must be positive")
    private int year;

    @NotNull(message = "Installed Unit Per Year is required")
    @Positive(message = "Installed Unit Per Year must be positive")
    private int installedUnitPerYear;

    @NotNull(message = "BAU Emission Without Project is required")
    @Positive(message = "BAU Emission Without Project must be positive")
    private double bauEmissionWithoutProject;
}
