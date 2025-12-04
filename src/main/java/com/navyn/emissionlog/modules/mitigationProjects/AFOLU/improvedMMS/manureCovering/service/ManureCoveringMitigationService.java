package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.service;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.dtos.ManureCoveringMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.models.ManureCoveringMitigation;

import java.util.List;

public interface ManureCoveringMitigationService {
    
    ManureCoveringMitigation createManureCoveringMitigation(ManureCoveringMitigationDto dto);
    
    List<ManureCoveringMitigation> getAllManureCoveringMitigation(Integer year);
}
