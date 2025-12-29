package com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.repository;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.models.EPRParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EPRParameterRepository extends JpaRepository<EPRParameter, UUID> {

    Optional<EPRParameter> findFirstByOrderByCreatedAtDesc();

    /**
     * Finds the latest active EPRParameter ordered by creation date (most recent first)
     * @return Optional containing the latest active parameter, or empty if none found
     */
    Optional<EPRParameter> findFirstByIsActiveTrueOrderByCreatedAtDesc();
}

