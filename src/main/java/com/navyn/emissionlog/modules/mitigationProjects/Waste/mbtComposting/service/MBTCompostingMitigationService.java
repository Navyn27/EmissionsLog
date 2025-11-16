package com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.service;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.dtos.MBTCompostingMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.models.MBTCompostingMitigation;

import java.util.List;

public interface MBTCompostingMitigationService {
    
    MBTCompostingMitigation createMBTCompostingMitigation(MBTCompostingMitigationDto dto);
    
    List<MBTCompostingMitigation> getAllMBTCompostingMitigation(Integer year);
}
