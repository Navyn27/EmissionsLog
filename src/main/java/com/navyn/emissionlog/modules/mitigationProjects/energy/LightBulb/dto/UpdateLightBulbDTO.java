package com.navyn.emissionlog.modules.mitigationProjects.Energy.LightBulb.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class UpdateLightBulbDTO {

    @Schema(description = "The year of the data entry.", example = "2023")
    @Min(value = 1900, message = "Year must be greater than or equal to 1900")
    private Integer year;

    @Schema(description = "Total number of light bulbs installed in a year.", example = "1000")
    @Positive(message = "Total installed bulbs per year must be a positive number")
    private Double totalInstalledBulbsPerYear;

    @Schema(description = "Reduction capacity of a single light bulb.", example = "0.1")
    @Positive(message = "Reduction capacity per bulb must be a positive number")
    private Double reductionCapacityPerBulb;

    @Schema(description = "Emission factor.", example = "0.5")
    @Positive(message = "Emission factor must be a positive number")
    private Double emissionFactor;

    @Schema(description = "Business as usual (BAU) emissions.", example = "500")
    @Positive(message = "BAU must be a positive number")
    private Double bau;
}
