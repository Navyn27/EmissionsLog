package com.navyn.emissionlog.modules.mitigationProjects.greenFences.service;

import com.navyn.emissionlog.modules.mitigationProjects.greenFences.dtos.GreenFencesMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.greenFences.models.GreenFencesMitigation;

import java.util.List;
import java.util.Optional;

public interface GreenFencesMitigationService {
    
    GreenFencesMitigation createGreenFencesMitigation(GreenFencesMitigationDto dto);
    
    List<GreenFencesMitigation> getAllGreenFencesMitigation(Integer year);
    
    Optional<GreenFencesMitigation> getByYear(Integer year);
}
