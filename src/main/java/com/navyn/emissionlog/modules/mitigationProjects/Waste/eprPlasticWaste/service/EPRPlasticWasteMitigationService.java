package com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.service;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.dtos.EPRPlasticWasteMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.models.EPRPlasticWasteMitigation;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface EPRPlasticWasteMitigationService {
    
    EPRPlasticWasteMitigation createEPRPlasticWasteMitigation(EPRPlasticWasteMitigationDto dto);
    
    EPRPlasticWasteMitigation updateEPRPlasticWasteMitigation(UUID id, EPRPlasticWasteMitigationDto dto);
    
    void deleteEPRPlasticWasteMitigation(UUID id);
    
    List<EPRPlasticWasteMitigation> getAllEPRPlasticWasteMitigation(Integer year);

    byte[] generateExcelTemplate();

    Map<String, Object> createEPRPlasticWasteMitigationFromExcel(MultipartFile file);
}
