package com.navyn.emissionlog.modules.mitigationProjects.protectiveForest.service;

import com.navyn.emissionlog.Enums.Mitigation.ProtectiveForestCategory;
import com.navyn.emissionlog.modules.mitigationProjects.protectiveForest.dtos.ProtectiveForestMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.protectiveForest.models.ProtectiveForestMitigation;

import java.util.List;
import java.util.Optional;

public interface ProtectiveForestMitigationService {
    
    ProtectiveForestMitigation createProtectiveForestMitigation(ProtectiveForestMitigationDto dto);
    
    List<ProtectiveForestMitigation> getAllProtectiveForestMitigation(
        Integer year, 
        ProtectiveForestCategory category
    );
    
    Optional<ProtectiveForestMitigation> getByYearAndCategory(
        Integer year, 
        ProtectiveForestCategory category
    );
}
