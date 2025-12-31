package com.navyn.emissionlog.modules.mitigationProjects.energy.rooftop.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.UUID;

@Data
public class RoofTopMitigationDto {
    @NotNull(message = "Year is required")
    @Positive(message = "Year must be positive")
    private int year;

    @NotNull(message = "Installed Unit Per Year is required")
    @Positive(message = "Installed Unit Per Year must be positive")
    private int installedUnitPerYear;

    @NotNull(message = "Solar PV Capacity is required")
    @Positive(message = "Solar PV Capacity must be positive")
    private double solarPVCapacity;

    @NotNull(message = "Project Intervention is required")
    private UUID projectInterventionId; // Foreign key to Intervention table
}
