package com.navyn.emissionlog.modules.transportScenarios.ElectricVehicle.dtos;

import com.navyn.emissionlog.modules.transportScenarios.enums.VehicleCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ElectricVehicleMitigationResponseDto {
    private UUID id;
    private Integer year;
    private VehicleCategory vehicleCategory;
    private Double vkt; // km
    private Double fleetPopulation;
    private Double evPowerDemand; // km/kWh
    private Double bau; // GgCO₂e
    private Double annualElectricityConsumption; // MWh
    private Double totalProjectEmission; // GgCO₂e
    private Double emissionReduction; // GgCO₂e
    private InterventionInfo intervention;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InterventionInfo {
        private UUID id;
        private String name;
    }
}

