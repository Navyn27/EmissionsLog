package com.navyn.emissionlog.modules.transportScenarios.ElectricVehicle.dtos;

import com.navyn.emissionlog.modules.transportScenarios.enums.VehicleCategory;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.UUID;

@Data
public class ElectricVehicleMitigationDto {
    
    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be 1900 or later")
    @Max(value = 2100, message = "Year must be 2100 or earlier")
    private Integer year;
    
    @NotNull(message = "Vehicle Category is required")
    private VehicleCategory vehicleCategory;
    
    @NotNull(message = "VTK (Vehicle Kilometers Traveled) is required")
    @Positive(message = "VTK must be positive")
    private Double vkt; // km
    
    @NotNull(message = "Fleet Population is required")
    @Positive(message = "Fleet Population must be positive")
    private Double fleetPopulation;
    
    @NotNull(message = "EV Power Demand is required")
    @Positive(message = "EV Power Demand must be positive")
    private Double evPowerDemand; // km/kWh
    
    private Double bau; // GgCO₂e (optional)
    
    @NotNull(message = "Intervention is required")
    private UUID interventionId; // Foreign key to Intervention table
    
    // For Excel upload - intervention name (will be converted to UUID)
    private String interventionName; // Used for Excel import
}

