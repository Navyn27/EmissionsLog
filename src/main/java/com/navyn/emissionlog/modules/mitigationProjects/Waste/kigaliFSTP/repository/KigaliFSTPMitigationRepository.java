package com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.repository;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliFSTP.models.KigaliFSTPMitigation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KigaliFSTPMitigationRepository extends JpaRepository<KigaliFSTPMitigation, Long>, 
        JpaSpecificationExecutor<KigaliFSTPMitigation> {
    
    Optional<KigaliFSTPMitigation> findByYear(Integer year);
}
