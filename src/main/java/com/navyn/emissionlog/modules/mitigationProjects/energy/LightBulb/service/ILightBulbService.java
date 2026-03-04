package com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.service;

import com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.dto.CreateLightBulbDTO;
import com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.dto.LightBulbMitigationResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.dto.UpdateLightBulbDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ILightBulbService {
    LightBulbMitigationResponseDto create(CreateLightBulbDTO lightBulbDTO);
    List<LightBulbMitigationResponseDto> getAll();
    LightBulbMitigationResponseDto getById(UUID id);
    LightBulbMitigationResponseDto update(UUID id, UpdateLightBulbDTO lightBulbDTO);
    void delete(UUID id);

    List<LightBulbMitigationResponseDto> getByYear(int year);
    
    byte[] generateExcelTemplate();
    Map<String, Object> createLightBulbMitigationFromExcel(MultipartFile file);
}
