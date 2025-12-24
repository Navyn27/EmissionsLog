package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.dailySpread.service;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.dailySpread.dtos.DailySpreadMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.dailySpread.models.DailySpreadMitigation;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface DailySpreadMitigationService {

    DailySpreadMitigation createDailySpreadMitigation(DailySpreadMitigationDto dto);

    DailySpreadMitigation updateDailySpreadMitigation(UUID id, DailySpreadMitigationDto dto);

    void deleteDailySpreadMitigation(UUID id);

    List<DailySpreadMitigation> getAllDailySpreadMitigation(Integer year);

    byte[] generateExcelTemplate();

    Map<String, Object> createDailySpreadMitigationFromExcel(MultipartFile file);
}
