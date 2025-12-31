package com.navyn.emissionlog.modules.transportScenarios.modalShift.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ModalShiftParameterResponseDto {
    private UUID id;
    private Double energyContentDiesel; // MJ/litre
    private Double energyContentGasoline; // MJ/litre
    private Double emissionFactorCarbonDiesel; // kgCO2/TJ
    private Double emissionFactorCarbonGasoline; // kgCO2/TJ
    private Double emissionFactorMethaneDiesel; // kgCH4/TJ
    private Double emissionFactorMethaneGasoline; // kgCH4/TJ
    private Double emissionFactorNitrogenDiesel; // kgNO2/TJ
    private Double emissionFactorNitrogenGasoline; // kgNO2/TJ
    private Double potentialMethane; // GWPCH4
    private Double potentialNitrogen; // GWPNO2
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

