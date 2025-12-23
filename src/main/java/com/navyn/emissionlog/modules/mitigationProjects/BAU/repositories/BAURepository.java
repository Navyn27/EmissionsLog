package com.navyn.emissionlog.modules.mitigationProjects.BAU.repositories;

import com.navyn.emissionlog.modules.mitigationProjects.BAU.enums.ESector;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.models.BAU;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BAURepository extends JpaRepository<BAU, UUID>, JpaSpecificationExecutor<BAU> {
    
    Optional<BAU> findByYearAndSector(Integer year, ESector sector);
    
    List<BAU> findByYearOrderBySectorAsc(Integer year);
    
    List<BAU> findBySectorOrderByYearAsc(ESector sector);
}

