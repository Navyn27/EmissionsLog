package com.navyn.emissionlog.modules.mitigationProjects.streetTrees.repositories;

import com.navyn.emissionlog.modules.mitigationProjects.streetTrees.models.StreetTreesMitigation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StreetTreesMitigationRepository 
        extends JpaRepository<StreetTreesMitigation, UUID>,
                JpaSpecificationExecutor<StreetTreesMitigation> {
    
    Optional<StreetTreesMitigation> findByYear(Integer year);
}
