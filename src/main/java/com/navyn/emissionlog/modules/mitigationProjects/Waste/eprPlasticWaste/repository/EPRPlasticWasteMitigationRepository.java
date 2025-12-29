package com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.repository;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.eprPlasticWaste.models.EPRPlasticWasteMitigation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EPRPlasticWasteMitigationRepository extends JpaRepository<EPRPlasticWasteMitigation, UUID>, 
        JpaSpecificationExecutor<EPRPlasticWasteMitigation> {
    
    Optional<EPRPlasticWasteMitigation> findByYear(Integer year);
}
