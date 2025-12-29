package com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.service;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.dtos.KigaliWWTPParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.dtos.KigaliWWTPParameterResponseDto;

import java.util.List;
import java.util.UUID;

public interface KigaliWWTPParameterService {

    KigaliWWTPParameterResponseDto createKigaliWWTPParameter(KigaliWWTPParameterDto dto);

    KigaliWWTPParameterResponseDto updateKigaliWWTPParameter(UUID id, KigaliWWTPParameterDto dto);

    KigaliWWTPParameterResponseDto getKigaliWWTPParameterById(UUID id);

    List<KigaliWWTPParameterResponseDto> getAllKigaliWWTPParameters();

    void deleteKigaliWWTPParameter(UUID id);

    KigaliWWTPParameterResponseDto getLatestActive();

    void disable(UUID id);
}

