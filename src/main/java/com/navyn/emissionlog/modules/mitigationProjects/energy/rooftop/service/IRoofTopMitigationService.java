package com.navyn.emissionlog.modules.mitigationProjects.Energy.rooftop.service;

import com.navyn.emissionlog.modules.mitigationProjects.Energy.rooftop.dto.RoofTopMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.Energy.rooftop.dto.RoofTopMitigationResponseDto;

import java.util.List;
import java.util.UUID;

public interface IRoofTopMitigationService {
    RoofTopMitigationResponseDto create(RoofTopMitigationDto dto);
    RoofTopMitigationResponseDto getById(UUID id);
    List<RoofTopMitigationResponseDto> getAll();
    RoofTopMitigationResponseDto update(UUID id, RoofTopMitigationDto dto);
    void delete(UUID id);
    RoofTopMitigationResponseDto getByYear(int year);
}
