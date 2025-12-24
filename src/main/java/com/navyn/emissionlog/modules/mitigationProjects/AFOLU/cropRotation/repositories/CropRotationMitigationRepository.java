package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.repositories;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.cropRotation.models.CropRotationMitigation;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CropRotationMitigationRepository 
        extends JpaRepository<CropRotationMitigation, UUID>,
                JpaSpecificationExecutor<CropRotationMitigation> {
    
    Optional<CropRotationMitigation> findByYear(Integer year);
    
    /**
     * Find all CropRotationMitigation records with eagerly fetched intervention using JOIN FETCH
     * This prevents lazy loading issues when accessing intervention data
     * The EntityGraph annotation will ensure intervention is loaded eagerly
     */
    @EntityGraph(attributePaths = {"intervention"})
    @Override
    List<CropRotationMitigation> findAll(Specification<CropRotationMitigation> spec, Sort sort);
}
