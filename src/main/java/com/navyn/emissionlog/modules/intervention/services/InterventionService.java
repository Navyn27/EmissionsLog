package com.navyn.emissionlog.modules.intervention.services;

import com.navyn.emissionlog.modules.intervention.Intervention;
import com.navyn.emissionlog.modules.intervention.dtos.InterventionDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InterventionService {
    
    Intervention createIntervention(InterventionDto dto);
    
    Intervention updateIntervention(UUID id, InterventionDto dto);
    
    Optional<Intervention> getInterventionById(UUID id);
    
    List<Intervention> getAllInterventions();
    
    void deleteIntervention(UUID id);
}

