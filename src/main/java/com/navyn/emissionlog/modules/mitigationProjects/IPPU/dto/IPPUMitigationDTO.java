package com.navyn.emissionlog.modules.mitigationProjects.IPPU.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
@Schema(description = "Data Transfer Object for creating and updating an IPPU Mitigation entry. Contains all the necessary input fields for calculation.")
public class IPPUMitigationDTO {

    @Schema(description = "The year of the mitigation calculation.", example = "2025", required = true)
    @NotNull(message = "Year cannot be null.")
    @Min(value = 1990, message = "Year must be 1990 or later.")
    private int year;

    @Schema(description = "Business-as-usual (BAU) emissions in ktCO2e.", example = "150.5", required = true)
    @NotNull(message = "BAU value cannot be null.")
    @PositiveOrZero(message = "BAU value must be a positive number or zero.")
    private double bau;

    @Schema(description = "Name of the F-gas being mitigated.", example = "Perfluoromethane (PFC-14)", required = true)
    @NotBlank(message = "F-gas name cannot be blank.")
    private String fGasName;

    @Schema(description = "Amount of F-gas avoided in kilograms (kg).", example = "5000", required = true)
    @NotNull(message = "Amount of avoided F-gas cannot be null.")
    @Positive(message = "Amount of avoided F-gas must be a positive number.")
    private double amountOfAvoidedFGas;

    @Schema(description = "Global Warming Potential (GWP) factor for the specific F-gas.", example = "7390", required = true)
    @NotNull(message = "GWP factor cannot be null.")
    @Positive(message = "GWP factor must be a positive number.")
    private double gwpFactor;
}
