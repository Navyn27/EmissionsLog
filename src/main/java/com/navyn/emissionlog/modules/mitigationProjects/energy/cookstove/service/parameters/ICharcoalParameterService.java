package com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.service.parameters;

import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.dto.parameters.CharcoalParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.dto.parameters.CharcoalParameterResponseDto;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface ICharcoalParameterService {
    CharcoalParameterResponseDto createParameter(@Valid CharcoalParameterDto dto);
    CharcoalParameterResponseDto updateParameter(UUID id, @Valid CharcoalParameterDto dto);
    CharcoalParameterResponseDto getParameterById(UUID id);
    List<CharcoalParameterResponseDto> getAllParameters();
    CharcoalParameterResponseDto getLatestActive();
    void disableParameter(UUID id);
    void deleteParameter(UUID id);
}

