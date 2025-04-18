package com.navyn.emissionlog.Repositories;

import com.navyn.emissionlog.Enums.RegionGroup;
import com.navyn.emissionlog.Enums.TransportType;
import com.navyn.emissionlog.Enums.VehicleEngineType;
import com.navyn.emissionlog.Models.Fuel;
import com.navyn.emissionlog.Models.TransportFuelEmissionFactors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Repository
public interface TransportFuelEmissionFactorsRepository extends JpaRepository<TransportFuelEmissionFactors, UUID> {
    List<TransportFuelEmissionFactors> findByFuel(Fuel fuel);

    List<TransportFuelEmissionFactors> findByRegionGroup(RegionGroup regionGroup);

    Optional<TransportFuelEmissionFactors> findByFuelAndRegionGroupAndTransportTypeAndVehicleEngineType(Fuel fuel, RegionGroup regionGroup, TransportType transportType, VehicleEngineType vehicleEngineType);

    List<TransportFuelEmissionFactors> findByTransportType(TransportType transportType);

    List<TransportFuelEmissionFactors> findByVehicleEngineType(VehicleEngineType vehicleEngineType);
}
