package com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.repository;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.landfillGasUtilization.models.LandfillGasUtilizationMitigation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LandfillGasUtilizationMitigationRepository extends JpaRepository<LandfillGasUtilizationMitigation, Long>, 
        JpaSpecificationExecutor<LandfillGasUtilizationMitigation> {
    
    Optional<LandfillGasUtilizationMitigation> findByYear(Integer year);
}
