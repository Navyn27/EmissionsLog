package com.navyn.emissionlog.modules.transportScenarios.repositories;

import com.navyn.emissionlog.modules.transportScenarios.models.TransportScenarioModalShiftAssumption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransportScenarioModalShiftAssumptionRepository 
        extends JpaRepository<TransportScenarioModalShiftAssumption, UUID>, 
                JpaSpecificationExecutor<TransportScenarioModalShiftAssumption> {
    
    List<TransportScenarioModalShiftAssumption> findByScenarioId(UUID scenarioId);
    
    Optional<TransportScenarioModalShiftAssumption> findByScenarioIdAndYear(UUID scenarioId, Integer year);
}
