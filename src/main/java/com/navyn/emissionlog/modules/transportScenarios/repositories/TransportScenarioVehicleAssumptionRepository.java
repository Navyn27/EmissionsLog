package com.navyn.emissionlog.modules.transportScenarios.repositories;

import com.navyn.emissionlog.modules.transportScenarios.enums.TransportScenarioVehicleCategory;
import com.navyn.emissionlog.modules.transportScenarios.models.TransportScenarioVehicleAssumption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransportScenarioVehicleAssumptionRepository 
        extends JpaRepository<TransportScenarioVehicleAssumption, UUID>, 
                JpaSpecificationExecutor<TransportScenarioVehicleAssumption> {
    
    List<TransportScenarioVehicleAssumption> findByScenarioId(UUID scenarioId);
    
    List<TransportScenarioVehicleAssumption> findByScenarioIdAndYear(UUID scenarioId, Integer year);
    
    Optional<TransportScenarioVehicleAssumption> findByScenarioIdAndYearAndVehicleCategory(
            UUID scenarioId, Integer year, TransportScenarioVehicleCategory vehicleCategory);
}
