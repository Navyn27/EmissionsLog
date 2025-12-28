package com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.repository;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.models.KigaliWWTPParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface KigaliWWTPParameterRepository extends JpaRepository<KigaliWWTPParameter, UUID> {

    /**
     * Finds the latest active KigaliWWTPParameter ordered by creation date (most recent first)
     * @return Optional containing the latest active parameter, or empty if none found
     */
    Optional<KigaliWWTPParameter> findFirstByIsActiveTrueOrderByCreatedAtDesc();
}

