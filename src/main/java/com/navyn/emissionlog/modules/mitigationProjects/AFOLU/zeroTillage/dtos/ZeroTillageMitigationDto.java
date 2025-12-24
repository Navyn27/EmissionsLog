package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.zeroTillage.dtos;

import com.navyn.emissionlog.Enums.Metrics.AreaUnits;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.UUID;

@Data
public class ZeroTillageMitigationDto {
    
    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be 1900 or later")
    @Max(value = 2100, message = "Year must be 2100 or earlier")
    private Integer year;
    
    @NotNull(message = "Area under zero-tillage is required")
    @DecimalMin(value = "0.0", message = "Area under zero-tillage must be at least 0")
    private Double areaUnderZeroTillage;
    @NotNull(message = "Urea applied on zero-tillage area is required")
    @DecimalMin(value = "0.0", message = "Urea applied on zero-tillage area, tonnes must be at least 0")
    private Double ureaApplied;
    
    @NotNull(message = "Area unit is required")
    private AreaUnits areaUnit; // Unit for area (standard: ha)
    
    // Optional intervention reference
    private UUID interventionId;
}
