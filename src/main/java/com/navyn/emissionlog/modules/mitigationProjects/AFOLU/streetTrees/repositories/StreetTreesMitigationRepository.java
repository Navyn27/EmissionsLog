package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.streetTrees.repositories;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.streetTrees.models.StreetTreesMitigation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StreetTreesMitigationRepository 
        extends JpaRepository<StreetTreesMitigation, UUID>,
                JpaSpecificationExecutor<StreetTreesMitigation> {
    
    // Find the most recent record BEFORE the given year
    Optional<StreetTreesMitigation> findTopByYearLessThanOrderByYearDesc(Integer year);
    
    List<StreetTreesMitigation> findByYearGreaterThanOrderByYearAsc(Integer year);
    
    // Find by exact year
    Optional<StreetTreesMitigation> findByYear(Integer year);

    // Find by year range for dashboard filtering
    List<StreetTreesMitigation> findByYearBetweenOrderByYearDesc(Integer startYear, Integer endYear);

    default List<StreetTreesMitigation> findByYearRange(Integer startYear, Integer endYear) {
        return findByYearBetweenOrderByYearDesc(startYear, endYear);
    }
}
