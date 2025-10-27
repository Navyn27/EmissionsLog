package com.navyn.emissionlog.modules.mitigationProjects.wetlandParks.repositories;

import com.navyn.emissionlog.Enums.Mitigation.WetlandTreeCategory;
import com.navyn.emissionlog.modules.mitigationProjects.wetlandParks.models.WetlandParksMitigation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WetlandParksMitigationRepository 
        extends JpaRepository<WetlandParksMitigation, UUID>,
                JpaSpecificationExecutor<WetlandParksMitigation> {
    
    Optional<WetlandParksMitigation> findByYearAndTreeCategory(
        Integer year, 
        WetlandTreeCategory treeCategory
    );
}
