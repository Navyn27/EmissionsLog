package com.navyn.emissionlog.Repositories;

import com.navyn.emissionlog.Models.EmissionFactors;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmissionFactorsRepository extends JpaRepository<EmissionFactors, UUID> {
    EmissionFactors findByFuelId(UUID fuelId);
}
