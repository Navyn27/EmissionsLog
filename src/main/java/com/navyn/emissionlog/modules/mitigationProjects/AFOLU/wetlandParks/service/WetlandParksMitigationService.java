package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.service;

import com.navyn.emissionlog.Enums.Mitigation.WetlandTreeCategory;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.dtos.WetlandParksMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.models.WetlandParksMitigation;

import java.util.List;
import java.util.Optional;

public interface WetlandParksMitigationService {
    
    WetlandParksMitigation createWetlandParksMitigation(WetlandParksMitigationDto dto);
    
    List<WetlandParksMitigation> getAllWetlandParksMitigation(Integer year, WetlandTreeCategory category);
    
    Optional<WetlandParksMitigation> getByYearAndCategory(Integer year, WetlandTreeCategory category);
}
