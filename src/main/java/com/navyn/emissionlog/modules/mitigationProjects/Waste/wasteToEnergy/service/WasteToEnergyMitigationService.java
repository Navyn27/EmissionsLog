package com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.service;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.dtos.WasteToEnergyMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.models.WasteToEnergyMitigation;

import java.util.List;
import java.util.UUID;

public interface WasteToEnergyMitigationService {
    
    WasteToEnergyMitigation createWasteToEnergyMitigation(WasteToEnergyMitigationDto dto);
    
    WasteToEnergyMitigation updateWasteToEnergyMitigation(UUID id, WasteToEnergyMitigationDto dto);
    
    List<WasteToEnergyMitigation> getAllWasteToEnergyMitigation(Integer year);
}
