package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.streetTrees.service;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.streetTrees.dtos.StreetTreesMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.streetTrees.models.StreetTreesMitigation;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface StreetTreesMitigationService {

    StreetTreesMitigation createStreetTreesMitigation(StreetTreesMitigationDto dto);

    StreetTreesMitigation updateStreetTreesMitigation(UUID id, StreetTreesMitigationDto dto);

    void deleteStreetTreesMitigation(UUID id);

    List<StreetTreesMitigation> getAllStreetTreesMitigation(Integer year);

    Optional<StreetTreesMitigation> getByYear(Integer year);

    byte[] generateExcelTemplate();

    Map<String, Object> createStreetTreesMitigationFromExcel(MultipartFile file);
}
