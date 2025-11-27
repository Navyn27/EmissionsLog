package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.dtos;

import com.navyn.emissionlog.Enums.Mitigation.WetlandTreeCategory;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class WetlandParksMitigationDto {
    
    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be 1900 or later")
    @Max(value = 2100, message = "Year must be 2100 or earlier")
    private Integer year;
    
    @NotNull(message = "Tree category is required")
    private WetlandTreeCategory treeCategory;

    @NotNull(message = "Area planted is required")
    @DecimalMin(value = "0.0", message = "Area planted must be at least 0")
    private Double areaPlanted; // ha
    
    @NotNull(message = "Aboveground biomass (AGB) is required")
    @DecimalMin(value = "0.0", message = "AGB must be at least 0")
    private Double abovegroundBiomassAGB; // m3/ha
}
