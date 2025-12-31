package com.navyn.emissionlog.modules.transportScenarios.ElectricVehicle.repository;

import com.navyn.emissionlog.modules.transportScenarios.ElectricVehicle.models.ElectricVehicleParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ElectricVehicleParameterRepository extends JpaRepository<ElectricVehicleParameter, UUID> {

    /**
     * Finds the latest active ElectricVehicleParameter ordered by creation date (most recent first)
     * @return Optional containing the latest active parameter, or empty if none found
     */
    Optional<ElectricVehicleParameter> findFirstByIsActiveTrueOrderByCreatedAtDesc();
}

