package com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.service;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.dtos.KigaliFSTPParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.dtos.KigaliFSTPParameterResponseDto;

import java.util.List;
import java.util.UUID;

public interface KigaliFSTPParameterService {

    KigaliFSTPParameterResponseDto createKigaliFSTPParameter(KigaliFSTPParameterDto dto);

    KigaliFSTPParameterResponseDto updateKigaliFSTPParameter(UUID id, KigaliFSTPParameterDto dto);

    KigaliFSTPParameterResponseDto getKigaliFSTPParameterById(UUID id);

    List<KigaliFSTPParameterResponseDto> getAllKigaliFSTPParameters();

    void deleteKigaliFSTPParameter(UUID id);

    KigaliFSTPParameterResponseDto getLatestActive();

    void disable(UUID id);
}

