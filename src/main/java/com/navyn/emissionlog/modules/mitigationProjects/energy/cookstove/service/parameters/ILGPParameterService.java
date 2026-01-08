package com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.service.parameters;

import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.dto.parameters.LGPParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.dto.parameters.LGPParameterResponseDto;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface ILGPParameterService {
    LGPParameterResponseDto createParameter(@Valid LGPParameterDto dto);
    LGPParameterResponseDto updateParameter(UUID id, @Valid LGPParameterDto dto);
    LGPParameterResponseDto getParameterById(UUID id);
    List<LGPParameterResponseDto> getAllParameters();
    LGPParameterResponseDto getLatestActive();
    void disableParameter(UUID id);
    void deleteParameter(UUID id);
}

