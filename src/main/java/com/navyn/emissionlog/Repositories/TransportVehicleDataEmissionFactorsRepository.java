package com.navyn.emissionlog.Repositories;

import com.navyn.emissionlog.Enums.RegionGroup;
import com.navyn.emissionlog.Models.Fuel;
import com.navyn.emissionlog.Models.TransportVehicleDataEmissionFactors;
import com.navyn.emissionlog.Models.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransportVehicleDataEmissionFactorsRepository extends JpaRepository<TransportVehicleDataEmissionFactors, UUID> {
    TransportVehicleDataEmissionFactors findByVehicleAndFuelAndRegionGroup(Vehicle vehicle, Fuel fuel, RegionGroup regionGroup);

    List<TransportVehicleDataEmissionFactors> findByFuel(Fuel fuel);
}
