package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.protectiveForest.repositories;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.protectiveForest.models.ProtectiveForestParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProtectiveForestParameterRepository extends JpaRepository<ProtectiveForestParameter, UUID> {

    /**
     * Finds the latest active ProtectiveForestParameter ordered by creation date
     * (most recent first)
     * 
     * @return Optional containing the latest active parameter, or empty if none
     *         found
     */
    Optional<ProtectiveForestParameter> findFirstByIsActiveTrueOrderByCreatedAtDesc();
}
