package com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.service;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.dtos.ISWMMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.models.ISWMMitigation;

import java.util.List;
import java.util.UUID;

public interface ISWMMitigationService {
    
    ISWMMitigation createISWMMitigation(ISWMMitigationDto dto);
    
    ISWMMitigation updateISWMMitigation(UUID id, ISWMMitigationDto dto);
    
    void deleteISWMMitigation(UUID id);
    
    List<ISWMMitigation> getAllISWMMitigation(Integer year);
    
}
