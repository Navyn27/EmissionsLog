package com.navyn.emissionlog.modules.transportEmissions.repositories;

import com.navyn.emissionlog.Enums.Transport.RegionGroup;
import com.navyn.emissionlog.modules.fuel.Fuel;
import com.navyn.emissionlog.modules.transportEmissions.models.TransportVehicleDataEmissionFactors;
import com.navyn.emissionlog.modules.vehicles.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransportVehicleDataEmissionFactorsRepository extends JpaRepository<TransportVehicleDataEmissionFactors, UUID> {
    TransportVehicleDataEmissionFactors findByVehicleAndFuelAndRegionGroup(Vehicle vehicle, Fuel fuel, RegionGroup regionGroup);

    List<TransportVehicleDataEmissionFactors> findByFuel(Fuel fuel);
}
