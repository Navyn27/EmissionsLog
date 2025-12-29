package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.protectiveForest.service;

import com.navyn.emissionlog.Enums.Mitigation.ProtectiveForestCategory;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.protectiveForest.dtos.ProtectiveForestMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.protectiveForest.dtos.ProtectiveForestMitigationResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface ProtectiveForestMitigationService {

    ProtectiveForestMitigationResponseDto createProtectiveForestMitigation(ProtectiveForestMitigationDto dto);

    ProtectiveForestMitigationResponseDto updateProtectiveForestMitigation(UUID id, ProtectiveForestMitigationDto dto);

    void deleteProtectiveForestMitigation(UUID id);

    List<ProtectiveForestMitigationResponseDto> getAllProtectiveForestMitigation(
        Integer year,
        ProtectiveForestCategory category
    );

    Optional<ProtectiveForestMitigationResponseDto> getByYearAndCategory(
        Integer year,
        ProtectiveForestCategory category
    );

    byte[] generateExcelTemplate();

    Map<String, Object> createProtectiveForestMitigationFromExcel(MultipartFile file);
}
