package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.dailySpread.service;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.dailySpread.dtos.DailySpreadParameterDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.dailySpread.dtos.DailySpreadParameterResponseDto;

import java.util.List;
import java.util.UUID;

public interface DailySpreadParameterService {
    DailySpreadParameterResponseDto create(DailySpreadParameterDto dto);
    DailySpreadParameterResponseDto getById(UUID id);
    List<DailySpreadParameterResponseDto> getAll();
    DailySpreadParameterResponseDto update(UUID id, DailySpreadParameterDto dto);
    void disable(UUID id);
    DailySpreadParameterResponseDto getLatestActive();
}

