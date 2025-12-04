package com.navyn.emissionlog.modules.mitigationProjects.Energy.cookstove.service;
import com.navyn.emissionlog.modules.mitigationProjects.Energy.cookstove.models.StoveType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StoveTypeService {
    StoveType save(StoveType stoveType);
    List<StoveType> findAll();
    Optional<StoveType> findById(UUID id);
    void deleteById(UUID id);
}
