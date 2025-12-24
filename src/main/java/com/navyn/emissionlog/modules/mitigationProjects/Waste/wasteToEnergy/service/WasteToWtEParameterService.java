package com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.service;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.dtos.WasteToWtEParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.models.WasteToWtEParameter;

import java.util.List;
import java.util.UUID;

public interface WasteToWtEParameterService {

    WasteToWtEParameter createWasteToWtEParameter(WasteToWtEParameterDto dto);

    WasteToWtEParameter updateWasteToWtEParameter(UUID id, WasteToWtEParameterDto dto);

    WasteToWtEParameter getWasteToWtEParameterById(UUID id);

    List<WasteToWtEParameter> getAllWasteToWtEParameters();

    void deleteWasteToWtEParameter(UUID id);

    WasteToWtEParameter getLatestWasteToWtEParameter();
}

