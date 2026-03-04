package com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.service.parameters;

import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.dto.parameters.ElectricityParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.dto.parameters.ElectricityParameterResponseDto;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface IElectricityParameterService {
    ElectricityParameterResponseDto createParameter(@Valid ElectricityParameterDto dto);
    ElectricityParameterResponseDto updateParameter(UUID id, @Valid ElectricityParameterDto dto);
    ElectricityParameterResponseDto getParameterById(UUID id);
    List<ElectricityParameterResponseDto> getAllParameters();
    ElectricityParameterResponseDto getLatestActive();
    void disableParameter(UUID id);
    void deleteParameter(UUID id);
}

