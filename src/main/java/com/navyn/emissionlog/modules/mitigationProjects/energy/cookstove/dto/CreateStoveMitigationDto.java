package com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.dto;

import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.enums.EStoveType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateStoveMitigationDto {

    @Schema(description = "The year of the data entry.", example = "2023", required = true)
    @NotNull(message = "Year cannot be null")
    @Min(value = 1900, message = "Year must be greater than or equal to 1900")
    private int year;

    @Schema(description = "Stove type", example = "ELECTRIC", required = true)
    @NotNull(message = "Stove type cannot be null")
    private EStoveType stoveType;

    @Schema(description = "Total number of stoves installed up to and including this year.", example = "1000", required = true)
    @NotNull(message = "Units installed cannot be null")
    @Positive(message = "Units installed must be a positive number")
    private int unitsInstalled;

    @Schema(description = "Efficiency of the stove", example = "80.5", required = true)
    @NotNull(message = "Efficiency cannot be null")
    @Positive(message = "Efficiency must be a positive number")
    private Double efficiency;

    @Schema(description = "Project Intervention ID", example = "123e4567-e89b-12d3-a456-426614174000", required = false)
    private UUID projectInterventionId;
}

