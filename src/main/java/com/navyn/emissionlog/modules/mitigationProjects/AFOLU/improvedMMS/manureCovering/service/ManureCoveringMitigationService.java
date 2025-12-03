package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.service;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.dtos.ManureCoveringMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.models.ManureCoveringMitigation;

import java.util.List;
import java.util.UUID;

public interface ManureCoveringMitigationService {

    ManureCoveringMitigation createManureCoveringMitigation(ManureCoveringMitigationDto dto);

    ManureCoveringMitigation updateManureCoveringMitigation(UUID id, ManureCoveringMitigationDto dto);

    void deleteManureCoveringMitigation(UUID id);

    List<ManureCoveringMitigation> getAllManureCoveringMitigation(Integer year);
}
