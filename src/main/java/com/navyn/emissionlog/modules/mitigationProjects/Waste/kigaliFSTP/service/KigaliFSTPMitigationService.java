package com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.service;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.dtos.KigaliFSTPMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.models.KigaliFSTPMitigation;

import java.util.List;

public interface KigaliFSTPMitigationService {
    
    KigaliFSTPMitigation createKigaliFSTPMitigation(KigaliFSTPMitigationDto dto);
    
    List<KigaliFSTPMitigation> getAllKigaliFSTPMitigation(Integer year);
}
