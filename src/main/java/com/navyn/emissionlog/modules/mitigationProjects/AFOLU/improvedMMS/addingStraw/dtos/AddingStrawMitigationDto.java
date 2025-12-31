package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AddingStrawMitigationDto {
    
    @NotNull(message = "Year is required")
    private Integer year;
    
    @NotNull(message = "Number of cows is required")
    @Min(value = 0, message = "Number of cows must be at least 0")
    private Integer numberOfCows;
    
    private UUID interventionId;
    
    // Temporary field for Excel import - intervention name (will be converted to interventionId)
    private String interventionName;
}
