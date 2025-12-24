package com.navyn.emissionlog.modules.transportScenarios.repositories;

import com.navyn.emissionlog.modules.transportScenarios.models.TransportScenario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransportScenarioRepository extends JpaRepository<TransportScenario, UUID>, JpaSpecificationExecutor<TransportScenario> {
    
    Optional<TransportScenario> findByName(String name);
}
