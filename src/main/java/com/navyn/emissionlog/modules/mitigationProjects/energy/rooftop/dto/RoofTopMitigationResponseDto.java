package com.navyn.emissionlog.modules.mitigationProjects.energy.rooftop.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class RoofTopMitigationResponseDto {
    private UUID id;
    private int year;
    private int installedUnitPerYear;
    private int cumulativeInstalledUnitPerYear;
    private int percentageOfFinalMaximumRate;
    private double dieselDisplacedInMillionLitterPerArea;
    private double dieselDisplacedInTonJoule;
    private double bauEmissionWithoutProject;
    private double netGhGMitigationAchieved;
    private double scenarioGhGEmissionWithProject;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

