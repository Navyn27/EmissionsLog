package com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.service;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.dtos.LandfillGasUtilizationMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.models.LandfillGasUtilizationMitigation;

import java.util.List;
import java.util.UUID;

public interface LandfillGasUtilizationMitigationService {
    
    LandfillGasUtilizationMitigation createLandfillGasUtilizationMitigation(LandfillGasUtilizationMitigationDto dto);
    
    LandfillGasUtilizationMitigation updateLandfillGasUtilizationMitigation(UUID id, LandfillGasUtilizationMitigationDto dto);
    
    void deleteLandfillGasUtilizationMitigation(UUID id);
    
    List<LandfillGasUtilizationMitigation> getAllLandfillGasUtilizationMitigation(Integer year);
}
