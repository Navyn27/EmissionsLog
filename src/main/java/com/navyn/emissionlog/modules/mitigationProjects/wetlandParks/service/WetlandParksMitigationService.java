package com.navyn.emissionlog.modules.mitigationProjects.wetlandParks.service;

import com.navyn.emissionlog.Enums.Mitigation.WetlandTreeCategory;
import com.navyn.emissionlog.modules.mitigationProjects.wetlandParks.dtos.WetlandParksMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.wetlandParks.models.WetlandParksMitigation;

import java.util.List;
import java.util.Optional;

public interface WetlandParksMitigationService {
    
    WetlandParksMitigation createWetlandParksMitigation(WetlandParksMitigationDto dto);
    
    List<WetlandParksMitigation> getAllWetlandParksMitigation(Integer year, WetlandTreeCategory category);
    
    Optional<WetlandParksMitigation> getByYearAndCategory(Integer year, WetlandTreeCategory category);
}
