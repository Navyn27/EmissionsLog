package com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.repository;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.models.MBTCompostingParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MBTCompostingParameterRepository extends JpaRepository<MBTCompostingParameter, UUID> {

    Optional<MBTCompostingParameter> findFirstByOrderByCreatedAtDesc();

    /**
     * Finds the latest active MBTCompostingParameter ordered by creation date (most recent first)
     * @return Optional containing the latest active parameter, or empty if none found
     */
    Optional<MBTCompostingParameter> findFirstByIsActiveTrueOrderByCreatedAtDesc();
}

