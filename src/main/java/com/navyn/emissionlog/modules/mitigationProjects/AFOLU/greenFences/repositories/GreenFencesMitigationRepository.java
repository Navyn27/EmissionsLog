package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.greenFences.repositories;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.greenFences.models.GreenFencesMitigation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GreenFencesMitigationRepository 
        extends JpaRepository<GreenFencesMitigation, UUID>,
                JpaSpecificationExecutor<GreenFencesMitigation> {
    
    Optional<GreenFencesMitigation> findByYear(Integer year);
}
