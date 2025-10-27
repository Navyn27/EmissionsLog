package com.navyn.emissionlog.modules.mitigationProjects.streetTrees.service;

import com.navyn.emissionlog.modules.mitigationProjects.streetTrees.dtos.StreetTreesMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.streetTrees.models.StreetTreesMitigation;

import java.util.List;
import java.util.Optional;

public interface StreetTreesMitigationService {
    
    StreetTreesMitigation createStreetTreesMitigation(StreetTreesMitigationDto dto);
    
    List<StreetTreesMitigation> getAllStreetTreesMitigation(Integer year);
    
    Optional<StreetTreesMitigation> getByYear(Integer year);
}
