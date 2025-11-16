package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.protectiveForest.repositories;

import com.navyn.emissionlog.Enums.Mitigation.ProtectiveForestCategory;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.protectiveForest.models.ProtectiveForestMitigation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProtectiveForestMitigationRepository 
        extends JpaRepository<ProtectiveForestMitigation, UUID>,
                JpaSpecificationExecutor<ProtectiveForestMitigation> {
    
    Optional<ProtectiveForestMitigation> findByYearAndCategory(
        Integer year, 
        ProtectiveForestCategory category
    );
}
