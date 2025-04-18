package com.navyn.emissionlog.Services;

import com.navyn.emissionlog.Enums.RegionGroup;
import com.navyn.emissionlog.Enums.TransportType;
import com.navyn.emissionlog.Enums.VehicleEngineType;
import com.navyn.emissionlog.Models.Fuel;
import com.navyn.emissionlog.Models.TransportFuelEmissionFactors;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public interface TransportFuelEmissionFactorsService {
    TransportFuelEmissionFactors saveTransportFuelEmissionFactors(TransportFuelEmissionFactors transportFuelEmissionFactors);

    // Read
    Optional<TransportFuelEmissionFactors> findById(UUID id);
    List<TransportFuelEmissionFactors> findAll();

    // Find by criteria
    List<TransportFuelEmissionFactors> findByFuel(UUID fuel) throws BadRequestException;

    Optional<TransportFuelEmissionFactors> findByFuelAndRegionGroupAndTransportTypeAndVehicleEngineType(
            Fuel fuel, RegionGroup regionGroup, TransportType transportType, VehicleEngineType vehicleEngineType);

    // Update
    TransportFuelEmissionFactors updateTransportFuelEmissionFactors(TransportFuelEmissionFactors transportFuelEmissionFactors);

    void deleteById(UUID id);

    void delete(TransportFuelEmissionFactors transportFuelEmissionFactors);
    void deleteAll();

    List<TransportFuelEmissionFactors> findAllFactorsByRegionGroup(RegionGroup regionGroup);

    List<TransportFuelEmissionFactors> findAllFactorsByTransportType(TransportType transportType);

    List<TransportFuelEmissionFactors> findAllFactorsByVehicleEngineType(VehicleEngineType vehicleEngineType);
}
