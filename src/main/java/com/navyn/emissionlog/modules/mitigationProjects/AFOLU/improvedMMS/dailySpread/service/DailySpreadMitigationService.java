package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.dailySpread.service;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.dailySpread.dtos.DailySpreadMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.dailySpread.models.DailySpreadMitigation;

import java.util.List;

public interface DailySpreadMitigationService {
    
    DailySpreadMitigation createDailySpreadMitigation(DailySpreadMitigationDto dto);
    
    List<DailySpreadMitigation> getAllDailySpreadMitigation(Integer year);
}
