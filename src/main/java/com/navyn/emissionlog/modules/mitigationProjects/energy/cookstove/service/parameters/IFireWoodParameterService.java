package com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.service.parameters;

import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.dto.parameters.FireWoodParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.dto.parameters.FireWoodParameterResponseDto;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface IFireWoodParameterService {
    FireWoodParameterResponseDto createParameter(@Valid FireWoodParameterDto dto);
    FireWoodParameterResponseDto updateParameter(UUID id, @Valid FireWoodParameterDto dto);
    FireWoodParameterResponseDto getParameterById(UUID id);
    List<FireWoodParameterResponseDto> getAllParameters();
    FireWoodParameterResponseDto getLatestActive();
    void disableParameter(UUID id);
    void deleteParameter(UUID id);
}

