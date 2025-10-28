package com.navyn.emissionlog.modules.mitigationProjects.improvedMMS.repositories;

import com.navyn.emissionlog.modules.mitigationProjects.improvedMMS.models.ImprovedMMSMitigation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ImprovedMMSMitigationRepository 
        extends JpaRepository<ImprovedMMSMitigation, UUID>,
                JpaSpecificationExecutor<ImprovedMMSMitigation> {
    
    Optional<ImprovedMMSMitigation> findByYear(Integer year);
}
