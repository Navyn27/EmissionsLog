package com.navyn.emissionlog.modules.vehicles;

import com.navyn.emissionlog.modules.activities.models.VehicleData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VehicleDataRepository extends JpaRepository<VehicleData, UUID> {
}
