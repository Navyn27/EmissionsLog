package com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.service;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.dtos.MBTCompostingMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.models.MBTCompostingMitigation;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface MBTCompostingMitigationService {
    
    MBTCompostingMitigation createMBTCompostingMitigation(MBTCompostingMitigationDto dto);
    
    MBTCompostingMitigation updateMBTCompostingMitigation(UUID id, MBTCompostingMitigationDto dto);
    
    void deleteMBTCompostingMitigation(UUID id);
    
    List<MBTCompostingMitigation> getAllMBTCompostingMitigation(Integer year);

    byte[] generateExcelTemplate();

    Map<String, Object> createMBTCompostingMitigationFromExcel(MultipartFile file);
}
