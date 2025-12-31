package com.navyn.emissionlog.modules.mitigationProjects.energy.rooftop.repository;

import com.navyn.emissionlog.modules.mitigationProjects.energy.rooftop.model.RoofTopParameter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IRoofTopParameterRepository extends JpaRepository<RoofTopParameter, UUID> {
    
    Optional<RoofTopParameter> findFirstByOrderByCreatedAtDesc();

    /**
     * Finds the latest active RoofTopParameter ordered by creation date (most recent first)
     * @return Optional containing the latest active parameter, or empty if none found
     */
    Optional<RoofTopParameter> findFirstByIsActiveTrueOrderByCreatedAtDesc();
}
