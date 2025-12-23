package com.navyn.emissionlog.modules.mitigationProjects.BAU.services;

import com.navyn.emissionlog.modules.mitigationProjects.BAU.enums.ESector;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.models.BAU;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.dtos.BAUDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BAUService {
    
    BAU createBAU(BAUDto dto);
    
    BAU updateBAU(UUID id, BAUDto dto);
    
    Optional<BAU> getBAUById(UUID id);
    
    List<BAU> getAllBAUs();
    
    List<BAU> getBAUsByYear(Integer year);
    
    List<BAU> getBAUsBySector(ESector sector);
    
    Optional<BAU> getBAUByYearAndSector(Integer year, ESector sector);
    
    void deleteBAU(UUID id);
}

