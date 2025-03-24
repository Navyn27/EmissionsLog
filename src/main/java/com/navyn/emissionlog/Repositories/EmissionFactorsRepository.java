package com.navyn.emissionlog.Repositories;

import com.navyn.emissionlog.Models.StationaryEmissionFactors;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmissionFactorsRepository extends JpaRepository<StationaryEmissionFactors, UUID> {
    StationaryEmissionFactors findByFuelId(UUID fuelId);
}
