package com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.repository;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.models.EPRPlasticWasteMitigation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EPRPlasticWasteMitigationRepository extends JpaRepository<EPRPlasticWasteMitigation, Long>, 
        JpaSpecificationExecutor<EPRPlasticWasteMitigation> {
    
    Optional<EPRPlasticWasteMitigation> findByYear(Integer year);
    
    @Query("SELECT e FROM EPRPlasticWasteMitigation e WHERE e.year < :year ORDER BY e.year DESC LIMIT 1")
    Optional<EPRPlasticWasteMitigation> findPreviousYear(Integer year);
}
