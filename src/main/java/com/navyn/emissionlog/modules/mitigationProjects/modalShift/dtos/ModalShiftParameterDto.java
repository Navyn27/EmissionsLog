package com.navyn.emissionlog.modules.mitigationProjects.modalShift.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ModalShiftParameterDto {

    @NotNull(message = "Energy Content Diesel is required")
    @Positive(message = "Energy Content Diesel must be positive")
    private Double energyContentDiesel; // MJ/litre

    @NotNull(message = "Energy Content Gasoline is required")
    @Positive(message = "Energy Content Gasoline must be positive")
    private Double energyContentGasoline; // MJ/litre

    @NotNull(message = "Emission Factor Carbon Diesel is required")
    @Positive(message = "Emission Factor Carbon Diesel must be positive")
    private Double emissionFactorCarbonDiesel; // kgCO2/TJ

    @NotNull(message = "Emission Factor Carbon Gasoline is required")
    @Positive(message = "Emission Factor Carbon Gasoline must be positive")
    private Double emissionFactorCarbonGasoline; // kgCO2/TJ

    @NotNull(message = "Emission Factor Methane Diesel is required")
    @Positive(message = "Emission Factor Methane Diesel must be positive")
    private Double emissionFactorMethaneDiesel; // kgCH4/TJ

    @NotNull(message = "Emission Factor Methane Gasoline is required")
    @Positive(message = "Emission Factor Methane Gasoline must be positive")
    private Double emissionFactorMethaneGasoline; // kgCH4/TJ

    @NotNull(message = "Emission Factor Nitrogen Diesel is required")
    @Positive(message = "Emission Factor Nitrogen Diesel must be positive")
    private Double emissionFactorNitrogenDiesel; // kgNO2/TJ

    @NotNull(message = "Emission Factor Nitrogen Gasoline is required")
    @Positive(message = "Emission Factor Nitrogen Gasoline must be positive")
    private Double emissionFactorNitrogenGasoline; // kgNO2/TJ

    @NotNull(message = "Potential Methane (GWP) is required")
    @Positive(message = "Potential Methane (GWP) must be positive")
    private Double potentialMethane; // GWPCH4

    @NotNull(message = "Potential Nitrogen (GWP) is required")
    @Positive(message = "Potential Nitrogen (GWP) must be positive")
    private Double potentialNitrogen; // GWPNO2
}
