package com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.repository;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.models.ISWMMitigationParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ISWMMitigationParameterRepository extends JpaRepository<ISWMMitigationParameter, UUID> {
    
    /**
     * Finds the latest active ISWMMitigationParameter ordered by creation date (most recent first)
     */
    Optional<ISWMMitigationParameter> findFirstByIsActiveTrueOrderByCreatedAtDesc();
}

