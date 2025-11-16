package com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.repository;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.kigaliWWTP.models.KigaliWWTPMitigation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KigaliWWTPMitigationRepository extends JpaRepository<KigaliWWTPMitigation, Long>, 
        JpaSpecificationExecutor<KigaliWWTPMitigation> {
    
    Optional<KigaliWWTPMitigation> findByYear(Integer year);
}
