package com.navyn.emissionlog.Repositories;

import com.navyn.emissionlog.Models.StationaryEmissionFactors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EmissionFactorsRepository extends JpaRepository<StationaryEmissionFactors, UUID> {
    StationaryEmissionFactors findByFuelId(UUID fuelId);
}
