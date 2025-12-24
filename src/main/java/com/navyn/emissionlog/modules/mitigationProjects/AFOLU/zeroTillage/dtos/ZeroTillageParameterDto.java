package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.zeroTillage.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ZeroTillageParameterDto {

    @NotNull(message = "Carbon increase in soil is required")
    @DecimalMin(value = "0.0", message = "Carbon increase in soil must be at least 0")
    private Double carbonIncreaseInSoil; // Carbon increase in soil, tonnes C/ha per year

    @NotNull(message = "C to CO2 conversion is required")
    @DecimalMin(value = "0.0", message = "C to CO2 conversion must be at least 0")
    private Double carbonToC02; // C to CO2 conversion

    @NotNull(message = "Emission factor from urea is required")
    @DecimalMin(value = "0.0", message = "Emission factor from urea must be at least 0")
    private Double emissionFactorFromUrea; // Emission factor from urea
}
