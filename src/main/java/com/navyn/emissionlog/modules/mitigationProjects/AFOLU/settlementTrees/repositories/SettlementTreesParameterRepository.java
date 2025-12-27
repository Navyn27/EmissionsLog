package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.settlementTrees.repositories;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.settlementTrees.models.SettlementParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SettlementTreesParameterRepository extends JpaRepository<SettlementParameter, UUID> {

    /**
     * Finds the latest active SettlementTreesParameter ordered by creation date
     * (most recent first)
     * 
     * @return Optional containing the latest active parameter, or empty if none
     *         found
     */
    Optional<SettlementParameter> findFirstByIsActiveTrueOrderByCreatedAtDesc();
}
