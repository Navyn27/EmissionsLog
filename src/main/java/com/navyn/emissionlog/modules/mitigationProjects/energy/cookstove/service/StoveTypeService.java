package com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.service;

import com.navyn.emissionlog.modules.mitigationProjects.energy.cookstove.models.StoveType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StoveTypeService {
    StoveType save(StoveType stoveType);

    List<StoveType> findAll();

    Optional<StoveType> findById(UUID id);

    void deleteById(UUID id);

    StoveType update(UUID id, StoveType stoveType);

    StoveType findOrCreateStoveType(String name, double baselinePercentage);

    Optional<StoveType> findByNameIgnoreCase(String name);
}
