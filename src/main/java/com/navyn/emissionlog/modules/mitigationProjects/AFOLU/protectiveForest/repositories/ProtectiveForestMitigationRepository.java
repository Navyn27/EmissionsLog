package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.protectiveForest.repositories;

import com.navyn.emissionlog.Enums.Mitigation.ProtectiveForestCategory;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.protectiveForest.models.ProtectiveForestMitigation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProtectiveForestMitigationRepository 
        extends JpaRepository<ProtectiveForestMitigation, UUID>,
                JpaSpecificationExecutor<ProtectiveForestMitigation> {
    
    // Find the most recent record BEFORE the given year for a specific category
    Optional<ProtectiveForestMitigation> findTopByYearLessThanAndCategoryOrderByYearDesc(
        Integer year,
        ProtectiveForestCategory category
    );
    
    List<ProtectiveForestMitigation> findByYearGreaterThanAndCategoryOrderByYearAsc(
        Integer year,
        ProtectiveForestCategory category
    );
    
    // Find by exact year and category
    Optional<ProtectiveForestMitigation> findByYearAndCategory(
        Integer year,
        ProtectiveForestCategory category
    );
}
