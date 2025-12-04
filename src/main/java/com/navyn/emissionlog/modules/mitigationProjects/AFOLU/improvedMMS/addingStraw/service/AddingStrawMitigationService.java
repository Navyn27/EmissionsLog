package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.service;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.dtos.AddingStrawMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.models.AddingStrawMitigation;

import java.util.List;
import java.util.UUID;

public interface AddingStrawMitigationService {

    AddingStrawMitigation createAddingStrawMitigation(AddingStrawMitigationDto dto);

    AddingStrawMitigation updateAddingStrawMitigation(UUID id, AddingStrawMitigationDto dto);

    void deleteAddingStrawMitigation(UUID id);

    List<AddingStrawMitigation> getAllAddingStrawMitigation(Integer year);
}
