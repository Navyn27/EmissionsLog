package com.navyn.emissionlog.modules.transportEmissions.serviceImpls;

import com.navyn.emissionlog.Enums.RegionGroup;
import com.navyn.emissionlog.Enums.TransportType;
import com.navyn.emissionlog.Enums.VehicleEngineType;
import com.navyn.emissionlog.modules.fuel.Fuel;
import com.navyn.emissionlog.modules.transportEmissions.models.TransportFuelEmissionFactors;
import com.navyn.emissionlog.modules.transportEmissions.repositories.TransportFuelEmissionFactorsRepository;
import com.navyn.emissionlog.modules.fuel.repositories.FuelRepository;
import com.navyn.emissionlog.Services.TransportFuelEmissionFactorsService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

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
