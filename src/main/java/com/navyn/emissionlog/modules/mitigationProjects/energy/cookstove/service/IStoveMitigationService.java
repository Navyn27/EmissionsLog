package com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.service;

import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.dto.CreateStoveMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.dto.StoveMitigationResponseDto;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.dto.UpdateStoveMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.enums.EStoveType;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IStoveMitigationService {
    StoveMitigationResponseDto createStoveMitigation(@Valid CreateStoveMitigationDto dto);
    StoveMitigationResponseDto updateStoveMitigation(UUID id, @Valid UpdateStoveMitigationDto dto);
    StoveMitigationResponseDto getStoveMitigationById(UUID id);
    List<StoveMitigationResponseDto> getAllStoveMitigations(Integer year, EStoveType stoveType);
    void deleteStoveMitigation(UUID id);
    
    byte[] generateExcelTemplate();
    Map<String, Object> createStoveMitigationFromExcel(MultipartFile file);
}

