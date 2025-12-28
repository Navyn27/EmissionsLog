package com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.service;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.dtos.ISWMParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.dtos.ISWMParameterResponseDto;

import java.util.List;
import java.util.UUID;

public interface ISWMParameterService {

    ISWMParameterResponseDto createISWMParameter(ISWMParameterDto dto);

    ISWMParameterResponseDto updateISWMParameter(UUID id, ISWMParameterDto dto);

    ISWMParameterResponseDto getISWMParameterById(UUID id);

    List<ISWMParameterResponseDto> getAllISWMParameters();

    void deleteISWMParameter(UUID id);

    ISWMParameterResponseDto getLatestActive();

    void disable(UUID id);
}

