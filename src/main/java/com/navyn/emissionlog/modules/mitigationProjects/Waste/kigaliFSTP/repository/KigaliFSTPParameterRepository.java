package com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.repository;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.models.KigaliFSTPParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface KigaliFSTPParameterRepository extends JpaRepository<KigaliFSTPParameter, UUID> {

    /**
     * Finds the latest active KigaliFSTPParameter ordered by creation date (most recent first)
     * @return Optional containing the latest active parameter, or empty if none found
     */
    Optional<KigaliFSTPParameter> findFirstByIsActiveTrueOrderByCreatedAtDesc();
}

