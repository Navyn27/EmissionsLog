package com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.repository;

import com.navyn.emissionlog.modules.mitigationProjects.energy.LightBulb.model.LightBulbParameter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ILightBulbParameterRepository extends JpaRepository<LightBulbParameter, UUID> {
    Optional<LightBulbParameter> findFirstByOrderByCreatedAtDesc();

    /**
     * Finds the latest active Light Bulb Parameter ordered by creation date (most recent first)
     *
     * @return Optional containing the latest active parameter, or empty if none found
     */
    Optional<LightBulbParameter> findFirstByIsActiveTrueOrderByCreatedAtDesc();
}
