package com.navyn.emissionlog.modules.mitigationProjects.energy.rooftop.service;

import com.navyn.emissionlog.modules.mitigationProjects.energy.rooftop.dto.RoofTopParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.energy.rooftop.dto.RoofTopParameterResponseDto;

import java.util.List;
import java.util.UUID;

public interface IRoofTopParameterService {
    RoofTopParameterResponseDto create(RoofTopParameterDto dto);
    RoofTopParameterResponseDto getById(UUID id);
    List<RoofTopParameterResponseDto> getAll();
    RoofTopParameterResponseDto update(UUID id, RoofTopParameterDto dto);
    void delete(UUID id);
    RoofTopParameterResponseDto getLatest();
}
