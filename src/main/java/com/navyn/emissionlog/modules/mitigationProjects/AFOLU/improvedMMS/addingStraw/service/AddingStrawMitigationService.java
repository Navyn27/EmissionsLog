package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.service;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.dtos.AddingStrawMitigationDto;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.models.AddingStrawMitigation;

import java.util.List;

public interface AddingStrawMitigationService {
    
    AddingStrawMitigation createAddingStrawMitigation(AddingStrawMitigationDto dto);
    
    List<AddingStrawMitigation> getAllAddingStrawMitigation(Integer year);
}
