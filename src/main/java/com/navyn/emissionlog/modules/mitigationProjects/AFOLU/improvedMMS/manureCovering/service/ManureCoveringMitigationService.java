package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.service;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.dtos.ManureCoveringMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.dtos.ManureCoveringMitigationResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ManureCoveringMitigationService {

    ManureCoveringMitigationResponseDto createManureCoveringMitigation(ManureCoveringMitigationDto dto);

    ManureCoveringMitigationResponseDto updateManureCoveringMitigation(UUID id, ManureCoveringMitigationDto dto);

    void deleteManureCoveringMitigation(UUID id);

    List<ManureCoveringMitigationResponseDto> getAllManureCoveringMitigation(Integer year);

    byte[] generateExcelTemplate();

    Map<String, Object> createManureCoveringMitigationFromExcel(MultipartFile file);
}
