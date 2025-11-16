package com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.repository;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.mbtComposting.models.MBTCompostingMitigation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MBTCompostingMitigationRepository extends JpaRepository<MBTCompostingMitigation, Long>, 
        JpaSpecificationExecutor<MBTCompostingMitigation> {
    
    Optional<MBTCompostingMitigation> findByYear(Integer year);
}
