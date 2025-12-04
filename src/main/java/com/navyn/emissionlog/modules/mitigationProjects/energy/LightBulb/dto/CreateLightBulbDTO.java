package com.navyn.emissionlog.modules.mitigationProjects.Energy.LightBulb.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreateLightBulbDTO {

    @Schema(description = "The year of the data entry.", example = "2023", required = true)
    @NotNull(message = "Year cannot be null")
    @Min(value = 1900, message = "Year must be greater than or equal to 1900")
    private int year;

    @Schema(description = "Total number of light bulbs installed in a year.", example = "1000", required = true)
    @NotNull(message = "Total installed bulbs per year cannot be null")
    @Positive(message = "Total installed bulbs per year must be a positive number")
    private double totalInstalledBulbsPerYear;

    @Schema(description = "Reduction capacity of a single light bulb.", example = "0.1", required = true)
    @NotNull(message = "Reduction capacity per bulb cannot be null")
    @Positive(message = "Reduction capacity per bulb must be a positive number")
    private double reductionCapacityPerBulb;

    @Schema(description = "Emission factor.", example = "0.5", required = true)
    @NotNull(message = "Emission factor cannot be null")
    @Positive(message = "Emission factor must be a positive number")
    private double emissionFactor;

    @Schema(description = "Business as usual (BAU) emissions.", example = "500", required = true)
    @NotNull(message = "BAU cannot be null")
    @Positive(message = "BAU must be a positive number")
    private double bau;
}
