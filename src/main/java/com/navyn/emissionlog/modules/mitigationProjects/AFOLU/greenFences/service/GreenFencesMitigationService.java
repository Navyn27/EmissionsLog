package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.greenFences.service;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.greenFences.dtos.GreenFencesMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.greenFences.models.GreenFencesMitigation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GreenFencesMitigationService {
    
    GreenFencesMitigation createGreenFencesMitigation(GreenFencesMitigationDto dto);
    
    GreenFencesMitigation updateGreenFencesMitigation(UUID id, GreenFencesMitigationDto dto);
    
    List<GreenFencesMitigation> getAllGreenFencesMitigation(Integer year);
    
    Optional<GreenFencesMitigation> getByYear(Integer year);
}
