package com.navyn.emissionlog.modules.mitigationProjects.Energy.rooftop.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class RoofTopParameterDto {
    @NotNull(message = "Solar PV Capacity is required")
    @Positive(message = "Solar PV Capacity must be positive")
    private double solarPVCapacity;

    @NotNull(message = "Energy Output is required")
    @Positive(message = "Energy Output must be positive")
    private double energyOutPut;

    @NotNull(message = "Percentage Output Displaced Diesel is required")
    @Positive(message = "Percentage Output Displaced Diesel must be positive")
    private double percentageOutPutDisplacedDiesel;

    @NotNull(message = "Avoided Diesel Consumption is required")
    @Positive(message = "Avoided Diesel Consumption must be positive")
    private double avoidedDieselConsumption;

    @NotNull(message = "Diesel Energy Content is required")
    @Positive(message = "Diesel Energy Content must be positive")
    private double dieselEnergyContent;

    @NotNull(message = "Genset Efficiency is required")
    @Positive(message = "Genset Efficiency must be positive")
    private double gensetEfficiency;

    @NotNull(message = "Constant (MJ/MWh) is required")
    @Positive(message = "Constant must be positive")
    private double constant; // MJ/MWh
}

