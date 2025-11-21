package com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.repository;

import com.navyn.emissionlog.modules.mitigationProjects.AFOLU.improvedMMS.addingStraw.models.AddingStrawMitigation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddingStrawMitigationRepository extends JpaRepository<AddingStrawMitigation, Long>, 
        JpaSpecificationExecutor<AddingStrawMitigation> {
    
    Optional<AddingStrawMitigation> findByYear(Integer year);
}
