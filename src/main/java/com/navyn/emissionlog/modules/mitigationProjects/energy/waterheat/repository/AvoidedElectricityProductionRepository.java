package com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.repository;

import com.navyn.emissionlog.modules.mitigationProjects.energy.rooftop.model.RoofTopMitigation;
import com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.models.AvoidedElectricityProduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AvoidedElectricityProductionRepository extends JpaRepository<AvoidedElectricityProduction, UUID> {
    // You can add custom queries if needed
    List<AvoidedElectricityProduction> findAllByYear(int year);

}
