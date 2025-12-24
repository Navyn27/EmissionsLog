package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.zeroTillage.repositories;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.zeroTillage.models.ZeroTillageParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ZeroTillageParameterRepository extends JpaRepository<ZeroTillageParameter, UUID> {
    
    /**
     * Finds the latest active ZeroTillageParameter ordered by creation date (most recent first)
     * @return Optional containing the latest active parameter, or empty if none found
     */
    Optional<ZeroTillageParameter> findFirstByIsActiveTrueOrderByCreatedAtDesc();
}

