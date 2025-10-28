package com.navyn.emissionlog.modules.mitigationProjects.improvedMMS.dtos;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ImprovedMMSMitigationDto {
    
    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be 1900 or later")
    @Max(value = 2100, message = "Year must be 2100 or earlier")
    private Integer year;
    
    @NotNull(message = "Number of cows is required")
    @Min(value = 0, message = "Number of cows must be at least 0")
    private Integer numberOfCows;
}
