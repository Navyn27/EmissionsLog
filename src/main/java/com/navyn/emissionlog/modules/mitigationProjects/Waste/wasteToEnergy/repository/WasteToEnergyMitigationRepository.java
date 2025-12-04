package com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.repository;

import com.navyn.emissionlog.modules.mitigationProjects.Waste.wasteToEnergy.models.WasteToEnergyMitigation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WasteToEnergyMitigationRepository extends JpaRepository<WasteToEnergyMitigation, UUID>, 
        JpaSpecificationExecutor<WasteToEnergyMitigation> {
    
    Optional<WasteToEnergyMitigation> findByYear(Integer year);
}
