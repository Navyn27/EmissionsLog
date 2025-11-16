package com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.service;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.dtos.ISWMMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.models.ISWMMitigation;

import java.util.List;

public interface ISWMMitigationService {
    
    ISWMMitigation createISWMMitigation(ISWMMitigationDto dto);
    
    List<ISWMMitigation> getAllISWMMitigation(Integer year);
}
