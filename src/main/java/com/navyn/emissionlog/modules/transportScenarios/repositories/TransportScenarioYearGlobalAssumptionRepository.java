package com.navyn.emissionlog.modules.transportScenarios.repositories;

import com.navyn.emissionlog.modules.transportScenarios.models.TransportScenarioYearGlobalAssumption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransportScenarioYearGlobalAssumptionRepository 
        extends JpaRepository<TransportScenarioYearGlobalAssumption, UUID>, 
                JpaSpecificationExecutor<TransportScenarioYearGlobalAssumption> {
    
    List<TransportScenarioYearGlobalAssumption> findByScenarioId(UUID scenarioId);
    
    Optional<TransportScenarioYearGlobalAssumption> findByScenarioIdAndYear(UUID scenarioId, Integer year);
}
