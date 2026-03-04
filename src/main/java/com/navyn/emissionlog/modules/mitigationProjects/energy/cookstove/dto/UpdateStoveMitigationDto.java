package com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.dto;

import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.enums.EStoveType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.UUID;

@Data
public class UpdateStoveMitigationDto {

    @Schema(description = "The year of the data entry.", example = "2023")
    @Min(value = 1900, message = "Year must be greater than or equal to 1900")
    private Integer year;

    @Schema(description = "Stove type", example = "ELECTRIC")
    private EStoveType stoveType;

    @Schema(description = "Total number of stoves installed up to and including this year.", example = "1000")
    @Positive(message = "Units installed must be a positive number")
    private Integer unitsInstalled;

    @Schema(description = "Efficiency of the stove", example = "80.5")
    @Positive(message = "Efficiency must be a positive number")
    private Double efficiency;

    @Schema(description = "Project Intervention ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID projectInterventionId;
}

