package com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.repository;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.iswm.models.ISWMMitigation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ISWMMitigationRepository extends JpaRepository<ISWMMitigation, Long>, 
        JpaSpecificationExecutor<ISWMMitigation> {
    
    Optional<ISWMMitigation> findByYear(Integer year);
}
