package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.zeroTillage.repositories;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.zeroTillage.models.ZeroTillageMitigation;
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
public interface ZeroTillageMitigationRepository 
        extends JpaRepository<ZeroTillageMitigation, UUID>,
                JpaSpecificationExecutor<ZeroTillageMitigation> {
    
    Optional<ZeroTillageMitigation> findByYear(Integer year);
    
    /**
     * Find all ZeroTillageMitigation records with eagerly fetched intervention using JOIN FETCH
     * This prevents lazy loading issues when accessing intervention data
     * The EntityGraph annotation will ensure intervention is loaded eagerly
     */
    @EntityGraph(attributePaths = {"intervention"})
    @Override
    List<ZeroTillageMitigation> findAll(Specification<ZeroTillageMitigation> spec, Sort sort);
}
