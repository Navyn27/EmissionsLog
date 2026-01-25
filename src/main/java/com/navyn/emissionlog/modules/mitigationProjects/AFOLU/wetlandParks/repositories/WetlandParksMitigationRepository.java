package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.repositories;

import com.navyn.emissionlog.Enums.Mitigation.WetlandTreeCategory;
import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.wetlandParks.models.WetlandParksMitigation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WetlandParksMitigationRepository 
        extends JpaRepository<WetlandParksMitigation, UUID>,
                JpaSpecificationExecutor<WetlandParksMitigation> {
    
    // Find the most recent record BEFORE the given year for a specific tree category
    Optional<WetlandParksMitigation> findTopByYearLessThanAndTreeCategoryOrderByYearDesc(
        Integer year, 
        WetlandTreeCategory treeCategory
    );
    
    // Find subsequent years for cascading updates
    List<WetlandParksMitigation> findByYearGreaterThanAndTreeCategoryOrderByYearAsc(
        Integer year, 
        WetlandTreeCategory treeCategory
    );
    
    // Find by exact year and tree category
    Optional<WetlandParksMitigation> findByYearAndTreeCategory(
        Integer year,
        WetlandTreeCategory treeCategory
    );

    // Find by year range for dashboard filtering
    List<WetlandParksMitigation> findByYearBetweenOrderByYearDesc(Integer startYear, Integer endYear);

    default List<WetlandParksMitigation> findByYearRange(Integer startYear, Integer endYear) {
        return findByYearBetweenOrderByYearDesc(startYear, endYear);
    }
}
