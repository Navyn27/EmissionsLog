package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.service;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.dtos.AddingStrawParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.dtos.AddingStrawParameterResponseDto;

import java.util.List;
import java.util.UUID;

public interface AddingStrawParameterService {

    AddingStrawParameterResponseDto create(AddingStrawParameterDto dto);

    AddingStrawParameterResponseDto getById(UUID id);

    List<AddingStrawParameterResponseDto> getAll();

    AddingStrawParameterResponseDto update(UUID id, AddingStrawParameterDto dto);

    void disable(UUID id);

    AddingStrawParameterResponseDto getLatestActive();
}

