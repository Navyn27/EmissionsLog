package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandsRewetting.service;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandsRewetting.dtos.WetlandsRewettingMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandsRewetting.dtos.WetlandsRewettingMitigationResponseDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WetlandsRewettingMitigationService {

    WetlandsRewettingMitigationResponseDto create(WetlandsRewettingMitigationDto dto);

    WetlandsRewettingMitigationResponseDto update(UUID id, WetlandsRewettingMitigationDto dto);

    void delete(UUID id);

    List<WetlandsRewettingMitigationResponseDto> getAll(Integer year);

    Optional<WetlandsRewettingMitigationResponseDto> getById(UUID id);

    byte[] generateExcelTemplate();

    java.util.Map<String, Object> createFromExcel(org.springframework.web.multipart.MultipartFile file);
}
