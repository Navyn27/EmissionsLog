package com.navyn.emissionlog.modules.transportEmissions.serviceImpls;

import com.navyn.emissionlog.Enums.Transport.RegionGroup;
import com.navyn.emissionlog.Enums.Transport.TransportType;
import com.navyn.emissionlog.Enums.Transport.VehicleEngineType;
import com.navyn.emissionlog.modules.fuel.Fuel;
import com.navyn.emissionlog.modules.transportEmissions.models.TransportFuelEmissionFactors;
import com.navyn.emissionlog.modules.transportEmissions.repositories.TransportFuelEmissionFactorsRepository;
import com.navyn.emissionlog.modules.fuel.repositories.FuelRepository;
import com.navyn.emissionlog.modules.transportEmissions.services.TransportFuelEmissionFactorsService;
import com.navyn.emissionlog.utils.Specifications.TransportEmissionFactorSpecifications;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransportFuelEmissionFactorsServiceImpl implements TransportFuelEmissionFactorsService {

    private final TransportFuelEmissionFactorsRepository transportFuelEmissionFactorsRepository;
    private final FuelRepository fuelRepository;

    @Override
    public TransportFuelEmissionFactors saveTransportFuelEmissionFactors(TransportFuelEmissionFactors transportFuelEmissionFactors) {
        System.out.println(transportFuelEmissionFactors);
        if(transportFuelEmissionFactorsRepository.findByCheckSum(transportFuelEmissionFactors.getCheckSum()).isPresent()) {
            return null;
        }
        return transportFuelEmissionFactorsRepository.save(transportFuelEmissionFactors);
    }

    @Override
    public Optional<TransportFuelEmissionFactors> findById(UUID id) {
        return transportFuelEmissionFactorsRepository.findById(id);
    }

    @Override
    public List<TransportFuelEmissionFactors> findAll() {
        return transportFuelEmissionFactorsRepository.findAll();
    }

    @Override
    public List<TransportFuelEmissionFactors> findByFuel(UUID fuel) throws BadRequestException {
        Optional<Fuel> fuel1 = fuelRepository.findById(fuel);
        if(fuel1.isEmpty()){
            throw new BadRequestException("Fuel not found");
        }
        return transportFuelEmissionFactorsRepository.findByFuel(fuel1.get());
    }

    @Override
    public Optional<TransportFuelEmissionFactors> findByFuelAndRegionGroupAndTransportTypeAndVehicleEngineType(Fuel fuel, RegionGroup regionGroup, TransportType transportType, VehicleEngineType vehicleEngineType) {
        return transportFuelEmissionFactorsRepository.findByFuelAndRegionGroupAndTransportTypeAndVehicleEngineType(fuel, regionGroup, transportType, vehicleEngineType);
    }

    @Override
    public Optional<TransportFuelEmissionFactors> findBestMatchWithWildcardSupport(
            Fuel fuel, RegionGroup regionGroup, TransportType transportType, VehicleEngineType vehicleEngineType) {
        
        // Use specification to find all matching factors (including ANY wildcards)
        Specification<TransportFuelEmissionFactors> spec = 
            TransportEmissionFactorSpecifications.matchesFlexibly(fuel, regionGroup, transportType, vehicleEngineType);
        
        List<TransportFuelEmissionFactors> matches = transportFuelEmissionFactorsRepository.findAll(spec);
        
        if (matches.isEmpty()) {
            return Optional.empty();
        }
        
        // Sort by specificity: prefer exact matches over ANY wildcards
        // Higher score = more specific = preferred
        return matches.stream()
            .sorted(Comparator.comparingInt(this::calculateSpecificity).reversed())
            .findFirst();
    }

    /**
     * Calculates specificity score for an emission factor.
     * Higher score = more specific match = preferred.
     * 
     * Scoring:
     * - Exact TransportType match: +2 points
     * - Exact VehicleEngineType match: +1 point
     * - ANY values: 0 points
     * 
     * Examples:
     * - (MARINE, FOUR_STROKE) = 3 points (most specific)
     * - (MARINE, ANY) = 2 points
     * - (ANY, FOUR_STROKE) = 1 point
     * - (ANY, ANY) = 0 points (least specific, fallback)
     */
    private int calculateSpecificity(TransportFuelEmissionFactors factor) {
        int score = 0;
        
        // TransportType: exact match gets higher priority
        if (factor.getTransportType() != null && factor.getTransportType() != TransportType.ANY) {
            score += 2;
        }
        
        // VehicleEngineType: exact match gets lower priority than transport type
        if (factor.getVehicleEngineType() != null && factor.getVehicleEngineType() != VehicleEngineType.ANY) {
            score += 1;
        }
        
        return score;
    }

    @Override
    public TransportFuelEmissionFactors updateTransportFuelEmissionFactors(TransportFuelEmissionFactors transportFuelEmissionFactors) {
        return transportFuelEmissionFactorsRepository.save(transportFuelEmissionFactors);
    }

    @Override
    public void deleteById(UUID id) {
        transportFuelEmissionFactorsRepository.deleteById(id);
    }

    @Override
    public void delete(TransportFuelEmissionFactors transportFuelEmissionFactors) {
        transportFuelEmissionFactorsRepository.delete(transportFuelEmissionFactors);
    }

    @Override
    public void deleteAll() {
        transportFuelEmissionFactorsRepository.deleteAll();
    }

    @Override
    public List<TransportFuelEmissionFactors> findAllFactorsByRegionGroup(RegionGroup regionGroup) {
        return transportFuelEmissionFactorsRepository.findByRegionGroup(regionGroup);
    }

    @Override
    public List<TransportFuelEmissionFactors> findAllFactorsByTransportType(TransportType transportType) {
        return transportFuelEmissionFactorsRepository.findByTransportType(transportType);
    }

    @Override
    public List<TransportFuelEmissionFactors> findAllFactorsByVehicleEngineType(VehicleEngineType vehicleEngineType) {
        return transportFuelEmissionFactorsRepository.findByVehicleEngineType(vehicleEngineType);
    }
}
