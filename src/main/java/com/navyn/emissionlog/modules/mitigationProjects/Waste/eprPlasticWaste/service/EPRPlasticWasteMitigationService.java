package com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.service;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.dtos.EPRPlasticWasteMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.dtos.EPRPlasticWasteMitigationResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface EPRPlasticWasteMitigationService {
    
    EPRPlasticWasteMitigationResponseDto createEPRPlasticWasteMitigation(EPRPlasticWasteMitigationDto dto);
    
    EPRPlasticWasteMitigationResponseDto updateEPRPlasticWasteMitigation(UUID id, EPRPlasticWasteMitigationDto dto);
    
    void deleteEPRPlasticWasteMitigation(UUID id);
    
    List<EPRPlasticWasteMitigationResponseDto> getAllEPRPlasticWasteMitigation(Integer year);

    byte[] generateExcelTemplate();

    Map<String, Object> createEPRPlasticWasteMitigationFromExcel(MultipartFile file);
}
