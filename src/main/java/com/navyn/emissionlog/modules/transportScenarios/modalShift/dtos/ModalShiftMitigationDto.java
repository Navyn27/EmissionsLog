package com.navyn.emissionlog.modules.transportScenarios.modalShift.dtos;

import com.navyn.emissionlog.modules.transportScenarios.enums.FuelType;
import com.navyn.emissionlog.modules.transportScenarios.enums.VehicleCategory;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.UUID;

@Data
public class ModalShiftMitigationDto {
    
    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be 1900 or later")
    @Max(value = 2100, message = "Year must be 2100 or earlier")
    private Integer year;
    
    @NotNull(message = "Category Before Shift is required")
    private VehicleCategory categoryBeforeShift;
    
    @NotNull(message = "Category After Shift is required")
    private VehicleCategory categoryAfterShift;
    
    @NotNull(message = "VTK (Vehicle Kilometers Traveled) is required")
    @Positive(message = "VTK must be positive")
    private Double vtk; // km
    
    @NotNull(message = "Fuel Economy is required")
    @Positive(message = "Fuel Economy must be positive")
    private Double fuelEconomy; // L/100km
    
    @NotNull(message = "Fleet Population is required")
    @Positive(message = "Fleet Population must be positive")
    private Double fleetPopulation;
    
    @NotNull(message = "Fuel Type is required")
    private FuelType fuelType;
    
    private Double bauOfShift; // GgCO2e (optional)
    
    @NotNull(message = "Intervention is required")
    private UUID interventionId; // Foreign key to Intervention table
    
    // For Excel upload - intervention name (will be converted to UUID)
    private String interventionName; // Used for Excel import
}

