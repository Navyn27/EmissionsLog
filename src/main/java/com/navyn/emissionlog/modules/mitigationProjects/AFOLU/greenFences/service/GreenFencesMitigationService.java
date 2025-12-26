package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.greenFences.service;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.greenFences.dtos.GreenFencesMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.greenFences.dtos.GreenFencesMitigationResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface GreenFencesMitigationService {
    
    GreenFencesMitigationResponseDto createGreenFencesMitigation(GreenFencesMitigationDto dto);
    
    GreenFencesMitigationResponseDto updateGreenFencesMitigation(UUID id, GreenFencesMitigationDto dto);

    void deleteGreenFencesMitigation(UUID id);
    
    List<GreenFencesMitigationResponseDto> getAllGreenFencesMitigation(Integer year);
    
    Optional<GreenFencesMitigationResponseDto> getByYear(Integer year);

    byte[] generateExcelTemplate();

    Map<String, Object> createGreenFencesMitigationFromExcel(MultipartFile file);
}
