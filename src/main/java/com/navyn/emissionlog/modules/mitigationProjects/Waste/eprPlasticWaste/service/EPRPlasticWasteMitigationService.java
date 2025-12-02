package com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.service;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.dtos.EPRPlasticWasteMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.models.EPRPlasticWasteMitigation;

import java.util.List;
import java.util.UUID;

public interface EPRPlasticWasteMitigationService {
    
    EPRPlasticWasteMitigation createEPRPlasticWasteMitigation(EPRPlasticWasteMitigationDto dto);
    
    EPRPlasticWasteMitigation updateEPRPlasticWasteMitigation(UUID id, EPRPlasticWasteMitigationDto dto);
    
    List<EPRPlasticWasteMitigation> getAllEPRPlasticWasteMitigation(Integer year);
}
