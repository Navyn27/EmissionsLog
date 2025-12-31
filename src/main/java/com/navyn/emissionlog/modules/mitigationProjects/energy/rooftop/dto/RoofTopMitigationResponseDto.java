package com.navyn.emissionlog.modules.mitigationProjects.energy.rooftop.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class RoofTopMitigationResponseDto {
    private UUID id;
    private int year;
    private int installedUnitPerYear;
    private double solarPVCapacity;
    private int cumulativeInstalledUnitPerYear;
    private int percentageOfFinalMaximumRate;
    private double dieselDisplacedInMillionLitterPerArea;
    private double dieselDisplacedInTonJoule;
    private double bauEmissionWithoutProject;
    private double netGhGMitigationAchieved;
    private double scenarioGhGEmissionWithProject;
    private double adjustedBauEmissionMitigation;
    private InterventionInfo projectIntervention;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    public static class InterventionInfo {
        private UUID id;
        private String name;

        public InterventionInfo(UUID id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}

