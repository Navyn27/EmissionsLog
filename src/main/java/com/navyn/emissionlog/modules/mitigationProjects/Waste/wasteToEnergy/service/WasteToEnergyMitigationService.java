package com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.service;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.dtos.WasteToEnergyMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.models.WasteToEnergyMitigation;

import java.util.List;

public interface WasteToEnergyMitigationService {
    
    WasteToEnergyMitigation createWasteToEnergyMitigation(WasteToEnergyMitigationDto dto);
    
    List<WasteToEnergyMitigation> getAllWasteToEnergyMitigation(Integer year);
}
