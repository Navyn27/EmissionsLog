package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.streetTrees.service;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.streetTrees.dtos.StreetTreesMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.streetTrees.dtos.StreetTreesMitigationResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface StreetTreesMitigationService {

    StreetTreesMitigationResponseDto createStreetTreesMitigation(StreetTreesMitigationDto dto);

    StreetTreesMitigationResponseDto updateStreetTreesMitigation(UUID id, StreetTreesMitigationDto dto);

    void deleteStreetTreesMitigation(UUID id);

    List<StreetTreesMitigationResponseDto> getAllStreetTreesMitigation(Integer year);

    Optional<StreetTreesMitigationResponseDto> getByYear(Integer year);

    byte[] generateExcelTemplate();

    Map<String, Object> createStreetTreesMitigationFromExcel(MultipartFile file);
}
