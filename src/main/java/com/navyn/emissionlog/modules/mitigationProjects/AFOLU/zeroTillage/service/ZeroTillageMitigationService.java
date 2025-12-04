package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.zeroTillage.service;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.zeroTillage.dtos.ZeroTillageMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.zeroTillage.models.ZeroTillageMitigation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ZeroTillageMitigationService {
    
    ZeroTillageMitigation createZeroTillageMitigation(ZeroTillageMitigationDto dto);
    
    ZeroTillageMitigation updateZeroTillageMitigation(UUID id, ZeroTillageMitigationDto dto);
    
    List<ZeroTillageMitigation> getAllZeroTillageMitigation(Integer year);
    
    Optional<ZeroTillageMitigation> getByYear(Integer year);
}
