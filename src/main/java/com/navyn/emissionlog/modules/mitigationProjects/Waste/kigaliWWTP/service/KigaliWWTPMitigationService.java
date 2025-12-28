package com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.service;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.dtos.KigaliWWTPMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.dtos.KigaliWWTPMitigationResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface KigaliWWTPMitigationService {
    
    KigaliWWTPMitigationResponseDto createKigaliWWTPMitigation(KigaliWWTPMitigationDto dto);
    
    KigaliWWTPMitigationResponseDto updateKigaliWWTPMitigation(UUID id, KigaliWWTPMitigationDto dto);
    
    void deleteKigaliWWTPMitigation(UUID id);
    
    List<KigaliWWTPMitigationResponseDto> getAllKigaliWWTPMitigation(Integer year);

    byte[] generateExcelTemplate();

    Map<String, Object> createKigaliWWTPMitigationFromExcel(MultipartFile file);
}
