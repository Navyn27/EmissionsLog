package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.protectiveForest.service;

import com.navyn.emissionlog.Enums.Mitigation.ProtectiveForestCategory;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.protectiveForest.dtos.ProtectiveForestMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.protectiveForest.models.ProtectiveForestMitigation;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface ProtectiveForestMitigationService {

    ProtectiveForestMitigation createProtectiveForestMitigation(ProtectiveForestMitigationDto dto);

    ProtectiveForestMitigation updateProtectiveForestMitigation(UUID id, ProtectiveForestMitigationDto dto);

    void deleteProtectiveForestMitigation(UUID id);

    List<ProtectiveForestMitigation> getAllProtectiveForestMitigation(
        Integer year,
        ProtectiveForestCategory category
    );

    Optional<ProtectiveForestMitigation> getByYearAndCategory(
        Integer year,
        ProtectiveForestCategory category
    );

    byte[] generateExcelTemplate();

    Map<String, Object> createProtectiveForestMitigationFromExcel(MultipartFile file);
}
