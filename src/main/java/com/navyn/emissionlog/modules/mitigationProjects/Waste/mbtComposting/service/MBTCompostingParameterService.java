package com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.service;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.dtos.MBTCompostingParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.dtos.MBTCompostingParameterResponseDto;

import java.util.List;
import java.util.UUID;

public interface MBTCompostingParameterService {

    MBTCompostingParameterResponseDto createMBTCompostingParameter(MBTCompostingParameterDto dto);

    MBTCompostingParameterResponseDto updateMBTCompostingParameter(UUID id, MBTCompostingParameterDto dto);

    MBTCompostingParameterResponseDto getMBTCompostingParameterById(UUID id);

    List<MBTCompostingParameterResponseDto> getAllMBTCompostingParameters();

    void deleteMBTCompostingParameter(UUID id);

    MBTCompostingParameterResponseDto getLatestActive();

    void disable(UUID id);
}

