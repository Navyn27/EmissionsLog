package com.navyn.emissionlog.modules.mitigationProjects.zeroTillage.service;

import com.navyn.emissionlog.modules.mitigationProjects.zeroTillage.dtos.ZeroTillageMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.zeroTillage.models.ZeroTillageMitigation;

import java.util.List;
import java.util.Optional;

public interface ZeroTillageMitigationService {
    
    ZeroTillageMitigation createZeroTillageMitigation(ZeroTillageMitigationDto dto);
    
    List<ZeroTillageMitigation> getAllZeroTillageMitigation(Integer year);
    
    Optional<ZeroTillageMitigation> getByYear(Integer year);
}
