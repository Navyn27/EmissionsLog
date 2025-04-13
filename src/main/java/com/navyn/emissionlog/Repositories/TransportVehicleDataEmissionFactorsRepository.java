package com.navyn.emissionlog.Repositories;

import com.navyn.emissionlog.Enums.RegionGroup;
import com.navyn.emissionlog.Models.Fuel;
import com.navyn.emissionlog.Models.TransportVehicleDataEmissionFactors;
import com.navyn.emissionlog.Models.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TransportVehicleDataEmissionFactorsRepository extends JpaRepository<TransportVehicleDataEmissionFactors, UUID> {
    TransportVehicleDataEmissionFactors findByVehicleAndFuelAndRegionGroup(Vehicle vehicle, Fuel fuel, RegionGroup regionGroup);

    List<TransportVehicleDataEmissionFactors> findByFuel(Fuel fuel);
}
