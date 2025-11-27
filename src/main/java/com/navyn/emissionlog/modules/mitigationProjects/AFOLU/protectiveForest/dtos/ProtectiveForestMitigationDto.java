package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.protectiveForest.dtos;

import com.navyn.emissionlog.Enums.Mitigation.ProtectiveForestCategory;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ProtectiveForestMitigationDto {
    
    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be 1900 or later")
    @Max(value = 2100, message = "Year must be 2100 or earlier")
    private Integer year;
    
    @NotNull(message = "Category is required")
    private ProtectiveForestCategory category;
    
    @NotNull(message = "Area planted is required")
    @DecimalMin(value = "0.0", message = "Area planted must be at least 0")
    private Double areaPlanted; // ha
    
    // NO agbPreviousYear - auto-fetched from DB!
    
    @NotNull(message = "AGB in current year is required")
    @DecimalMin(value = "0.0", message = "AGB in current year must be at least 0")
    private Double agbCurrentYear; // m3/ha
}
