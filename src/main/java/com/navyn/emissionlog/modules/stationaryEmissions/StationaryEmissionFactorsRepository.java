package com.navyn.emissionlog.Repositories;

import com.navyn.emissionlog.modules.fuel.Fuel;
import com.navyn.emissionlog.modules.stationaryEmissions.StationaryEmissionFactors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StationaryEmissionFactorsRepository extends JpaRepository<StationaryEmissionFactors, UUID> {
    List<StationaryEmissionFactors> findByFuel(Fuel fuelId);
}
