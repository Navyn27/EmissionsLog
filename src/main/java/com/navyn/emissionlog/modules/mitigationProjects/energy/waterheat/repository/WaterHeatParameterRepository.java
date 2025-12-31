package com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.repository;

import com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.models.WaterHeatParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WaterHeatParameterRepository extends JpaRepository<WaterHeatParameter, UUID> {

    /**
     * Finds the latest WaterHeatParameter ordered by creation date (most recent first)
     * @return Optional containing the latest parameter, or empty if none found
     */
    Optional<WaterHeatParameter> findFirstByOrderByCreatedAtDesc();

    /**
     * Finds the latest active WaterHeatParameter ordered by creation date (most recent first)
     * @return Optional containing the latest active parameter, or empty if none found
     */
    Optional<WaterHeatParameter> findFirstByIsActiveTrueOrderByCreatedAtDesc();
}