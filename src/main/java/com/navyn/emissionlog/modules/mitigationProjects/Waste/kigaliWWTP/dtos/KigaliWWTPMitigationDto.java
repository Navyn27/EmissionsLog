package com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.dtos;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.constants.WWTPProjectPhase;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class KigaliWWTPMitigationDto {
    
    @NotNull(message = "Year is required")
    private Integer year;
    
    @NotNull(message = "Project Phase is required")
    private WWTPProjectPhase projectPhase;
}
