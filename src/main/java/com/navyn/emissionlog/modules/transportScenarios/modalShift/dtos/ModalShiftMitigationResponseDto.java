package com.navyn.emissionlog.modules.transportScenarios.modalShift.dtos;

import com.navyn.emissionlog.modules.transportScenarios.enums.FuelType;
import com.navyn.emissionlog.modules.transportScenarios.enums.VehicleCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModalShiftMitigationResponseDto {
    private UUID id;
    private Integer year;
    private VehicleCategory categoryBeforeShift;
    private VehicleCategory categoryAfterShift;
    private Double vtk; // km
    private Double fuelEconomy; // L/100km
    private Double fleetPopulation;
    private FuelType fuelType;
    private Double bauOfShift; // GgCO2e
    private Double totalFuel; // L
    private Double projectEmissionCarbon; // kg CO2
    private Double projectEmissionMethane; // kg CH4
    private Double projectEmissionNitrogen; // kg N2O
    private Double totalProjectEmission; // GgCO2e
    private Double emissionReduction; // GgCO2e
    private InterventionInfo intervention;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InterventionInfo {
        private UUID id;
        private String name;
    }
}

