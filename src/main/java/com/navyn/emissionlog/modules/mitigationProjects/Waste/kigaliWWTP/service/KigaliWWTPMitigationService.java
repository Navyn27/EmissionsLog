package com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.service;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.dtos.KigaliWWTPMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.models.KigaliWWTPMitigation;

import java.util.List;

public interface KigaliWWTPMitigationService {
    
    KigaliWWTPMitigation createKigaliWWTPMitigation(KigaliWWTPMitigationDto dto);
    
    KigaliWWTPMitigation updateKigaliWWTPMitigation(Long id, KigaliWWTPMitigationDto dto);
    
    List<KigaliWWTPMitigation> getAllKigaliWWTPMitigation(Integer year);
}
