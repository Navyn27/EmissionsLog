package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.repositories;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.models.WetlandParksParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WetlandParksParameterRepository extends JpaRepository<WetlandParksParameter, UUID> {

    /**
     * Finds the latest active WetlandParksParameter ordered by creation date
     * (most recent first)
     * 
     * @return Optional containing the latest active parameter, or empty if none
     *         found
     */
    Optional<WetlandParksParameter> findFirstByIsActiveTrueOrderByCreatedAtDesc();
}

