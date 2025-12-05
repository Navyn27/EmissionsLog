package com.navyn.emissionlog.modules.transportEmissions.services;

import com.navyn.emissionlog.Enums.Transport.RegionGroup;
import com.navyn.emissionlog.Enums.Transport.TransportType;
import com.navyn.emissionlog.Enums.Transport.VehicleEngineType;
import com.navyn.emissionlog.modules.fuel.Fuel;
import com.navyn.emissionlog.modules.transportEmissions.models.TransportFuelEmissionFactors;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

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

    /**
     * Finds the best matching emission factor with wildcard support for "ANY" values.
     * Uses priority-based matching: exact matches preferred over ANY wildcards.
     * 
     * @param fuel The fuel to match
     * @param regionGroup The region group to match
     * @param transportType The transport type to match (supports ANY wildcard)
     * @param vehicleEngineType The vehicle engine type to match (supports ANY wildcard)
     * @return Optional containing the best matching emission factor, or empty if no match found
     */
    Optional<TransportFuelEmissionFactors> findBestMatchWithWildcardSupport(
            Fuel fuel, RegionGroup regionGroup, TransportType transportType, VehicleEngineType vehicleEngineType);

    /**
     * Finds all matching emission factors with wildcard support for "ANY" values.
     * Returns all factors that match the criteria, including those with ANY wildcards.
     * 
     * @param fuel The fuel to match
     * @param regionGroup The region group to match
     * @param transportType The transport type to match (supports ANY wildcard)
     * @param vehicleEngineType The vehicle engine type to match (supports ANY wildcard)
     * @return List of all matching emission factors, or empty list if no matches found
     */
    List<TransportFuelEmissionFactors> findAllMatchingWithWildcardSupport(
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
