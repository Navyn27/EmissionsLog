package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.streetTrees.dtos;

import com.navyn.emissionlog.Enums.Metrics.VolumeUnits;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.UUID;

@Data
public class StreetTreesMitigationDto {

    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be 1900 or later")
    @Max(value = 2100, message = "Year must be 2100 or earlier")
    private Integer year;

    @NotNull(message = "Number of trees planted is required")
    @DecimalMin(value = "0.0", message = "Number of trees planted must be at least 0")
    private Double numberOfTreesPlanted;

    @NotNull(message = "AGB of single tree in current year is required")
    @DecimalMin(value = "0.0", message = "AGB of single tree in current year must be at least 0")
    private Double agbSingleTreeCurrentYear;

    @NotNull(message = "AGB unit is required")
    private VolumeUnits agbUnit; // Unit for AGB volume (standard: cubic meters)

    // Optional intervention reference
    private UUID interventionId;

    // Temporary field for Excel import - intervention name (will be converted to interventionId)
    private String interventionName;
}
