package com.navyn.emissionlog.modules.transportScenarios.ElectricVehicle.repository;

import com.navyn.emissionlog.modules.transportScenarios.ElectricVehicle.models.ElectricVehicleMitigation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ElectricVehicleMitigationRepository extends JpaRepository<ElectricVehicleMitigation, UUID>, 
        JpaSpecificationExecutor<ElectricVehicleMitigation> {
}

