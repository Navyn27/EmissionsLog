package com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.service;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.dtos.LandfillGasParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.dtos.LandfillGasParameterResponseDto;

import java.util.List;
import java.util.UUID;

public interface LandfillGasParameterService {

    LandfillGasParameterResponseDto createLandfillGasParameter(LandfillGasParameterDto dto);

    LandfillGasParameterResponseDto updateLandfillGasParameter(UUID id, LandfillGasParameterDto dto);

    LandfillGasParameterResponseDto getLandfillGasParameterById(UUID id);

    List<LandfillGasParameterResponseDto> getAllLandfillGasParameters();

    void deleteLandfillGasParameter(UUID id);

    LandfillGasParameterResponseDto getLatestActive();

    void disable(UUID id);
}
