package com.navyn.emissionlog.modules.mitigationProjects.Energy.rooftop.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class RoofTopParameterResponseDto {
    private UUID id;
    private double solarPVCapacity;
    private double energyOutPut;
    private double percentageOutPutDisplacedDiesel;
    private double avoidedDieselConsumption;
    private double dieselEnergyContent;
    private double gensetEfficiency;
    private double constant;
    private double avoidedDieselConsumptionCalculated;
    private double avoidedDieselConsumptionAverage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

