package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.service;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.dtos.ManureCoveringMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.models.ManureCoveringMitigation;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ManureCoveringMitigationService {

    ManureCoveringMitigation createManureCoveringMitigation(ManureCoveringMitigationDto dto);

    ManureCoveringMitigation updateManureCoveringMitigation(UUID id, ManureCoveringMitigationDto dto);

    void deleteManureCoveringMitigation(UUID id);

    List<ManureCoveringMitigation> getAllManureCoveringMitigation(Integer year);

    byte[] generateExcelTemplate();

    Map<String, Object> createManureCoveringMitigationFromExcel(MultipartFile file);
}
