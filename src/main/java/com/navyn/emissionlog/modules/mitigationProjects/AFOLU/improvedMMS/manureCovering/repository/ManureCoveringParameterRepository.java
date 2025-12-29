package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.repository;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.models.ManureCoveringParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ManureCoveringParameterRepository extends JpaRepository<ManureCoveringParameter, UUID> {
    Optional<ManureCoveringParameter> findFirstByIsActiveTrueOrderByCreatedAtDesc();
}

