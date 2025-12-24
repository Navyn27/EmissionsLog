package com.navyn.emissionlog.modules.mitigationProjects.BAU.dtos;

import com.navyn.emissionlog.modules.mitigationProjects.BAU.enums.ESector;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for creating and updating a BAU (Business As Usual) record")
public class BAUDto {

    @Schema(description = "The BAU value in ktCO2e", example = "150.5")
    @NotNull(message = "BAU value is required")
    @PositiveOrZero(message = "BAU value must be a positive number or zero")
    private Double value;

    @Schema(description = "The sector for this BAU record", example = "WASTE")
    @NotNull(message = "Sector is required")
    private ESector sector;

    @Schema(description = "The year for this BAU record", example = "2025")
    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be 1900 or later")
    private Integer year;
}

