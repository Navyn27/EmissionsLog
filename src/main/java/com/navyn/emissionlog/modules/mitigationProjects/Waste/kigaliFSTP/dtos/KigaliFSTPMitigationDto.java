package com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.dtos;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.constants.ProjectPhase;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class KigaliFSTPMitigationDto {
    
    @NotNull(message = "Year is required")
    private Integer year;
    
    @NotNull(message = "Project Phase is required")
    private ProjectPhase projectPhase;
}
