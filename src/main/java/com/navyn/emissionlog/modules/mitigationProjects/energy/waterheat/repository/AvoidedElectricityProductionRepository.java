package com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.repository;

import com.navyn.emissionlog.modules.mitigationProjects.energy.waterheat.models.AvoidedElectricityProduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AvoidedElectricityProductionRepository extends JpaRepository<AvoidedElectricityProduction, UUID> {
    List<AvoidedElectricityProduction> findByYear(int year);
}
