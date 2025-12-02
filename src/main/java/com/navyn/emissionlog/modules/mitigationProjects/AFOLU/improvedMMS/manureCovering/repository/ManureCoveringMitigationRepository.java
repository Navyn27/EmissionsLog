package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.repository;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.models.ManureCoveringMitigation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ManureCoveringMitigationRepository extends JpaRepository<ManureCoveringMitigation, UUID>, 
        JpaSpecificationExecutor<ManureCoveringMitigation> {
    
    Optional<ManureCoveringMitigation> findByYear(Integer year);
}
