package com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.service;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.dtos.KigaliFSTPMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.dtos.KigaliFSTPMitigationResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface KigaliFSTPMitigationService {
    
    KigaliFSTPMitigationResponseDto createKigaliFSTPMitigation(KigaliFSTPMitigationDto dto);
    
    KigaliFSTPMitigationResponseDto updateKigaliFSTPMitigation(UUID id, KigaliFSTPMitigationDto dto);
    
    void deleteKigaliFSTPMitigation(UUID id);
    
    List<KigaliFSTPMitigationResponseDto> getAllKigaliFSTPMitigation(Integer year);

    byte[] generateExcelTemplate();

    Map<String, Object> createKigaliFSTPMitigationFromExcel(MultipartFile file);
}
