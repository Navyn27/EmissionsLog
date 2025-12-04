package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.dailySpread.service;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.dailySpread.dtos.DailySpreadMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.dailySpread.models.DailySpreadMitigation;

import java.util.List;
import java.util.UUID;

public interface DailySpreadMitigationService {

    DailySpreadMitigation createDailySpreadMitigation(DailySpreadMitigationDto dto);

    DailySpreadMitigation updateDailySpreadMitigation(UUID id, DailySpreadMitigationDto dto);

    void deleteDailySpreadMitigation(UUID id);

    List<DailySpreadMitigation> getAllDailySpreadMitigation(Integer year);
}
