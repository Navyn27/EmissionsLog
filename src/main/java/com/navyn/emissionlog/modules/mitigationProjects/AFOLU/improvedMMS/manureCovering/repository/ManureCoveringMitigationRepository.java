package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.repository;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.manureCovering.models.ManureCoveringMitigation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ManureCoveringMitigationRepository extends JpaRepository<ManureCoveringMitigation, Long>, 
        JpaSpecificationExecutor<ManureCoveringMitigation> {
    
    Optional<ManureCoveringMitigation> findByYear(Integer year);
}
