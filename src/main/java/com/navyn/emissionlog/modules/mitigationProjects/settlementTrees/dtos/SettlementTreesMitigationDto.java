package com.navyn.emissionlog.modules.mitigationProjects.settlementTrees.dtos;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SettlementTreesMitigationDto {
    
    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be 1900 or later")
    @Max(value = 2100, message = "Year must be 2100 or earlier")
    private Integer year;
    
    @NotNull(message = "Cumulative number of trees is required")
    @DecimalMin(value = "0.0", message = "Cumulative number of trees must be at least 0")
    private Double cumulativeNumberOfTrees;
    
    @NotNull(message = "Number of trees planted is required")
    @DecimalMin(value = "0.0", message = "Number of trees planted must be at least 0")
    private Double numberOfTreesPlanted;
    
    @NotNull(message = "AGB of single tree in previous year is required")
    @DecimalMin(value = "0.0", message = "AGB of single tree in previous year must be at least 0")
    private Double agbSingleTreePreviousYear; // m3
    
    @NotNull(message = "AGB of single tree in current year is required")
    @DecimalMin(value = "0.0", message = "AGB of single tree in current year must be at least 0")
    private Double agbSingleTreeCurrentYear; // m3
}
