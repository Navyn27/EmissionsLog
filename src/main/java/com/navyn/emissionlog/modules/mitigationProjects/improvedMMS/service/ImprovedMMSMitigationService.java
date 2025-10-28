package com.navyn.emissionlog.modules.mitigationProjects.improvedMMS.service;

import com.navyn.emissionlog.modules.mitigationProjects.improvedMMS.dtos.ImprovedMMSMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.improvedMMS.models.ImprovedMMSMitigation;

import java.util.List;
import java.util.Optional;

public interface ImprovedMMSMitigationService {
    
    ImprovedMMSMitigation createImprovedMMSMitigation(ImprovedMMSMitigationDto dto);
    
    List<ImprovedMMSMitigation> getAllImprovedMMSMitigation(Integer year);
    
    Optional<ImprovedMMSMitigation> getByYear(Integer year);
}
