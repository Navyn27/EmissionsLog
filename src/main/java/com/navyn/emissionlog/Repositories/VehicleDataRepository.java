package com.navyn.emissionlog.Repositories;

import com.navyn.emissionlog.Models.ActivityData.VehicleData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VehicleDataRepository extends JpaRepository<VehicleData, UUID> {
}
