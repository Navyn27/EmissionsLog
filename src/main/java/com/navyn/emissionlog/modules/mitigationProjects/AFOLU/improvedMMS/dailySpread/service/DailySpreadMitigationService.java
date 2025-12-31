package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.dailySpread.service;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.dailySpread.dtos.DailySpreadMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.dailySpread.dtos.DailySpreadMitigationResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface DailySpreadMitigationService {

    DailySpreadMitigationResponseDto createDailySpreadMitigation(DailySpreadMitigationDto dto);

    DailySpreadMitigationResponseDto updateDailySpreadMitigation(UUID id, DailySpreadMitigationDto dto);

    void deleteDailySpreadMitigation(UUID id);

    List<DailySpreadMitigationResponseDto> getAllDailySpreadMitigation(Integer year);

    byte[] generateExcelTemplate();

    Map<String, Object> createDailySpreadMitigationFromExcel(MultipartFile file);
}
