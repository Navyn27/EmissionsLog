package com.navyn.emissionlog.modules.agricultureEmissions.dtos.Livestock;

import com.navyn.emissionlog.Enums.Agriculture.ManureManagementLivestock;
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

    @NotNull(message = "Animal population is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Animal population must be greater than 0")
    private Double animalPopulation;
}
