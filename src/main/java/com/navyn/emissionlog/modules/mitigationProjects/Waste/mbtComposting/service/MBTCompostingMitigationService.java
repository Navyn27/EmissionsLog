package com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.service;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.dtos.MBTCompostingMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.dtos.MBTCompostingMitigationResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface MBTCompostingMitigationService {
    
    MBTCompostingMitigationResponseDto createMBTCompostingMitigation(MBTCompostingMitigationDto dto);
    
    MBTCompostingMitigationResponseDto updateMBTCompostingMitigation(UUID id, MBTCompostingMitigationDto dto);
    
    void deleteMBTCompostingMitigation(UUID id);
    
    List<MBTCompostingMitigationResponseDto> getAllMBTCompostingMitigation(Integer year);

    byte[] generateExcelTemplate();

    Map<String, Object> createMBTCompostingMitigationFromExcel(MultipartFile file);
}
