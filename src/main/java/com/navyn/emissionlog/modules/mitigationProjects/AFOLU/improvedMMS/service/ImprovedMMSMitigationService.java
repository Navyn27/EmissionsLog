package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.service;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.dtos.ImprovedMMSMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.models.ImprovedMMSMitigation;

import java.util.List;
import java.util.Optional;

public interface ImprovedMMSMitigationService {
    
    ImprovedMMSMitigation createImprovedMMSMitigation(ImprovedMMSMitigationDto dto);
    
    List<ImprovedMMSMitigation> getAllImprovedMMSMitigation(Integer year);
    
    Optional<ImprovedMMSMitigation> getByYear(Integer year);
}
