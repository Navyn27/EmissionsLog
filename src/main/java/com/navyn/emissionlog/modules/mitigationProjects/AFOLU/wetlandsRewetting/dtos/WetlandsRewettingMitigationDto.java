package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandsRewetting.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class WetlandsRewettingMitigationDto {

    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be 1900 or later")
    @Max(value = 2100, message = "Year must be 2100 or earlier")
    private Integer year;

    @NotNull(message = "Area of rewetted mineral wetlands (ha) is required")
    @DecimalMin(value = "0.0", message = "Area must be at least 0")
    private Double areaRewettedMineralWetlandsHa;

    @NotNull(message = "Swap is required")
    private UUID swapId;

    // Temporary field for Excel import – swap name (resolved to swapId in service)
    private String swapName;
}
