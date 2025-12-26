package com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.repository;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.models.WasteToWtEParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WasteToWtEParameterRepository extends JpaRepository<WasteToWtEParameter, UUID> {

    Optional<WasteToWtEParameter> findFirstByOrderByCreatedAtDesc();

    /**
     * Finds the latest active WasteToWtEParameter ordered by creation date (most recent first)
     * @return Optional containing the latest active parameter, or empty if none found
     */
    Optional<WasteToWtEParameter> findFirstByIsActiveTrueOrderByCreatedAtDesc();
}

