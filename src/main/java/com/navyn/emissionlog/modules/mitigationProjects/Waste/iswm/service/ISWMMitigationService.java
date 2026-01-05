package com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.service;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.dtos.ISWMMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.dtos.ISWMMitigationResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ISWMMitigationService {
    
    ISWMMitigationResponseDto createISWMMitigation(ISWMMitigationDto dto);
    
    ISWMMitigationResponseDto updateISWMMitigation(UUID id, ISWMMitigationDto dto);
    
    void deleteISWMMitigation(UUID id);
    
    List<ISWMMitigationResponseDto> getAllISWMMitigation(Integer year);

    byte[] generateExcelTemplate();

    Map<String, Object> createISWMMitigationFromExcel(MultipartFile file);
}
