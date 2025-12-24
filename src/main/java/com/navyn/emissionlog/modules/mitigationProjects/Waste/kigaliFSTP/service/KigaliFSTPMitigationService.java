package com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.service;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.dtos.KigaliFSTPMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.models.KigaliFSTPMitigation;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface KigaliFSTPMitigationService {
    
    KigaliFSTPMitigation createKigaliFSTPMitigation(KigaliFSTPMitigationDto dto);
    
    KigaliFSTPMitigation updateKigaliFSTPMitigation(UUID id, KigaliFSTPMitigationDto dto);
    
    void deleteKigaliFSTPMitigation(UUID id);
    
    List<KigaliFSTPMitigation> getAllKigaliFSTPMitigation(Integer year);

    byte[] generateExcelTemplate();

    Map<String, Object> createKigaliFSTPMitigationFromExcel(MultipartFile file);
}
