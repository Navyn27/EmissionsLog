package com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.service;

import com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.dto.LightBulbParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.dto.LightBulbParameterResponseDto;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface ILightBulbParameterService {
    LightBulbParameterResponseDto createLightBulbParameter(@Valid LightBulbParameterDto dto);

    LightBulbParameterResponseDto updateLightBulbParameter(UUID id, @Valid LightBulbParameterDto dto);

    LightBulbParameterResponseDto getLightBulbParameterById(UUID id);

    List<LightBulbParameterResponseDto> getAllLightBulbParameters();

    LightBulbParameterResponseDto getLatestActive();

    void disable(UUID id);
}
