package com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.repository;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.models.ISWMMitigation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ISWMMitigationRepository extends JpaRepository<ISWMMitigation, UUID>, 
        JpaSpecificationExecutor<ISWMMitigation> {
    
    Optional<ISWMMitigation> findByYear(Integer year);
}
