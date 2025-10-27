package com.navyn.emissionlog.modules.mitigationProjects.zeroTillage.dtos;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ZeroTillageMitigationDto {
    
    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be 1900 or later")
    @Max(value = 2100, message = "Year must be 2100 or earlier")
    private Integer year;
    
    @NotNull(message = "Area under zero-tillage is required")
    @DecimalMin(value = "0.0", message = "Area under zero-tillage must be at least 0")
    private Double areaUnderZeroTillage; // ha
}
