package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.streetTrees.repositories;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.streetTrees.models.StreetTreesParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StreetTreesParameterRepository extends JpaRepository<StreetTreesParameter, UUID> {
    
    /**
     * Finds the latest active StreetTreesParameter ordered by creation date (most recent first)
     * @return Optional containing the latest active parameter, or empty if none found
     */
    Optional<StreetTreesParameter> findFirstByIsActiveTrueOrderByCreatedAtDesc();
}

