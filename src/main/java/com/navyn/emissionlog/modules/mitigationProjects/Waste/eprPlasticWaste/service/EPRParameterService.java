package com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.service;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.dtos.EPRParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.dtos.EPRParameterResponseDto;

import java.util.List;
import java.util.UUID;

public interface EPRParameterService {

    EPRParameterResponseDto createEPRParameter(EPRParameterDto dto);

    EPRParameterResponseDto updateEPRParameter(UUID id, EPRParameterDto dto);

    EPRParameterResponseDto getEPRParameterById(UUID id);

    List<EPRParameterResponseDto> getAllEPRParameters();

    void deleteEPRParameter(UUID id);

    EPRParameterResponseDto getLatestActive();

    void disable(UUID id);
}

