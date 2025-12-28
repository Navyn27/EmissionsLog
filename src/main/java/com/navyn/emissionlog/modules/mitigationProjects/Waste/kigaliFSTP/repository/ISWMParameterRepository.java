package com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.repository;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.models.ISWMParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ISWMParameterRepository extends JpaRepository<ISWMParameter, UUID> {

    /**
     * Finds the latest active ISWMParameter ordered by creation date (most recent first)
     * @return Optional containing the latest active parameter, or empty if none found
     */
    Optional<ISWMParameter> findFirstByIsActiveTrueOrderByCreatedAtDesc();
}

