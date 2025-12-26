package com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.service;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.dtos.WasteToWtEParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.dtos.WasteToWtEParameterResponseDto;

import java.util.List;
import java.util.UUID;

public interface WasteToWtEParameterService {

    WasteToWtEParameterResponseDto createWasteToWtEParameter(WasteToWtEParameterDto dto);

    WasteToWtEParameterResponseDto updateWasteToWtEParameter(UUID id, WasteToWtEParameterDto dto);

    WasteToWtEParameterResponseDto getWasteToWtEParameterById(UUID id);

    List<WasteToWtEParameterResponseDto> getAllWasteToWtEParameters();

    void deleteWasteToWtEParameter(UUID id);

    WasteToWtEParameterResponseDto getLatestActive();

    void disable(UUID id);
}

