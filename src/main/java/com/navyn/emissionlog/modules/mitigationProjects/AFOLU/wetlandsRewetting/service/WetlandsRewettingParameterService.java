package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandsRewetting.service;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandsRewetting.dtos.WetlandsRewettingParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandsRewetting.dtos.WetlandsRewettingParameterResponseDto;

import java.util.List;
import java.util.UUID;

public interface WetlandsRewettingParameterService {

    WetlandsRewettingParameterResponseDto create(WetlandsRewettingParameterDto dto);

    WetlandsRewettingParameterResponseDto getById(UUID id);

    List<WetlandsRewettingParameterResponseDto> getAll();

    WetlandsRewettingParameterResponseDto update(UUID id, WetlandsRewettingParameterDto dto);

    void disable(UUID id);

    WetlandsRewettingParameterResponseDto getLatestActive();
}
