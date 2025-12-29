package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.service;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.dtos.ManureCoveringParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.dtos.ManureCoveringParameterResponseDto;

import java.util.List;
import java.util.UUID;

public interface ManureCoveringParameterService {
    ManureCoveringParameterResponseDto create(ManureCoveringParameterDto dto);
    ManureCoveringParameterResponseDto getById(UUID id);
    List<ManureCoveringParameterResponseDto> getAll();
    ManureCoveringParameterResponseDto update(UUID id, ManureCoveringParameterDto dto);
    void disable(UUID id);
    ManureCoveringParameterResponseDto getLatestActive();
}

