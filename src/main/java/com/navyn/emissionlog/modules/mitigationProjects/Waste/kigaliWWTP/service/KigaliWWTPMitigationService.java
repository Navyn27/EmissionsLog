package com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.service;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.constants.WWTPProjectPhase;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.dtos.KigaliWWTPMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.models.KigaliWWTPMitigation;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface KigaliWWTPMitigationService {
    
    KigaliWWTPMitigation createKigaliWWTPMitigation(KigaliWWTPMitigationDto dto);
    
    KigaliWWTPMitigation updateKigaliWWTPMitigation(UUID id, KigaliWWTPMitigationDto dto);
    
    void deleteKigaliWWTPMitigation(UUID id);
    
    List<KigaliWWTPMitigation> getAllKigaliWWTPMitigation(Integer year, WWTPProjectPhase projectPhase);

    byte[] generateExcelTemplate();

    Map<String, Object> createKigaliWWTPMitigationFromExcel(MultipartFile file);
}
