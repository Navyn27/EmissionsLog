package com.navyn.emissionlog.modules.agricultureEmissions.dtos.Livestock;

import com.navyn.emissionlog.Enums.Agriculture.ManureManagementLivestock;
import com.navyn.emissionlog.Enums.Agriculture.ManureManagementSystem;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ManureManagementEmissionsDto {
    
    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be 1900 or later")
    @Max(value = 2100, message = "Year must be 2100 or earlier")
    private Integer year;
    
    @NotNull(message = "Livestock species is required")
    private ManureManagementLivestock species;
    
    @NotNull(message = "Manure management system is required")
    private ManureManagementSystem manureManagementSystem;
    
    @NotNull(message = "Animal population is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Animal population must be greater than 0")
    private Double animalPopulation;
    
    @NotNull(message = "Average annual temperature is required")
    @DecimalMin(value = "-50.0", message = "Temperature must be at least -50°C")
    @DecimalMax(value = "60.0", message = "Temperature must be at most 60°C")
    private Double averageAnnualTemperature;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "Average animal weight must be greater than 0")
    private Double averageAnimalWeight; // Optional
}
